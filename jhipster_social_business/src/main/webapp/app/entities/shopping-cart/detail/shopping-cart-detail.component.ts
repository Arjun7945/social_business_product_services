import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IShoppingCart } from '../shopping-cart.model';

@Component({
  selector: 'jhi-shopping-cart-detail',
  templateUrl: './shopping-cart-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class ShoppingCartDetailComponent {
  shoppingCart = input<IShoppingCart | null>(null);

  previousState(): void {
    window.history.back();
  }
}
