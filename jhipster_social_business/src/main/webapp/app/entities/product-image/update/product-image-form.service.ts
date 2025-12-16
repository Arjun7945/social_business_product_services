import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProductImage, NewProductImage } from '../product-image.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProductImage for edit and NewProductImageFormGroupInput for create.
 */
type ProductImageFormGroupInput = IProductImage | PartialWithRequiredKeyOf<NewProductImage>;

type ProductImageFormDefaults = Pick<NewProductImage, 'id'>;

type ProductImageFormGroupContent = {
  id: FormControl<IProductImage['id'] | NewProductImage['id']>;
  imageUrl: FormControl<IProductImage['imageUrl']>;
  displayOrder: FormControl<IProductImage['displayOrder']>;
  product: FormControl<IProductImage['product']>;
};

export type ProductImageFormGroup = FormGroup<ProductImageFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductImageFormService {
  createProductImageFormGroup(productImage: ProductImageFormGroupInput = { id: null }): ProductImageFormGroup {
    const productImageRawValue = {
      ...this.getFormDefaults(),
      ...productImage,
    };
    return new FormGroup<ProductImageFormGroupContent>({
      id: new FormControl(
        { value: productImageRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      imageUrl: new FormControl(productImageRawValue.imageUrl, {
        validators: [Validators.required],
      }),
      displayOrder: new FormControl(productImageRawValue.displayOrder, {
        validators: [Validators.required],
      }),
      product: new FormControl(productImageRawValue.product, {
        validators: [Validators.required],
      }),
    });
  }

  getProductImage(form: ProductImageFormGroup): IProductImage | NewProductImage {
    return form.getRawValue() as IProductImage | NewProductImage;
  }

  resetForm(form: ProductImageFormGroup, productImage: ProductImageFormGroupInput): void {
    const productImageRawValue = { ...this.getFormDefaults(), ...productImage };
    form.reset(
      {
        ...productImageRawValue,
        id: { value: productImageRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ProductImageFormDefaults {
    return {
      id: null,
    };
  }
}
