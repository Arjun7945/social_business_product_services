import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICustomerOrder, NewCustomerOrder } from '../customer-order.model';

export type PartialUpdateCustomerOrder = Partial<ICustomerOrder> & Pick<ICustomerOrder, 'id'>;

type RestOf<T extends ICustomerOrder | NewCustomerOrder> = Omit<T, 'orderTime' | 'confirmedAt'> & {
  orderTime?: string | null;
  confirmedAt?: string | null;
};

export type RestCustomerOrder = RestOf<ICustomerOrder>;

export type NewRestCustomerOrder = RestOf<NewCustomerOrder>;

export type PartialUpdateRestCustomerOrder = RestOf<PartialUpdateCustomerOrder>;

export type EntityResponseType = HttpResponse<ICustomerOrder>;
export type EntityArrayResponseType = HttpResponse<ICustomerOrder[]>;

@Injectable({ providedIn: 'root' })
export class CustomerOrderService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/customer-orders');

  create(customerOrder: NewCustomerOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(customerOrder);
    return this.http
      .post<RestCustomerOrder>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(customerOrder: ICustomerOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(customerOrder);
    return this.http
      .put<RestCustomerOrder>(`${this.resourceUrl}/${this.getCustomerOrderIdentifier(customerOrder)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(customerOrder: PartialUpdateCustomerOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(customerOrder);
    return this.http
      .patch<RestCustomerOrder>(`${this.resourceUrl}/${this.getCustomerOrderIdentifier(customerOrder)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCustomerOrder>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCustomerOrder[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCustomerOrderIdentifier(customerOrder: Pick<ICustomerOrder, 'id'>): number {
    return customerOrder.id;
  }

  compareCustomerOrder(o1: Pick<ICustomerOrder, 'id'> | null, o2: Pick<ICustomerOrder, 'id'> | null): boolean {
    return o1 && o2 ? this.getCustomerOrderIdentifier(o1) === this.getCustomerOrderIdentifier(o2) : o1 === o2;
  }

  addCustomerOrderToCollectionIfMissing<Type extends Pick<ICustomerOrder, 'id'>>(
    customerOrderCollection: Type[],
    ...customerOrdersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const customerOrders: Type[] = customerOrdersToCheck.filter(isPresent);
    if (customerOrders.length > 0) {
      const customerOrderCollectionIdentifiers = customerOrderCollection.map(customerOrderItem =>
        this.getCustomerOrderIdentifier(customerOrderItem),
      );
      const customerOrdersToAdd = customerOrders.filter(customerOrderItem => {
        const customerOrderIdentifier = this.getCustomerOrderIdentifier(customerOrderItem);
        if (customerOrderCollectionIdentifiers.includes(customerOrderIdentifier)) {
          return false;
        }
        customerOrderCollectionIdentifiers.push(customerOrderIdentifier);
        return true;
      });
      return [...customerOrdersToAdd, ...customerOrderCollection];
    }
    return customerOrderCollection;
  }

  protected convertDateFromClient<T extends ICustomerOrder | NewCustomerOrder | PartialUpdateCustomerOrder>(customerOrder: T): RestOf<T> {
    return {
      ...customerOrder,
      orderTime: customerOrder.orderTime?.toJSON() ?? null,
      confirmedAt: customerOrder.confirmedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCustomerOrder: RestCustomerOrder): ICustomerOrder {
    return {
      ...restCustomerOrder,
      orderTime: restCustomerOrder.orderTime ? dayjs(restCustomerOrder.orderTime) : undefined,
      confirmedAt: restCustomerOrder.confirmedAt ? dayjs(restCustomerOrder.confirmedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCustomerOrder>): HttpResponse<ICustomerOrder> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCustomerOrder[]>): HttpResponse<ICustomerOrder[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
