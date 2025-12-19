import dayjs from 'dayjs/esm';

import { ICustomerOrder, NewCustomerOrder } from './customer-order.model';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';

export const sampleWithRequiredData: ICustomerOrder = {
  id: 2458,
  orderTime: dayjs('2025-12-15T09:24'),
  totalAmount: 19945,
  status: OrderStatus.DELIVERY_ONWAY,
  paymentMethod: 'meh save multicolored',
};

export const sampleWithPartialData: ICustomerOrder = {
  id: 22678,
  orderTime: dayjs('2025-12-14T19:42'),
  totalAmount: 6556.79,
  status: OrderStatus.DELIVERY_ONWAY,
  paymentMethod: 'never',
};

export const sampleWithFullData: ICustomerOrder = {
  id: 24520,
  orderTime: dayjs('2025-12-15T01:07'),
  totalAmount: 9891.78,
  status: OrderStatus.ORDER_DELIVERED_SUCESSFULLY,
  paymentMethod: 'uproot ack sans',
  confirmedAt: dayjs('2025-12-15T01:10'),
};

export const sampleWithNewData: NewCustomerOrder = {
  orderTime: dayjs('2025-12-14T22:42'),
  totalAmount: 13060.3,
  status: OrderStatus.ORDER_FAILED,
  paymentMethod: 'gah',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
