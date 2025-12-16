import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../fish-product.test-samples';

import { FishProductFormService } from './fish-product-form.service';

describe('FishProduct Form Service', () => {
  let service: FishProductFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FishProductFormService);
  });

  describe('Service methods', () => {
    describe('createFishProductFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFishProductFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            pricePerKg: expect.any(Object),
            imageUrl: expect.any(Object),
            description: expect.any(Object),
            isAvailable: expect.any(Object),
            createdAt: expect.any(Object),
          }),
        );
      });

      it('passing IFishProduct should create a new form with FormGroup', () => {
        const formGroup = service.createFishProductFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            pricePerKg: expect.any(Object),
            imageUrl: expect.any(Object),
            description: expect.any(Object),
            isAvailable: expect.any(Object),
            createdAt: expect.any(Object),
          }),
        );
      });
    });

    describe('getFishProduct', () => {
      it('should return NewFishProduct for default FishProduct initial value', () => {
        const formGroup = service.createFishProductFormGroup(sampleWithNewData);

        const fishProduct = service.getFishProduct(formGroup) as any;

        expect(fishProduct).toMatchObject(sampleWithNewData);
      });

      it('should return NewFishProduct for empty FishProduct initial value', () => {
        const formGroup = service.createFishProductFormGroup();

        const fishProduct = service.getFishProduct(formGroup) as any;

        expect(fishProduct).toMatchObject({});
      });

      it('should return IFishProduct', () => {
        const formGroup = service.createFishProductFormGroup(sampleWithRequiredData);

        const fishProduct = service.getFishProduct(formGroup) as any;

        expect(fishProduct).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFishProduct should not enable id FormControl', () => {
        const formGroup = service.createFishProductFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFishProduct should disable id FormControl', () => {
        const formGroup = service.createFishProductFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
