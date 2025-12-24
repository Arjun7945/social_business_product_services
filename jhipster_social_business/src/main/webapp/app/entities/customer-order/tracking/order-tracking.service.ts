import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import dayjs from 'dayjs/esm';
import { IOrderStatusHistory } from './order-status-history.model';

type RestOf<T extends IOrderStatusHistory> = Omit<T, 'changeTime'> & {
  changeTime?: string | null;
};

export type RestOrderStatusHistory = RestOf<IOrderStatusHistory>;

@Injectable({ providedIn: 'root' })
export class OrderTrackingService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/order-status-histories');

  getHistoryByOrder(orderId: number): Observable<HttpResponse<IOrderStatusHistory[]>> {
    return this.http
      .get<RestOrderStatusHistory[]>(`${this.resourceUrl}/order/${orderId}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  protected convertDateFromServer(restOrderStatusHistory: RestOrderStatusHistory): IOrderStatusHistory {
    return {
      ...restOrderStatusHistory,
      changeTime: restOrderStatusHistory.changeTime ? dayjs(restOrderStatusHistory.changeTime) : undefined,
    };
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestOrderStatusHistory[]>): HttpResponse<IOrderStatusHistory[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
