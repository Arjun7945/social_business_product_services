import dayjs from 'dayjs';
import { IReturnedOrder } from 'app/shared/model/returned-order.model';
import { ReturnStatus } from 'app/shared/model/enumerations/return-status.model';

export interface IReturnStatusHistory {
  id?: number;
  status?: keyof typeof ReturnStatus;
  changeTime?: dayjs.Dayjs;
  returnedOrder?: IReturnedOrder | null;
}

export const defaultValue: Readonly<IReturnStatusHistory> = {};
