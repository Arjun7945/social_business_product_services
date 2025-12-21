import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRemovedOrderSummary, NewRemovedOrderSummary } from '../removed-order-summary.model';

export type EntityResponseType = HttpResponse<IRemovedOrderSummary>;
export type EntityArrayResponseType = HttpResponse<IRemovedOrderSummary[]>;

@Injectable({ providedIn: 'root' })
export class RemovedOrderSummaryService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/removed-order-summaries');

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestRemovedOrderSummary>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRemovedOrderSummary[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getRemovedOrderSummaryIdentifier(removedOrderSummary: Pick<IRemovedOrderSummary, 'id'>): number {
    return removedOrderSummary.id;
  }

  compareRemovedOrderSummary(o1: Pick<IRemovedOrderSummary, 'id'> | null, o2: Pick<IRemovedOrderSummary, 'id'> | null): boolean {
    return o1 && o2 ? this.getRemovedOrderSummaryIdentifier(o1) === this.getRemovedOrderSummaryIdentifier(o2) : o1 === o2;
  }

  protected convertResponseFromServer(res: HttpResponse<RestRemovedOrderSummary>): HttpResponse<IRemovedOrderSummary> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestRemovedOrderSummary[]>): HttpResponse<IRemovedOrderSummary[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }

  protected convertDateFromServer(restRemovedOrderSummary: RestRemovedOrderSummary): IRemovedOrderSummary {
    return {
      ...restRemovedOrderSummary,
      firstInteractionAt: restRemovedOrderSummary.firstInteractionAt ? dayjs(restRemovedOrderSummary.firstInteractionAt) : null,
      lastInteractionAt: restRemovedOrderSummary.lastInteractionAt ? dayjs(restRemovedOrderSummary.lastInteractionAt) : null,
      removedAt: restRemovedOrderSummary.removedAt ? dayjs(restRemovedOrderSummary.removedAt) : null,
    };
  }
}

type RestOf<T extends IRemovedOrderSummary | NewRemovedOrderSummary> = Omit<T, 'firstInteractionAt' | 'lastInteractionAt' | 'removedAt'> & {
  firstInteractionAt?: string | null;
  lastInteractionAt?: string | null;
  removedAt?: string | null;
};

export type RestRemovedOrderSummary = RestOf<IRemovedOrderSummary>;
export type NewRestRemovedOrderSummary = RestOf<NewRemovedOrderSummary>;
