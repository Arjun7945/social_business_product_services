import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ICartItem } from '../cart-item.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../cart-item.test-samples';

import { CartItemService, RestCartItem } from './cart-item.service';

const requireRestSample: RestCartItem = {
  ...sampleWithRequiredData,
  addedAt: sampleWithRequiredData.addedAt?.toJSON(),
};

describe('CartItem Service', () => {
  let service: CartItemService;
  let httpMock: HttpTestingController;
  let expectedResult: ICartItem | ICartItem[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CartItemService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a CartItem', () => {
      const cartItem = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(cartItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CartItem', () => {
      const cartItem = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(cartItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CartItem', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CartItem', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CartItem', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCartItemToCollectionIfMissing', () => {
      it('should add a CartItem to an empty array', () => {
        const cartItem: ICartItem = sampleWithRequiredData;
        expectedResult = service.addCartItemToCollectionIfMissing([], cartItem);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cartItem);
      });

      it('should not add a CartItem to an array that contains it', () => {
        const cartItem: ICartItem = sampleWithRequiredData;
        const cartItemCollection: ICartItem[] = [
          {
            ...cartItem,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCartItemToCollectionIfMissing(cartItemCollection, cartItem);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CartItem to an array that doesn't contain it", () => {
        const cartItem: ICartItem = sampleWithRequiredData;
        const cartItemCollection: ICartItem[] = [sampleWithPartialData];
        expectedResult = service.addCartItemToCollectionIfMissing(cartItemCollection, cartItem);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cartItem);
      });

      it('should add only unique CartItem to an array', () => {
        const cartItemArray: ICartItem[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const cartItemCollection: ICartItem[] = [sampleWithRequiredData];
        expectedResult = service.addCartItemToCollectionIfMissing(cartItemCollection, ...cartItemArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cartItem: ICartItem = sampleWithRequiredData;
        const cartItem2: ICartItem = sampleWithPartialData;
        expectedResult = service.addCartItemToCollectionIfMissing([], cartItem, cartItem2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cartItem);
        expect(expectedResult).toContain(cartItem2);
      });

      it('should accept null and undefined values', () => {
        const cartItem: ICartItem = sampleWithRequiredData;
        expectedResult = service.addCartItemToCollectionIfMissing([], null, cartItem, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cartItem);
      });

      it('should return initial array if no CartItem is added', () => {
        const cartItemCollection: ICartItem[] = [sampleWithRequiredData];
        expectedResult = service.addCartItemToCollectionIfMissing(cartItemCollection, undefined, null);
        expect(expectedResult).toEqual(cartItemCollection);
      });
    });

    describe('compareCartItem', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCartItem(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 2227 };
        const entity2 = null;

        const compareResult1 = service.compareCartItem(entity1, entity2);
        const compareResult2 = service.compareCartItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 2227 };
        const entity2 = { id: 7701 };

        const compareResult1 = service.compareCartItem(entity1, entity2);
        const compareResult2 = service.compareCartItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 2227 };
        const entity2 = { id: 2227 };

        const compareResult1 = service.compareCartItem(entity1, entity2);
        const compareResult2 = service.compareCartItem(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
