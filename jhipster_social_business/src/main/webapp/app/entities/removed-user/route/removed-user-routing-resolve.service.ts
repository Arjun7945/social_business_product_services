import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRemovedUser } from '../removed-user.model';
import { RemovedUserService } from '../service/removed-user.service';

const removedUserResolve = (route: ActivatedRouteSnapshot): Observable<null | IRemovedUser> => {
  const id = route.params.id;
  if (id) {
    return inject(RemovedUserService)
      .find(id)
      .pipe(
        mergeMap((removedUser: HttpResponse<IRemovedUser>) => {
          if (removedUser.body) {
            return of(removedUser.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default removedUserResolve;
