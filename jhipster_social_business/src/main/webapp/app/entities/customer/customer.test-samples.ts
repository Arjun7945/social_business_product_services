import dayjs from 'dayjs/esm';

import { ICustomer, NewCustomer } from './customer.model';

export const sampleWithRequiredData: ICustomer = {
  id: 3366,
  waPhoneNumber: 'slake splurge',
  isPincodeValid: true,
  role: 'DELIVERY_PERSON',
};

export const sampleWithPartialData: ICustomer = {
  id: 21342,
  waPhoneNumber: 'amidst sinful',
  address: 'smoggy yearly deliberately',
  distanceFromBusinessKm: 15587.5,
  isPincodeValid: true,
  role: 'DEVELOPER',
};

export const sampleWithFullData: ICustomer = {
  id: 4149,
  waPhoneNumber: 'where known',
  name: 'questionably',
  phoneNumber: 'clamor pace',
  locationLat: 19156.74,
  locationLon: 2075.41,
  address: 'for',
  distanceFromBusinessKm: 29111.12,
  isPincodeValid: false,
  role: 'CUSTOMER',
  joinedAt: dayjs('2025-12-14T14:32'),
  lastInteractionAt: dayjs('2025-12-15T04:17'),
};

export const sampleWithNewData: NewCustomer = {
  waPhoneNumber: 'thyme inasmuch',
  isPincodeValid: true,
  role: 'ADMIN',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
