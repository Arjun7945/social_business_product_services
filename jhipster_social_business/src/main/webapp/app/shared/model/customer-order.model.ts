import dayjs from 'dayjs';
import { IOrderStatusHistory } from 'app/shared/model/order-status-history.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { IDeliveryPerson } from 'app/shared/model/delivery-person.model';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';

export interface ICustomerOrder {
  id?: number;
  orderTime?: dayjs.Dayjs;
  totalAmount?: number;
  status?: keyof typeof OrderStatus;
  paymentMethod?: string;
  confirmedAt?: dayjs.Dayjs | null;
  removedCustomerId?: number | null;
  removedDeliveryPersonId?: number | null;
  transactionId?: string | null;
  history?: IOrderStatusHistory | null;
  customer?: ICustomer | null;
  deliveryPerson?: IDeliveryPerson | null;
}

export const defaultValue: Readonly<ICustomerOrder> = {};
