import { UserRole } from 'app/entities/enumerations/user-role.model';
import dayjs from 'dayjs/esm';

export interface IRemovedOrderSummary {
  id: number;
  userOriginalId?: number | null;
  userName?: string | null;
  userRole?: keyof typeof UserRole | null;
  totalOrders?: number | null;
  totalAmount?: number | null;
  firstInteractionAt?: dayjs.Dayjs | null;
  lastInteractionAt?: dayjs.Dayjs | null;
  removedAt?: dayjs.Dayjs | null;
}

export type NewRemovedOrderSummary = Omit<IRemovedOrderSummary, 'id'> & { id: null };
