import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRemovedUser, NewRemovedUser } from '../removed-user.model';

export type EntityResponseType = HttpResponse<IRemovedUser>;
export type EntityArrayResponseType = HttpResponse<IRemovedUser[]>;

@Injectable({ providedIn: 'root' })
export class RemovedUserService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/removed-users');

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestRemovedUser>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRemovedUser[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  restore(id: number): Observable<HttpResponse<number>> {
    return this.http.post<number>(`${this.resourceUrl}/${id}/restore`, {}, { observe: 'response' });
  }

  getRemovedUserIdentifier(removedUser: Pick<IRemovedUser, 'id'>): number {
    return removedUser.id;
  }

  compareRemovedUser(o1: Pick<IRemovedUser, 'id'> | null, o2: Pick<IRemovedUser, 'id'> | null): boolean {
    return o1 && o2 ? this.getRemovedUserIdentifier(o1) === this.getRemovedUserIdentifier(o2) : o1 === o2;
  }

  protected convertResponseFromServer(res: HttpResponse<RestRemovedUser>): HttpResponse<IRemovedUser> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestRemovedUser[]>): HttpResponse<IRemovedUser[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }

  protected convertDateFromServer(restRemovedUser: RestRemovedUser): IRemovedUser {
    return {
      ...restRemovedUser,
      joinedAt: restRemovedUser.joinedAt ? dayjs(restRemovedUser.joinedAt) : null,
      removedAt: restRemovedUser.removedAt ? dayjs(restRemovedUser.removedAt) : null,
    };
  }
}

type RestOf<T extends IRemovedUser | NewRemovedUser> = Omit<T, 'joinedAt' | 'removedAt'> & {
  joinedAt?: string | null;
  removedAt?: string | null;
};

export type RestRemovedUser = RestOf<IRemovedUser>;
export type NewRestRemovedUser = RestOf<NewRemovedUser>;
