import { Component, inject, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import dayjs from 'dayjs/esm';
import { Duration } from 'dayjs/plugin/duration';
import relativeTime from 'dayjs/plugin/relativeTime';

import { ICustomerOrder } from '../customer-order.model';
import { OrderTrackingService } from './order-tracking.service';
import { IOrderStatusHistory } from './order-status-history.model';

dayjs.extend(relativeTime);

@Component({
  standalone: true,
  selector: 'jhi-order-tracking-dialog',
  templateUrl: './order-tracking-dialog.component.html',
  styleUrls: ['./order-tracking-dialog.component.scss'],
  imports: [CommonModule, SharedModule, FormatMediumDatetimePipe],
})
export class OrderTrackingDialogComponent implements OnInit {
  customerOrder?: ICustomerOrder;
  history: IOrderStatusHistory[] = [];
  activeModal = inject(NgbActiveModal);
  trackingService = inject(OrderTrackingService);
  steps: any[] = [];
  totalDuration = '';

  ngOnInit(): void {
    if (this.customerOrder?.id) {
      this.loadHistory();
    }
  }

  loadHistory(): void {
    this.trackingService.getHistoryByOrder(this.customerOrder!.id).subscribe((res: HttpResponse<IOrderStatusHistory[]>) => {
      this.history = res.body ?? [];
      this.calculateSteps();
    });
  }

  calculateSteps(): void {
    // Sort history by time
    const sortedProps = this.history.sort((a, b) => dayjs(a.changeTime).diff(dayjs(b.changeTime)));

    // Add Order Placed time as the start if available
    const timelineNodes = [...sortedProps];

    this.steps = [];

    // Iterate up to the second to last element
    for (let i = 0; i < timelineNodes.length - 1; i++) {
      const current = timelineNodes[i];
      const next = timelineNodes[i + 1];

      const diff = dayjs(next.changeTime).diff(dayjs(current.changeTime));
      const duration = this.formatDuration(diff);

      this.steps.push({
        status: current.status,
        time: current.changeTime,
        duration,
        isCompleted: true,
      });
    }

    // Handle the last step (Current Stage)
    if (timelineNodes.length > 0) {
      const last = timelineNodes[timelineNodes.length - 1];
      this.steps.push({
        status: last.status,
        time: last.changeTime,
        duration: 'Current Stage',
        isCompleted: true,
      });
    }

    // If order is delivered, calculate total time
    if (this.customerOrder?.orderTime) {
      const lastTime = timelineNodes.length > 0 ? timelineNodes[timelineNodes.length - 1].changeTime : this.customerOrder.orderTime;
      // Total time from Order Placement to Last Update
      const totalDiff = dayjs(lastTime).diff(dayjs(this.customerOrder.orderTime));
      this.totalDuration = this.formatDuration(totalDiff);
    }
  }

  formatDuration(ms: number): string {
    const seconds = Math.floor((ms / 1000) % 60);
    const minutes = Math.floor((ms / (1000 * 60)) % 60);
    const hours = Math.floor((ms / (1000 * 60 * 60)) % 24);
    const days = Math.floor(ms / (1000 * 60 * 60 * 24));

    const parts = [];
    if (days > 0) parts.push(`${days} Days`);
    if (hours > 0) parts.push(`${hours} Hours`);
    if (minutes > 0) parts.push(`${minutes} Mins`);
    if (seconds > 0 && parts.length === 0) parts.push(`${seconds} Secs`);

    return parts.length > 0 ? parts.join(' ') : '0 Mins';
  }

  cancel(): void {
    this.activeModal.dismiss();
  }
}
