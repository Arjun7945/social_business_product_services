import dayjs from 'dayjs';
import { ITeamMember } from 'app/shared/model/team-member.model';
import { IDeliveryZone } from 'app/shared/model/delivery-zone.model';
import { UserRole } from 'app/shared/model/enumerations/user-role.model';

export interface ICustomer {
  id?: number;
  waPhoneNumber?: string;
  name?: string | null;
  phoneNumber?: string | null;
  locationLat?: number | null;
  locationLon?: number | null;
  address?: string | null;
  distanceFromBusinessKm?: number | null;
  isPincodeValid?: boolean;
  role?: keyof typeof UserRole;
  joinedAt?: dayjs.Dayjs | null;
  lastInteractionAt?: dayjs.Dayjs | null;
  addedBy?: ITeamMember | null;
  zone?: IDeliveryZone | null;
}

export const defaultValue: Readonly<ICustomer> = {
  isPincodeValid: false,
};
