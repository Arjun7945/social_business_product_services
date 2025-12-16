import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../customer-order.test-samples';

import { CustomerOrderFormService } from './customer-order-form.service';

describe('CustomerOrder Form Service', () => {
  let service: CustomerOrderFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CustomerOrderFormService);
  });

  describe('Service methods', () => {
    describe('createCustomerOrderFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCustomerOrderFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            orderTime: expect.any(Object),
            totalAmount: expect.any(Object),
            status: expect.any(Object),
            paymentMethod: expect.any(Object),
            confirmedAt: expect.any(Object),
            deliveryPerson: expect.any(Object),
            customer: expect.any(Object),
          }),
        );
      });

      it('passing ICustomerOrder should create a new form with FormGroup', () => {
        const formGroup = service.createCustomerOrderFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            orderTime: expect.any(Object),
            totalAmount: expect.any(Object),
            status: expect.any(Object),
            paymentMethod: expect.any(Object),
            confirmedAt: expect.any(Object),
            deliveryPerson: expect.any(Object),
            customer: expect.any(Object),
          }),
        );
      });
    });

    describe('getCustomerOrder', () => {
      it('should return NewCustomerOrder for default CustomerOrder initial value', () => {
        const formGroup = service.createCustomerOrderFormGroup(sampleWithNewData);

        const customerOrder = service.getCustomerOrder(formGroup) as any;

        expect(customerOrder).toMatchObject(sampleWithNewData);
      });

      it('should return NewCustomerOrder for empty CustomerOrder initial value', () => {
        const formGroup = service.createCustomerOrderFormGroup();

        const customerOrder = service.getCustomerOrder(formGroup) as any;

        expect(customerOrder).toMatchObject({});
      });

      it('should return ICustomerOrder', () => {
        const formGroup = service.createCustomerOrderFormGroup(sampleWithRequiredData);

        const customerOrder = service.getCustomerOrder(formGroup) as any;

        expect(customerOrder).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICustomerOrder should not enable id FormControl', () => {
        const formGroup = service.createCustomerOrderFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCustomerOrder should disable id FormControl', () => {
        const formGroup = service.createCustomerOrderFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
