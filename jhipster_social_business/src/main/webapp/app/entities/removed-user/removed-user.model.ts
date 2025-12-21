import { UserRole } from 'app/entities/enumerations/user-role.model';
import { AccountStatus } from 'app/entities/enumerations/account-status.model';
import dayjs from 'dayjs/esm';

export interface IRemovedUser {
  id: number;
  originalId?: number | null;
  name?: string | null;
  role?: keyof typeof UserRole | null;
  whatsappNumber?: string | null;
  phoneNumber?: string | null;
  address?: string | null;
  locationLat?: number | null;
  locationLon?: number | null;
  joinedAt?: dayjs.Dayjs | null;
  removedAt?: dayjs.Dayjs | null;
  reasonForRemoval?: string | null;
  lastSessionData?: string | null;
  status?: keyof typeof AccountStatus | null;
  orderHistoryId?: number | null;
}

export type NewRemovedUser = Omit<IRemovedUser, 'id'> & { id: null };
