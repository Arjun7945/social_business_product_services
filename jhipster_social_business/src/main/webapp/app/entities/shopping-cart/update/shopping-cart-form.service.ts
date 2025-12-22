import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IShoppingCart, NewShoppingCart } from '../shopping-cart.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IShoppingCart for edit and NewShoppingCartFormGroupInput for create.
 */
type ShoppingCartFormGroupInput = IShoppingCart | PartialWithRequiredKeyOf<NewShoppingCart>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IShoppingCart | NewShoppingCart> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

type ShoppingCartFormRawValue = FormValueOf<IShoppingCart>;

type NewShoppingCartFormRawValue = FormValueOf<NewShoppingCart>;

type ShoppingCartFormDefaults = Pick<NewShoppingCart, 'id' | 'createdAt' | 'updatedAt' | 'version'>;

type ShoppingCartFormGroupContent = {
  id: FormControl<ShoppingCartFormRawValue['id'] | NewShoppingCart['id']>;
  createdAt: FormControl<ShoppingCartFormRawValue['createdAt']>;
  updatedAt: FormControl<ShoppingCartFormRawValue['updatedAt']>;
  customer: FormControl<ShoppingCartFormRawValue['customer']>;
  version: FormControl<ShoppingCartFormRawValue['version']>;
};

export type ShoppingCartFormGroup = FormGroup<ShoppingCartFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ShoppingCartFormService {
  createShoppingCartFormGroup(shoppingCart: ShoppingCartFormGroupInput = { id: null }): ShoppingCartFormGroup {
    const shoppingCartRawValue = this.convertShoppingCartToShoppingCartRawValue({
      ...this.getFormDefaults(),
      ...shoppingCart,
    });
    return new FormGroup<ShoppingCartFormGroupContent>({
      id: new FormControl(
        { value: shoppingCartRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      createdAt: new FormControl(shoppingCartRawValue.createdAt, {
        validators: [Validators.required],
      }),
      updatedAt: new FormControl(shoppingCartRawValue.updatedAt),
      customer: new FormControl(shoppingCartRawValue.customer, {
        validators: [Validators.required],
      }),
      version: new FormControl(shoppingCartRawValue.version),
    });
  }

  getShoppingCart(form: ShoppingCartFormGroup): IShoppingCart | NewShoppingCart {
    return this.convertShoppingCartRawValueToShoppingCart(form.getRawValue() as ShoppingCartFormRawValue | NewShoppingCartFormRawValue);
  }

  resetForm(form: ShoppingCartFormGroup, shoppingCart: ShoppingCartFormGroupInput): void {
    const shoppingCartRawValue = this.convertShoppingCartToShoppingCartRawValue({ ...this.getFormDefaults(), ...shoppingCart });
    form.reset(
      {
        ...shoppingCartRawValue,
        id: { value: shoppingCartRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ShoppingCartFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      updatedAt: currentTime,
      version: null,
    };
  }

  private convertShoppingCartRawValueToShoppingCart(
    rawShoppingCart: ShoppingCartFormRawValue | NewShoppingCartFormRawValue,
  ): IShoppingCart | NewShoppingCart {
    return {
      ...rawShoppingCart,
      createdAt: dayjs(rawShoppingCart.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawShoppingCart.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertShoppingCartToShoppingCartRawValue(
    shoppingCart: IShoppingCart | (Partial<NewShoppingCart> & ShoppingCartFormDefaults),
  ): ShoppingCartFormRawValue | PartialWithRequiredKeyOf<NewShoppingCartFormRawValue> {
    return {
      ...shoppingCart,
      createdAt: shoppingCart.createdAt ? shoppingCart.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: shoppingCart.updatedAt ? shoppingCart.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
