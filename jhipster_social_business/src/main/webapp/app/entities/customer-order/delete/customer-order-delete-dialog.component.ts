import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICustomerOrder } from '../customer-order.model';
import { CustomerOrderService } from '../service/customer-order.service';

@Component({
  templateUrl: './customer-order-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CustomerOrderDeleteDialogComponent {
  customerOrder?: ICustomerOrder;

  protected customerOrderService = inject(CustomerOrderService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.customerOrderService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
