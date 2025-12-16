import { UserRole } from 'app/entities/enumerations/user-role.model';

export interface ITeamMember {
  id: number;
  name?: string | null;
  waPhoneNumber?: string | null;
  phoneNumber?: string | null;
  role?: keyof typeof UserRole | null;
  isActive?: boolean | null;
}

export type NewTeamMember = Omit<ITeamMember, 'id'> & { id: null };
