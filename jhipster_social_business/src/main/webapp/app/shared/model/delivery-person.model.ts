import dayjs from 'dayjs';
import { ITeamMember } from 'app/shared/model/team-member.model';
import { IDeliveryZone } from 'app/shared/model/delivery-zone.model';
import { DeliveryStatus } from 'app/shared/model/enumerations/delivery-status.model';

export interface IDeliveryPerson {
  id?: number;
  name?: string;
  waPhoneNumber?: string | null;
  phoneNumber?: string;
  status?: keyof typeof DeliveryStatus;
  joinedAt?: dayjs.Dayjs | null;
  isActive?: boolean;
  addedBy?: ITeamMember | null;
  zone?: IDeliveryZone;
}

export const defaultValue: Readonly<IDeliveryPerson> = {
  isActive: false,
};
