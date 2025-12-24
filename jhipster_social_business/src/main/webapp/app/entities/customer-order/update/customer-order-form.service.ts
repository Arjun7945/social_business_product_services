import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICustomerOrder, NewCustomerOrder } from '../customer-order.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICustomerOrder for edit and NewCustomerOrderFormGroupInput for create.
 */
type CustomerOrderFormGroupInput = ICustomerOrder | PartialWithRequiredKeyOf<NewCustomerOrder>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICustomerOrder | NewCustomerOrder> = Omit<T, 'orderTime' | 'confirmedAt'> & {
  orderTime?: string | null;
  confirmedAt?: string | null;
};

type CustomerOrderFormRawValue = FormValueOf<ICustomerOrder>;

type NewCustomerOrderFormRawValue = FormValueOf<NewCustomerOrder>;

type CustomerOrderFormDefaults = Pick<NewCustomerOrder, 'id' | 'orderTime' | 'confirmedAt' | 'version'>;

type CustomerOrderFormGroupContent = {
  id: FormControl<CustomerOrderFormRawValue['id'] | NewCustomerOrder['id']>;
  orderTime: FormControl<CustomerOrderFormRawValue['orderTime']>;
  totalAmount: FormControl<CustomerOrderFormRawValue['totalAmount']>;
  status: FormControl<CustomerOrderFormRawValue['status']>;
  paymentMethod: FormControl<CustomerOrderFormRawValue['paymentMethod']>;
  confirmedAt: FormControl<CustomerOrderFormRawValue['confirmedAt']>;
  deliveryPerson: FormControl<CustomerOrderFormRawValue['deliveryPerson']>;
  customer: FormControl<CustomerOrderFormRawValue['customer']>;
  version: FormControl<CustomerOrderFormRawValue['version']>;
  transactionId: FormControl<CustomerOrderFormRawValue['transactionId']>;
};

export type CustomerOrderFormGroup = FormGroup<CustomerOrderFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CustomerOrderFormService {
  createCustomerOrderFormGroup(customerOrder: CustomerOrderFormGroupInput = { id: null }): CustomerOrderFormGroup {
    const customerOrderRawValue = this.convertCustomerOrderToCustomerOrderRawValue({
      ...this.getFormDefaults(),
      ...customerOrder,
    });
    return new FormGroup<CustomerOrderFormGroupContent>({
      id: new FormControl(
        { value: customerOrderRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      orderTime: new FormControl(customerOrderRawValue.orderTime, {
        validators: [Validators.required],
      }),
      totalAmount: new FormControl(customerOrderRawValue.totalAmount, {
        validators: [Validators.required, Validators.min(0)],
      }),
      status: new FormControl(customerOrderRawValue.status, {
        validators: [Validators.required],
      }),
      paymentMethod: new FormControl(customerOrderRawValue.paymentMethod, {
        validators: [Validators.required],
      }),
      confirmedAt: new FormControl(customerOrderRawValue.confirmedAt),
      deliveryPerson: new FormControl(customerOrderRawValue.deliveryPerson),
      customer: new FormControl(customerOrderRawValue.customer, {
        validators: [Validators.required],
      }),
      version: new FormControl(customerOrderRawValue.version),
      transactionId: new FormControl(customerOrderRawValue.transactionId),
    });
  }

  getCustomerOrder(form: CustomerOrderFormGroup): ICustomerOrder | NewCustomerOrder {
    return this.convertCustomerOrderRawValueToCustomerOrder(form.getRawValue() as CustomerOrderFormRawValue | NewCustomerOrderFormRawValue);
  }

  resetForm(form: CustomerOrderFormGroup, customerOrder: CustomerOrderFormGroupInput): void {
    const customerOrderRawValue = this.convertCustomerOrderToCustomerOrderRawValue({ ...this.getFormDefaults(), ...customerOrder });
    form.reset(
      {
        ...customerOrderRawValue,
        id: { value: customerOrderRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CustomerOrderFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      orderTime: currentTime,
      confirmedAt: currentTime,
      version: null,
    };
  }

  private convertCustomerOrderRawValueToCustomerOrder(
    rawCustomerOrder: CustomerOrderFormRawValue | NewCustomerOrderFormRawValue,
  ): ICustomerOrder | NewCustomerOrder {
    return {
      ...rawCustomerOrder,
      orderTime: dayjs(rawCustomerOrder.orderTime, DATE_TIME_FORMAT),
      confirmedAt: dayjs(rawCustomerOrder.confirmedAt, DATE_TIME_FORMAT),
    };
  }

  private convertCustomerOrderToCustomerOrderRawValue(
    customerOrder: ICustomerOrder | (Partial<NewCustomerOrder> & CustomerOrderFormDefaults),
  ): CustomerOrderFormRawValue | PartialWithRequiredKeyOf<NewCustomerOrderFormRawValue> {
    return {
      ...customerOrder,
      orderTime: customerOrder.orderTime ? customerOrder.orderTime.format(DATE_TIME_FORMAT) : undefined,
      confirmedAt: customerOrder.confirmedAt ? customerOrder.confirmedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
