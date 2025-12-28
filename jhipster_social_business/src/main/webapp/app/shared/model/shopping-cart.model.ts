import dayjs from 'dayjs';
import { ICustomer } from 'app/shared/model/customer.model';

export interface IShoppingCart {
  id?: number;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
  customer?: ICustomer;
}

export const defaultValue: Readonly<IShoppingCart> = {};
