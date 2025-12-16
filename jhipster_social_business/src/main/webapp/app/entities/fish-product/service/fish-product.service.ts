import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFishProduct, NewFishProduct } from '../fish-product.model';

export type PartialUpdateFishProduct = Partial<IFishProduct> & Pick<IFishProduct, 'id'>;

type RestOf<T extends IFishProduct | NewFishProduct> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestFishProduct = RestOf<IFishProduct>;

export type NewRestFishProduct = RestOf<NewFishProduct>;

export type PartialUpdateRestFishProduct = RestOf<PartialUpdateFishProduct>;

export type EntityResponseType = HttpResponse<IFishProduct>;
export type EntityArrayResponseType = HttpResponse<IFishProduct[]>;

@Injectable({ providedIn: 'root' })
export class FishProductService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/fish-products');

  create(fishProduct: NewFishProduct): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(fishProduct);
    return this.http
      .post<RestFishProduct>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(fishProduct: IFishProduct): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(fishProduct);
    return this.http
      .put<RestFishProduct>(`${this.resourceUrl}/${this.getFishProductIdentifier(fishProduct)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(fishProduct: PartialUpdateFishProduct): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(fishProduct);
    return this.http
      .patch<RestFishProduct>(`${this.resourceUrl}/${this.getFishProductIdentifier(fishProduct)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestFishProduct>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestFishProduct[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFishProductIdentifier(fishProduct: Pick<IFishProduct, 'id'>): number {
    return fishProduct.id;
  }

  compareFishProduct(o1: Pick<IFishProduct, 'id'> | null, o2: Pick<IFishProduct, 'id'> | null): boolean {
    return o1 && o2 ? this.getFishProductIdentifier(o1) === this.getFishProductIdentifier(o2) : o1 === o2;
  }

  addFishProductToCollectionIfMissing<Type extends Pick<IFishProduct, 'id'>>(
    fishProductCollection: Type[],
    ...fishProductsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const fishProducts: Type[] = fishProductsToCheck.filter(isPresent);
    if (fishProducts.length > 0) {
      const fishProductCollectionIdentifiers = fishProductCollection.map(fishProductItem => this.getFishProductIdentifier(fishProductItem));
      const fishProductsToAdd = fishProducts.filter(fishProductItem => {
        const fishProductIdentifier = this.getFishProductIdentifier(fishProductItem);
        if (fishProductCollectionIdentifiers.includes(fishProductIdentifier)) {
          return false;
        }
        fishProductCollectionIdentifiers.push(fishProductIdentifier);
        return true;
      });
      return [...fishProductsToAdd, ...fishProductCollection];
    }
    return fishProductCollection;
  }

  protected convertDateFromClient<T extends IFishProduct | NewFishProduct | PartialUpdateFishProduct>(fishProduct: T): RestOf<T> {
    return {
      ...fishProduct,
      createdAt: fishProduct.createdAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restFishProduct: RestFishProduct): IFishProduct {
    return {
      ...restFishProduct,
      createdAt: restFishProduct.createdAt ? dayjs(restFishProduct.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestFishProduct>): HttpResponse<IFishProduct> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestFishProduct[]>): HttpResponse<IFishProduct[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
