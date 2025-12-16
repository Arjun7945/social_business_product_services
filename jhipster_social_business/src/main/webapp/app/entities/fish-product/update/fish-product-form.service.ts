import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IFishProduct, NewFishProduct } from '../fish-product.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFishProduct for edit and NewFishProductFormGroupInput for create.
 */
type FishProductFormGroupInput = IFishProduct | PartialWithRequiredKeyOf<NewFishProduct>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IFishProduct | NewFishProduct> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type FishProductFormRawValue = FormValueOf<IFishProduct>;

type NewFishProductFormRawValue = FormValueOf<NewFishProduct>;

type FishProductFormDefaults = Pick<NewFishProduct, 'id' | 'isAvailable' | 'createdAt'>;

type FishProductFormGroupContent = {
  id: FormControl<FishProductFormRawValue['id'] | NewFishProduct['id']>;
  name: FormControl<FishProductFormRawValue['name']>;
  pricePerKg: FormControl<FishProductFormRawValue['pricePerKg']>;
  imageUrl: FormControl<FishProductFormRawValue['imageUrl']>;
  description: FormControl<FishProductFormRawValue['description']>;
  isAvailable: FormControl<FishProductFormRawValue['isAvailable']>;
  createdAt: FormControl<FishProductFormRawValue['createdAt']>;
};

export type FishProductFormGroup = FormGroup<FishProductFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FishProductFormService {
  createFishProductFormGroup(fishProduct: FishProductFormGroupInput = { id: null }): FishProductFormGroup {
    const fishProductRawValue = this.convertFishProductToFishProductRawValue({
      ...this.getFormDefaults(),
      ...fishProduct,
    });
    return new FormGroup<FishProductFormGroupContent>({
      id: new FormControl(
        { value: fishProductRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(fishProductRawValue.name, {
        validators: [Validators.required],
      }),
      pricePerKg: new FormControl(fishProductRawValue.pricePerKg, {
        validators: [Validators.required, Validators.min(0)],
      }),
      imageUrl: new FormControl(fishProductRawValue.imageUrl),
      description: new FormControl(fishProductRawValue.description, {
        validators: [Validators.maxLength(2000)],
      }),
      isAvailable: new FormControl(fishProductRawValue.isAvailable, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(fishProductRawValue.createdAt),
    });
  }

  getFishProduct(form: FishProductFormGroup): IFishProduct | NewFishProduct {
    return this.convertFishProductRawValueToFishProduct(form.getRawValue() as FishProductFormRawValue | NewFishProductFormRawValue);
  }

  resetForm(form: FishProductFormGroup, fishProduct: FishProductFormGroupInput): void {
    const fishProductRawValue = this.convertFishProductToFishProductRawValue({ ...this.getFormDefaults(), ...fishProduct });
    form.reset(
      {
        ...fishProductRawValue,
        id: { value: fishProductRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): FishProductFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      isAvailable: false,
      createdAt: currentTime,
    };
  }

  private convertFishProductRawValueToFishProduct(
    rawFishProduct: FishProductFormRawValue | NewFishProductFormRawValue,
  ): IFishProduct | NewFishProduct {
    return {
      ...rawFishProduct,
      createdAt: dayjs(rawFishProduct.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertFishProductToFishProductRawValue(
    fishProduct: IFishProduct | (Partial<NewFishProduct> & FishProductFormDefaults),
  ): FishProductFormRawValue | PartialWithRequiredKeyOf<NewFishProductFormRawValue> {
    return {
      ...fishProduct,
      createdAt: fishProduct.createdAt ? fishProduct.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
