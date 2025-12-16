import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IShoppingCart } from '../shopping-cart.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../shopping-cart.test-samples';

import { RestShoppingCart, ShoppingCartService } from './shopping-cart.service';

const requireRestSample: RestShoppingCart = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
  updatedAt: sampleWithRequiredData.updatedAt?.toJSON(),
};

describe('ShoppingCart Service', () => {
  let service: ShoppingCartService;
  let httpMock: HttpTestingController;
  let expectedResult: IShoppingCart | IShoppingCart[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ShoppingCartService);
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

    it('should create a ShoppingCart', () => {
      const shoppingCart = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(shoppingCart).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ShoppingCart', () => {
      const shoppingCart = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(shoppingCart).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ShoppingCart', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ShoppingCart', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ShoppingCart', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addShoppingCartToCollectionIfMissing', () => {
      it('should add a ShoppingCart to an empty array', () => {
        const shoppingCart: IShoppingCart = sampleWithRequiredData;
        expectedResult = service.addShoppingCartToCollectionIfMissing([], shoppingCart);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(shoppingCart);
      });

      it('should not add a ShoppingCart to an array that contains it', () => {
        const shoppingCart: IShoppingCart = sampleWithRequiredData;
        const shoppingCartCollection: IShoppingCart[] = [
          {
            ...shoppingCart,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addShoppingCartToCollectionIfMissing(shoppingCartCollection, shoppingCart);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ShoppingCart to an array that doesn't contain it", () => {
        const shoppingCart: IShoppingCart = sampleWithRequiredData;
        const shoppingCartCollection: IShoppingCart[] = [sampleWithPartialData];
        expectedResult = service.addShoppingCartToCollectionIfMissing(shoppingCartCollection, shoppingCart);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(shoppingCart);
      });

      it('should add only unique ShoppingCart to an array', () => {
        const shoppingCartArray: IShoppingCart[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const shoppingCartCollection: IShoppingCart[] = [sampleWithRequiredData];
        expectedResult = service.addShoppingCartToCollectionIfMissing(shoppingCartCollection, ...shoppingCartArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const shoppingCart: IShoppingCart = sampleWithRequiredData;
        const shoppingCart2: IShoppingCart = sampleWithPartialData;
        expectedResult = service.addShoppingCartToCollectionIfMissing([], shoppingCart, shoppingCart2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(shoppingCart);
        expect(expectedResult).toContain(shoppingCart2);
      });

      it('should accept null and undefined values', () => {
        const shoppingCart: IShoppingCart = sampleWithRequiredData;
        expectedResult = service.addShoppingCartToCollectionIfMissing([], null, shoppingCart, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(shoppingCart);
      });

      it('should return initial array if no ShoppingCart is added', () => {
        const shoppingCartCollection: IShoppingCart[] = [sampleWithRequiredData];
        expectedResult = service.addShoppingCartToCollectionIfMissing(shoppingCartCollection, undefined, null);
        expect(expectedResult).toEqual(shoppingCartCollection);
      });
    });

    describe('compareShoppingCart', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareShoppingCart(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 21091 };
        const entity2 = null;

        const compareResult1 = service.compareShoppingCart(entity1, entity2);
        const compareResult2 = service.compareShoppingCart(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 21091 };
        const entity2 = { id: 19091 };

        const compareResult1 = service.compareShoppingCart(entity1, entity2);
        const compareResult2 = service.compareShoppingCart(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 21091 };
        const entity2 = { id: 21091 };

        const compareResult1 = service.compareShoppingCart(entity1, entity2);
        const compareResult2 = service.compareShoppingCart(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
