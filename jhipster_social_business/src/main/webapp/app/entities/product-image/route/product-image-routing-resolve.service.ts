import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProductImage } from '../product-image.model';
import { ProductImageService } from '../service/product-image.service';

const productImageResolve = (route: ActivatedRouteSnapshot): Observable<null | IProductImage> => {
  const id = route.params.id;
  if (id) {
    return inject(ProductImageService)
      .find(id)
      .pipe(
        mergeMap((productImage: HttpResponse<IProductImage>) => {
          if (productImage.body) {
            return of(productImage.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default productImageResolve;
