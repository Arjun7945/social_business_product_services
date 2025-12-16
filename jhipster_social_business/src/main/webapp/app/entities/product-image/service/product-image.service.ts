import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProductImage, NewProductImage } from '../product-image.model';

export type PartialUpdateProductImage = Partial<IProductImage> & Pick<IProductImage, 'id'>;

export type EntityResponseType = HttpResponse<IProductImage>;
export type EntityArrayResponseType = HttpResponse<IProductImage[]>;

@Injectable({ providedIn: 'root' })
export class ProductImageService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/product-images');

  create(productImage: NewProductImage): Observable<EntityResponseType> {
    return this.http.post<IProductImage>(this.resourceUrl, productImage, { observe: 'response' });
  }

  update(productImage: IProductImage): Observable<EntityResponseType> {
    return this.http.put<IProductImage>(`${this.resourceUrl}/${this.getProductImageIdentifier(productImage)}`, productImage, {
      observe: 'response',
    });
  }

  partialUpdate(productImage: PartialUpdateProductImage): Observable<EntityResponseType> {
    return this.http.patch<IProductImage>(`${this.resourceUrl}/${this.getProductImageIdentifier(productImage)}`, productImage, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProductImage>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductImage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProductImageIdentifier(productImage: Pick<IProductImage, 'id'>): number {
    return productImage.id;
  }

  compareProductImage(o1: Pick<IProductImage, 'id'> | null, o2: Pick<IProductImage, 'id'> | null): boolean {
    return o1 && o2 ? this.getProductImageIdentifier(o1) === this.getProductImageIdentifier(o2) : o1 === o2;
  }

  addProductImageToCollectionIfMissing<Type extends Pick<IProductImage, 'id'>>(
    productImageCollection: Type[],
    ...productImagesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const productImages: Type[] = productImagesToCheck.filter(isPresent);
    if (productImages.length > 0) {
      const productImageCollectionIdentifiers = productImageCollection.map(productImageItem =>
        this.getProductImageIdentifier(productImageItem),
      );
      const productImagesToAdd = productImages.filter(productImageItem => {
        const productImageIdentifier = this.getProductImageIdentifier(productImageItem);
        if (productImageCollectionIdentifiers.includes(productImageIdentifier)) {
          return false;
        }
        productImageCollectionIdentifiers.push(productImageIdentifier);
        return true;
      });
      return [...productImagesToAdd, ...productImageCollection];
    }
    return productImageCollection;
  }
}
