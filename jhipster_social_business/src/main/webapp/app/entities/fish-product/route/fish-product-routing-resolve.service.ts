import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFishProduct } from '../fish-product.model';
import { FishProductService } from '../service/fish-product.service';

const fishProductResolve = (route: ActivatedRouteSnapshot): Observable<null | IFishProduct> => {
  const id = route.params.id;
  if (id) {
    return inject(FishProductService)
      .find(id)
      .pipe(
        mergeMap((fishProduct: HttpResponse<IFishProduct>) => {
          if (fishProduct.body) {
            return of(fishProduct.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default fishProductResolve;
