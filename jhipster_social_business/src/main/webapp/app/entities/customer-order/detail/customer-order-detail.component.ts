import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ICustomerOrder } from '../customer-order.model';

@Component({
  selector: 'jhi-customer-order-detail',
  templateUrl: './customer-order-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class CustomerOrderDetailComponent {
  customerOrder = input<ICustomerOrder | null>(null);

  previousState(): void {
    window.history.back();
  }
}
