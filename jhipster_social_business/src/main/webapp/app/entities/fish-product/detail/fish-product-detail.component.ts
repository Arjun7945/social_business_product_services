import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IFishProduct } from '../fish-product.model';

@Component({
  selector: 'jhi-fish-product-detail',
  templateUrl: './fish-product-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class FishProductDetailComponent {
  fishProduct = input<IFishProduct | null>(null);

  previousState(): void {
    window.history.back();
  }
}
