import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ICustomerOrder } from '../customer-order.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../customer-order.test-samples';

import { CustomerOrderService, RestCustomerOrder } from './customer-order.service';

const requireRestSample: RestCustomerOrder = {
  ...sampleWithRequiredData,
  orderTime: sampleWithRequiredData.orderTime?.toJSON(),
  confirmedAt: sampleWithRequiredData.confirmedAt?.toJSON(),
};

describe('CustomerOrder Service', () => {
  let service: CustomerOrderService;
  let httpMock: HttpTestingController;
  let expectedResult: ICustomerOrder | ICustomerOrder[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CustomerOrderService);
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

    it('should create a CustomerOrder', () => {
      const customerOrder = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(customerOrder).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CustomerOrder', () => {
      const customerOrder = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(customerOrder).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CustomerOrder', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CustomerOrder', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CustomerOrder', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCustomerOrderToCollectionIfMissing', () => {
      it('should add a CustomerOrder to an empty array', () => {
        const customerOrder: ICustomerOrder = sampleWithRequiredData;
        expectedResult = service.addCustomerOrderToCollectionIfMissing([], customerOrder);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(customerOrder);
      });

      it('should not add a CustomerOrder to an array that contains it', () => {
        const customerOrder: ICustomerOrder = sampleWithRequiredData;
        const customerOrderCollection: ICustomerOrder[] = [
          {
            ...customerOrder,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCustomerOrderToCollectionIfMissing(customerOrderCollection, customerOrder);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CustomerOrder to an array that doesn't contain it", () => {
        const customerOrder: ICustomerOrder = sampleWithRequiredData;
        const customerOrderCollection: ICustomerOrder[] = [sampleWithPartialData];
        expectedResult = service.addCustomerOrderToCollectionIfMissing(customerOrderCollection, customerOrder);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(customerOrder);
      });

      it('should add only unique CustomerOrder to an array', () => {
        const customerOrderArray: ICustomerOrder[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const customerOrderCollection: ICustomerOrder[] = [sampleWithRequiredData];
        expectedResult = service.addCustomerOrderToCollectionIfMissing(customerOrderCollection, ...customerOrderArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const customerOrder: ICustomerOrder = sampleWithRequiredData;
        const customerOrder2: ICustomerOrder = sampleWithPartialData;
        expectedResult = service.addCustomerOrderToCollectionIfMissing([], customerOrder, customerOrder2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(customerOrder);
        expect(expectedResult).toContain(customerOrder2);
      });

      it('should accept null and undefined values', () => {
        const customerOrder: ICustomerOrder = sampleWithRequiredData;
        expectedResult = service.addCustomerOrderToCollectionIfMissing([], null, customerOrder, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(customerOrder);
      });

      it('should return initial array if no CustomerOrder is added', () => {
        const customerOrderCollection: ICustomerOrder[] = [sampleWithRequiredData];
        expectedResult = service.addCustomerOrderToCollectionIfMissing(customerOrderCollection, undefined, null);
        expect(expectedResult).toEqual(customerOrderCollection);
      });
    });

    describe('compareCustomerOrder', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCustomerOrder(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 18791 };
        const entity2 = null;

        const compareResult1 = service.compareCustomerOrder(entity1, entity2);
        const compareResult2 = service.compareCustomerOrder(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 18791 };
        const entity2 = { id: 9643 };

        const compareResult1 = service.compareCustomerOrder(entity1, entity2);
        const compareResult2 = service.compareCustomerOrder(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 18791 };
        const entity2 = { id: 18791 };

        const compareResult1 = service.compareCustomerOrder(entity1, entity2);
        const compareResult2 = service.compareCustomerOrder(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
