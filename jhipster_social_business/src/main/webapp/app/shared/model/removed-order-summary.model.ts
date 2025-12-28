import dayjs from 'dayjs';
import { UserRole } from 'app/shared/model/enumerations/user-role.model';

export interface IRemovedOrderSummary {
  id?: number;
  userOriginalId?: number | null;
  userName?: string | null;
  userRole?: keyof typeof UserRole | null;
  totalOrders?: number | null;
  totalAmount?: number | null;
  firstInteractionAt?: dayjs.Dayjs | null;
  lastInteractionAt?: dayjs.Dayjs | null;
  removedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IRemovedOrderSummary> = {};
