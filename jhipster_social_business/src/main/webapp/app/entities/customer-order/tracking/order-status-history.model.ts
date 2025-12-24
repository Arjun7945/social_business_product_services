import dayjs from 'dayjs/esm';
import { ICustomerOrder } from 'app/entities/customer-order/customer-order.model';

export interface IOrderStatusHistory {
  id: number;
  status?: string | null;
  changeTime?: dayjs.Dayjs | null;
  customerOrder?: Pick<ICustomerOrder, 'id'> | null;
}

export type NewOrderStatusHistory = Omit<IOrderStatusHistory, 'id'> & { id: null };
