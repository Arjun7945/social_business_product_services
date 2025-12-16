import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICartItem, NewCartItem } from '../cart-item.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICartItem for edit and NewCartItemFormGroupInput for create.
 */
type CartItemFormGroupInput = ICartItem | PartialWithRequiredKeyOf<NewCartItem>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICartItem | NewCartItem> = Omit<T, 'addedAt'> & {
  addedAt?: string | null;
};

type CartItemFormRawValue = FormValueOf<ICartItem>;

type NewCartItemFormRawValue = FormValueOf<NewCartItem>;

type CartItemFormDefaults = Pick<NewCartItem, 'id' | 'addedAt'>;

type CartItemFormGroupContent = {
  id: FormControl<CartItemFormRawValue['id'] | NewCartItem['id']>;
  quantityKg: FormControl<CartItemFormRawValue['quantityKg']>;
  addedAt: FormControl<CartItemFormRawValue['addedAt']>;
  product: FormControl<CartItemFormRawValue['product']>;
  cart: FormControl<CartItemFormRawValue['cart']>;
};

export type CartItemFormGroup = FormGroup<CartItemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CartItemFormService {
  createCartItemFormGroup(cartItem: CartItemFormGroupInput = { id: null }): CartItemFormGroup {
    const cartItemRawValue = this.convertCartItemToCartItemRawValue({
      ...this.getFormDefaults(),
      ...cartItem,
    });
    return new FormGroup<CartItemFormGroupContent>({
      id: new FormControl(
        { value: cartItemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantityKg: new FormControl(cartItemRawValue.quantityKg, {
        validators: [Validators.required],
      }),
      addedAt: new FormControl(cartItemRawValue.addedAt),
      product: new FormControl(cartItemRawValue.product, {
        validators: [Validators.required],
      }),
      cart: new FormControl(cartItemRawValue.cart, {
        validators: [Validators.required],
      }),
    });
  }

  getCartItem(form: CartItemFormGroup): ICartItem | NewCartItem {
    return this.convertCartItemRawValueToCartItem(form.getRawValue() as CartItemFormRawValue | NewCartItemFormRawValue);
  }

  resetForm(form: CartItemFormGroup, cartItem: CartItemFormGroupInput): void {
    const cartItemRawValue = this.convertCartItemToCartItemRawValue({ ...this.getFormDefaults(), ...cartItem });
    form.reset(
      {
        ...cartItemRawValue,
        id: { value: cartItemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CartItemFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      addedAt: currentTime,
    };
  }

  private convertCartItemRawValueToCartItem(rawCartItem: CartItemFormRawValue | NewCartItemFormRawValue): ICartItem | NewCartItem {
    return {
      ...rawCartItem,
      addedAt: dayjs(rawCartItem.addedAt, DATE_TIME_FORMAT),
    };
  }

  private convertCartItemToCartItemRawValue(
    cartItem: ICartItem | (Partial<NewCartItem> & CartItemFormDefaults),
  ): CartItemFormRawValue | PartialWithRequiredKeyOf<NewCartItemFormRawValue> {
    return {
      ...cartItem,
      addedAt: cartItem.addedAt ? cartItem.addedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
