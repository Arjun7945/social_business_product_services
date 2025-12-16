import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICustomer, NewCustomer } from '../customer.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICustomer for edit and NewCustomerFormGroupInput for create.
 */
type CustomerFormGroupInput = ICustomer | PartialWithRequiredKeyOf<NewCustomer>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICustomer | NewCustomer> = Omit<T, 'joinedAt' | 'lastInteractionAt'> & {
  joinedAt?: string | null;
  lastInteractionAt?: string | null;
};

type CustomerFormRawValue = FormValueOf<ICustomer>;

type NewCustomerFormRawValue = FormValueOf<NewCustomer>;

type CustomerFormDefaults = Pick<NewCustomer, 'id' | 'isPincodeValid' | 'joinedAt' | 'lastInteractionAt'>;

type CustomerFormGroupContent = {
  id: FormControl<CustomerFormRawValue['id'] | NewCustomer['id']>;
  waPhoneNumber: FormControl<CustomerFormRawValue['waPhoneNumber']>;
  name: FormControl<CustomerFormRawValue['name']>;
  phoneNumber: FormControl<CustomerFormRawValue['phoneNumber']>;
  locationLat: FormControl<CustomerFormRawValue['locationLat']>;
  locationLon: FormControl<CustomerFormRawValue['locationLon']>;
  address: FormControl<CustomerFormRawValue['address']>;
  distanceFromBusinessKm: FormControl<CustomerFormRawValue['distanceFromBusinessKm']>;
  isPincodeValid: FormControl<CustomerFormRawValue['isPincodeValid']>;
  role: FormControl<CustomerFormRawValue['role']>;
  joinedAt: FormControl<CustomerFormRawValue['joinedAt']>;
  lastInteractionAt: FormControl<CustomerFormRawValue['lastInteractionAt']>;
  addedBy: FormControl<CustomerFormRawValue['addedBy']>;
};

export type CustomerFormGroup = FormGroup<CustomerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CustomerFormService {
  createCustomerFormGroup(customer: CustomerFormGroupInput = { id: null }): CustomerFormGroup {
    const customerRawValue = this.convertCustomerToCustomerRawValue({
      ...this.getFormDefaults(),
      ...customer,
    });
    return new FormGroup<CustomerFormGroupContent>({
      id: new FormControl(
        { value: customerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      waPhoneNumber: new FormControl(customerRawValue.waPhoneNumber, {
        validators: [Validators.required],
      }),
      name: new FormControl(customerRawValue.name),
      phoneNumber: new FormControl(customerRawValue.phoneNumber),
      locationLat: new FormControl(customerRawValue.locationLat),
      locationLon: new FormControl(customerRawValue.locationLon),
      address: new FormControl(customerRawValue.address),
      distanceFromBusinessKm: new FormControl(customerRawValue.distanceFromBusinessKm),
      isPincodeValid: new FormControl(customerRawValue.isPincodeValid, {
        validators: [Validators.required],
      }),
      role: new FormControl(customerRawValue.role, {
        validators: [Validators.required],
      }),
      joinedAt: new FormControl(customerRawValue.joinedAt),
      lastInteractionAt: new FormControl(customerRawValue.lastInteractionAt),
      addedBy: new FormControl(customerRawValue.addedBy),
    });
  }

  getCustomer(form: CustomerFormGroup): ICustomer | NewCustomer {
    return this.convertCustomerRawValueToCustomer(form.getRawValue() as CustomerFormRawValue | NewCustomerFormRawValue);
  }

  resetForm(form: CustomerFormGroup, customer: CustomerFormGroupInput): void {
    const customerRawValue = this.convertCustomerToCustomerRawValue({ ...this.getFormDefaults(), ...customer });
    form.reset(
      {
        ...customerRawValue,
        id: { value: customerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CustomerFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      isPincodeValid: false,
      joinedAt: currentTime,
      lastInteractionAt: currentTime,
    };
  }

  private convertCustomerRawValueToCustomer(rawCustomer: CustomerFormRawValue | NewCustomerFormRawValue): ICustomer | NewCustomer {
    return {
      ...rawCustomer,
      joinedAt: dayjs(rawCustomer.joinedAt, DATE_TIME_FORMAT),
      lastInteractionAt: dayjs(rawCustomer.lastInteractionAt, DATE_TIME_FORMAT),
    };
  }

  private convertCustomerToCustomerRawValue(
    customer: ICustomer | (Partial<NewCustomer> & CustomerFormDefaults),
  ): CustomerFormRawValue | PartialWithRequiredKeyOf<NewCustomerFormRawValue> {
    return {
      ...customer,
      joinedAt: customer.joinedAt ? customer.joinedAt.format(DATE_TIME_FORMAT) : undefined,
      lastInteractionAt: customer.lastInteractionAt ? customer.lastInteractionAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
