import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRemovedOrderSummary } from '../removed-order-summary.model';
import { RemovedOrderSummaryService } from '../service/removed-order-summary.service';

const removedOrderSummaryResolve = (route: ActivatedRouteSnapshot): Observable<null | IRemovedOrderSummary> => {
  const id = route.params.id;
  if (id) {
    return inject(RemovedOrderSummaryService)
      .find(id)
      .pipe(
        mergeMap((removedOrderSummary: HttpResponse<IRemovedOrderSummary>) => {
          if (removedOrderSummary.body) {
            return of(removedOrderSummary.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default removedOrderSummaryResolve;
