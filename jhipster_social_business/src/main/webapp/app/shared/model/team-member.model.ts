import { UserRole } from 'app/shared/model/enumerations/user-role.model';

export interface ITeamMember {
  id?: number;
  name?: string;
  waPhoneNumber?: string | null;
  phoneNumber?: string;
  role?: keyof typeof UserRole;
  isActive?: boolean;
}

export const defaultValue: Readonly<ITeamMember> = {
  isActive: false,
};
