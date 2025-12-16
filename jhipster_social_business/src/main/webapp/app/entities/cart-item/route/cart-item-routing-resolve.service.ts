import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICartItem } from '../cart-item.model';
import { CartItemService } from '../service/cart-item.service';

const cartItemResolve = (route: ActivatedRouteSnapshot): Observable<null | ICartItem> => {
  const id = route.params.id;
  if (id) {
    return inject(CartItemService)
      .find(id)
      .pipe(
        mergeMap((cartItem: HttpResponse<ICartItem>) => {
          if (cartItem.body) {
            return of(cartItem.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default cartItemResolve;
