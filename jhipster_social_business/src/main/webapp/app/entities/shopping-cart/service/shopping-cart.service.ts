import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IShoppingCart, NewShoppingCart } from '../shopping-cart.model';

export type PartialUpdateShoppingCart = Partial<IShoppingCart> & Pick<IShoppingCart, 'id'>;

type RestOf<T extends IShoppingCart | NewShoppingCart> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

export type RestShoppingCart = RestOf<IShoppingCart>;

export type NewRestShoppingCart = RestOf<NewShoppingCart>;

export type PartialUpdateRestShoppingCart = RestOf<PartialUpdateShoppingCart>;

export type EntityResponseType = HttpResponse<IShoppingCart>;
export type EntityArrayResponseType = HttpResponse<IShoppingCart[]>;

@Injectable({ providedIn: 'root' })
export class ShoppingCartService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/shopping-carts');

  create(shoppingCart: NewShoppingCart): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shoppingCart);
    return this.http
      .post<RestShoppingCart>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(shoppingCart: IShoppingCart): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shoppingCart);
    return this.http
      .put<RestShoppingCart>(`${this.resourceUrl}/${this.getShoppingCartIdentifier(shoppingCart)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(shoppingCart: PartialUpdateShoppingCart): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shoppingCart);
    return this.http
      .patch<RestShoppingCart>(`${this.resourceUrl}/${this.getShoppingCartIdentifier(shoppingCart)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestShoppingCart>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestShoppingCart[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getShoppingCartIdentifier(shoppingCart: Pick<IShoppingCart, 'id'>): number {
    return shoppingCart.id;
  }

  compareShoppingCart(o1: Pick<IShoppingCart, 'id'> | null, o2: Pick<IShoppingCart, 'id'> | null): boolean {
    return o1 && o2 ? this.getShoppingCartIdentifier(o1) === this.getShoppingCartIdentifier(o2) : o1 === o2;
  }

  addShoppingCartToCollectionIfMissing<Type extends Pick<IShoppingCart, 'id'>>(
    shoppingCartCollection: Type[],
    ...shoppingCartsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const shoppingCarts: Type[] = shoppingCartsToCheck.filter(isPresent);
    if (shoppingCarts.length > 0) {
      const shoppingCartCollectionIdentifiers = shoppingCartCollection.map(shoppingCartItem =>
        this.getShoppingCartIdentifier(shoppingCartItem),
      );
      const shoppingCartsToAdd = shoppingCarts.filter(shoppingCartItem => {
        const shoppingCartIdentifier = this.getShoppingCartIdentifier(shoppingCartItem);
        if (shoppingCartCollectionIdentifiers.includes(shoppingCartIdentifier)) {
          return false;
        }
        shoppingCartCollectionIdentifiers.push(shoppingCartIdentifier);
        return true;
      });
      return [...shoppingCartsToAdd, ...shoppingCartCollection];
    }
    return shoppingCartCollection;
  }

  protected convertDateFromClient<T extends IShoppingCart | NewShoppingCart | PartialUpdateShoppingCart>(shoppingCart: T): RestOf<T> {
    return {
      ...shoppingCart,
      createdAt: shoppingCart.createdAt?.toJSON() ?? null,
      updatedAt: shoppingCart.updatedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restShoppingCart: RestShoppingCart): IShoppingCart {
    return {
      ...restShoppingCart,
      createdAt: restShoppingCart.createdAt ? dayjs(restShoppingCart.createdAt) : undefined,
      updatedAt: restShoppingCart.updatedAt ? dayjs(restShoppingCart.updatedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestShoppingCart>): HttpResponse<IShoppingCart> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestShoppingCart[]>): HttpResponse<IShoppingCart[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
