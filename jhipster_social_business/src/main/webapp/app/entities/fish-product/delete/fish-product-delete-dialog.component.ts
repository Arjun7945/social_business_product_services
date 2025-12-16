import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IFishProduct } from '../fish-product.model';
import { FishProductService } from '../service/fish-product.service';

@Component({
  templateUrl: './fish-product-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class FishProductDeleteDialogComponent {
  fishProduct?: IFishProduct;

  protected fishProductService = inject(FishProductService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.fishProductService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
