import dayjs from 'dayjs';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';

export interface IOrderStatusHistory {
  id?: number;
  status?: keyof typeof OrderStatus;
  changeTime?: dayjs.Dayjs;
  onWayTime?: dayjs.Dayjs | null;
  paymentPendingTime?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IOrderStatusHistory> = {};
