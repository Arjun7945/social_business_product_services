import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICartItem } from '../cart-item.model';
import { CartItemService } from '../service/cart-item.service';

@Component({
  templateUrl: './cart-item-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CartItemDeleteDialogComponent {
  cartItem?: ICartItem;

  protected cartItemService = inject(CartItemService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cartItemService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
