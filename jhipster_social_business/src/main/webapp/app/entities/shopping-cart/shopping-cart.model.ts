import dayjs from 'dayjs/esm';
import { ICustomer } from 'app/entities/customer/customer.model';

export interface IShoppingCart {
  id: number;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  customer?: Pick<ICustomer, 'id' | 'name'> | null;
}

export type NewShoppingCart = Omit<IShoppingCart, 'id'> & { id: null };
