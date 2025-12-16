import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../product-image.test-samples';

import { ProductImageFormService } from './product-image-form.service';

describe('ProductImage Form Service', () => {
  let service: ProductImageFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductImageFormService);
  });

  describe('Service methods', () => {
    describe('createProductImageFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProductImageFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            imageUrl: expect.any(Object),
            displayOrder: expect.any(Object),
            product: expect.any(Object),
          }),
        );
      });

      it('passing IProductImage should create a new form with FormGroup', () => {
        const formGroup = service.createProductImageFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            imageUrl: expect.any(Object),
            displayOrder: expect.any(Object),
            product: expect.any(Object),
          }),
        );
      });
    });

    describe('getProductImage', () => {
      it('should return NewProductImage for default ProductImage initial value', () => {
        const formGroup = service.createProductImageFormGroup(sampleWithNewData);

        const productImage = service.getProductImage(formGroup) as any;

        expect(productImage).toMatchObject(sampleWithNewData);
      });

      it('should return NewProductImage for empty ProductImage initial value', () => {
        const formGroup = service.createProductImageFormGroup();

        const productImage = service.getProductImage(formGroup) as any;

        expect(productImage).toMatchObject({});
      });

      it('should return IProductImage', () => {
        const formGroup = service.createProductImageFormGroup(sampleWithRequiredData);

        const productImage = service.getProductImage(formGroup) as any;

        expect(productImage).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProductImage should not enable id FormControl', () => {
        const formGroup = service.createProductImageFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProductImage should disable id FormControl', () => {
        const formGroup = service.createProductImageFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
