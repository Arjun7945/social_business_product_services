import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBotSession } from '../bot-session.model';
import { BotSessionService } from '../service/bot-session.service';

const botSessionResolve = (route: ActivatedRouteSnapshot): Observable<null | IBotSession> => {
  const id = route.params.id;
  if (id) {
    return inject(BotSessionService)
      .find(id)
      .pipe(
        mergeMap((botSession: HttpResponse<IBotSession>) => {
          if (botSession.body) {
            return of(botSession.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default botSessionResolve;
