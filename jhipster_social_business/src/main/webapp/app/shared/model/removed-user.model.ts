import dayjs from 'dayjs';
import { UserRole } from 'app/shared/model/enumerations/user-role.model';
import { AccountStatus } from 'app/shared/model/enumerations/account-status.model';

export interface IRemovedUser {
  id?: number;
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
  distanceFromBusinessKm?: number | null;
  isPincodeValid?: boolean | null;
  zoneName?: string | null;
  zoneId?: number | null;
}

export const defaultValue: Readonly<IRemovedUser> = {
  isPincodeValid: false,
};
