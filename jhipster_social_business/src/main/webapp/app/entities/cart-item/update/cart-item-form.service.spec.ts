import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../cart-item.test-samples';

import { CartItemFormService } from './cart-item-form.service';

describe('CartItem Form Service', () => {
  let service: CartItemFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CartItemFormService);
  });

  describe('Service methods', () => {
    describe('createCartItemFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCartItemFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantityKg: expect.any(Object),
            addedAt: expect.any(Object),
            product: expect.any(Object),
            cart: expect.any(Object),
          }),
        );
      });

      it('passing ICartItem should create a new form with FormGroup', () => {
        const formGroup = service.createCartItemFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantityKg: expect.any(Object),
            addedAt: expect.any(Object),
            product: expect.any(Object),
            cart: expect.any(Object),
          }),
        );
      });
    });

    describe('getCartItem', () => {
      it('should return NewCartItem for default CartItem initial value', () => {
        const formGroup = service.createCartItemFormGroup(sampleWithNewData);

        const cartItem = service.getCartItem(formGroup) as any;

        expect(cartItem).toMatchObject(sampleWithNewData);
      });

      it('should return NewCartItem for empty CartItem initial value', () => {
        const formGroup = service.createCartItemFormGroup();

        const cartItem = service.getCartItem(formGroup) as any;

        expect(cartItem).toMatchObject({});
      });

      it('should return ICartItem', () => {
        const formGroup = service.createCartItemFormGroup(sampleWithRequiredData);

        const cartItem = service.getCartItem(formGroup) as any;

        expect(cartItem).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICartItem should not enable id FormControl', () => {
        const formGroup = service.createCartItemFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCartItem should disable id FormControl', () => {
        const formGroup = service.createCartItemFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
