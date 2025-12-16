import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IProductImage } from '../product-image.model';
import { ProductImageService } from '../service/product-image.service';

@Component({
  templateUrl: './product-image-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ProductImageDeleteDialogComponent {
  productImage?: IProductImage;

  protected productImageService = inject(ProductImageService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.productImageService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
