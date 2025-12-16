import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITeamMember } from '../team-member.model';
import { TeamMemberService } from '../service/team-member.service';

const teamMemberResolve = (route: ActivatedRouteSnapshot): Observable<null | ITeamMember> => {
  const id = route.params.id;
  if (id) {
    return inject(TeamMemberService)
      .find(id)
      .pipe(
        mergeMap((teamMember: HttpResponse<ITeamMember>) => {
          if (teamMember.body) {
            return of(teamMember.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default teamMemberResolve;
