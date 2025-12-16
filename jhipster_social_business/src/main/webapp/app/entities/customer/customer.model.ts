import dayjs from 'dayjs/esm';
import { ITeamMember } from 'app/entities/team-member/team-member.model';
import { UserRole } from 'app/entities/enumerations/user-role.model';

export interface ICustomer {
  id: number;
  waPhoneNumber?: string | null;
  name?: string | null;
  phoneNumber?: string | null;
  locationLat?: number | null;
  locationLon?: number | null;
  address?: string | null;
  distanceFromBusinessKm?: number | null;
  isPincodeValid?: boolean | null;
  role?: keyof typeof UserRole | null;
  joinedAt?: dayjs.Dayjs | null;
  lastInteractionAt?: dayjs.Dayjs | null;
  addedBy?: Pick<ITeamMember, 'id' | 'name'> | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
