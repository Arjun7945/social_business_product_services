import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICustomerOrder } from '../customer-order.model';
import { CustomerOrderService } from '../service/customer-order.service';

const customerOrderResolve = (route: ActivatedRouteSnapshot): Observable<null | ICustomerOrder> => {
  const id = route.params.id;
  if (id) {
    return inject(CustomerOrderService)
      .find(id)
      .pipe(
        mergeMap((customerOrder: HttpResponse<ICustomerOrder>) => {
          if (customerOrder.body) {
            return of(customerOrder.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default customerOrderResolve;
