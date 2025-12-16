import dayjs from 'dayjs/esm';
import { ITeamMember } from 'app/entities/team-member/team-member.model';
import { ICustomer } from 'app/entities/customer/customer.model';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';

export interface ICustomerOrder {
  id: number;
  orderTime?: dayjs.Dayjs | null;
  totalAmount?: number | null;
  status?: keyof typeof OrderStatus | null;
  paymentMethod?: string | null;
  confirmedAt?: dayjs.Dayjs | null;
  deliveryPerson?: Pick<ITeamMember, 'id' | 'name'> | null;
  customer?: Pick<ICustomer, 'id' | 'name'> | null;
}

export type NewCustomerOrder = Omit<ICustomerOrder, 'id'> & { id: null };
