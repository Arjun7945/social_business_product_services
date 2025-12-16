import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IFishProduct } from '../fish-product.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../fish-product.test-samples';

import { FishProductService, RestFishProduct } from './fish-product.service';

const requireRestSample: RestFishProduct = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
};

describe('FishProduct Service', () => {
  let service: FishProductService;
  let httpMock: HttpTestingController;
  let expectedResult: IFishProduct | IFishProduct[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(FishProductService);
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

    it('should create a FishProduct', () => {
      const fishProduct = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(fishProduct).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a FishProduct', () => {
      const fishProduct = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(fishProduct).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a FishProduct', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of FishProduct', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a FishProduct', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addFishProductToCollectionIfMissing', () => {
      it('should add a FishProduct to an empty array', () => {
        const fishProduct: IFishProduct = sampleWithRequiredData;
        expectedResult = service.addFishProductToCollectionIfMissing([], fishProduct);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(fishProduct);
      });

      it('should not add a FishProduct to an array that contains it', () => {
        const fishProduct: IFishProduct = sampleWithRequiredData;
        const fishProductCollection: IFishProduct[] = [
          {
            ...fishProduct,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFishProductToCollectionIfMissing(fishProductCollection, fishProduct);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a FishProduct to an array that doesn't contain it", () => {
        const fishProduct: IFishProduct = sampleWithRequiredData;
        const fishProductCollection: IFishProduct[] = [sampleWithPartialData];
        expectedResult = service.addFishProductToCollectionIfMissing(fishProductCollection, fishProduct);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(fishProduct);
      });

      it('should add only unique FishProduct to an array', () => {
        const fishProductArray: IFishProduct[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const fishProductCollection: IFishProduct[] = [sampleWithRequiredData];
        expectedResult = service.addFishProductToCollectionIfMissing(fishProductCollection, ...fishProductArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const fishProduct: IFishProduct = sampleWithRequiredData;
        const fishProduct2: IFishProduct = sampleWithPartialData;
        expectedResult = service.addFishProductToCollectionIfMissing([], fishProduct, fishProduct2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(fishProduct);
        expect(expectedResult).toContain(fishProduct2);
      });

      it('should accept null and undefined values', () => {
        const fishProduct: IFishProduct = sampleWithRequiredData;
        expectedResult = service.addFishProductToCollectionIfMissing([], null, fishProduct, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(fishProduct);
      });

      it('should return initial array if no FishProduct is added', () => {
        const fishProductCollection: IFishProduct[] = [sampleWithRequiredData];
        expectedResult = service.addFishProductToCollectionIfMissing(fishProductCollection, undefined, null);
        expect(expectedResult).toEqual(fishProductCollection);
      });
    });

    describe('compareFishProduct', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFishProduct(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 7384 };
        const entity2 = null;

        const compareResult1 = service.compareFishProduct(entity1, entity2);
        const compareResult2 = service.compareFishProduct(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 7384 };
        const entity2 = { id: 15405 };

        const compareResult1 = service.compareFishProduct(entity1, entity2);
        const compareResult2 = service.compareFishProduct(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 7384 };
        const entity2 = { id: 7384 };

        const compareResult1 = service.compareFishProduct(entity1, entity2);
        const compareResult2 = service.compareFishProduct(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
