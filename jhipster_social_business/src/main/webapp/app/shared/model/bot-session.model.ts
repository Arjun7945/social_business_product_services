import dayjs from 'dayjs';

export interface IBotSession {
  id?: number;
  waPhoneNumber?: string;
  currentState?: string;
  sessionData?: string | null;
  lastActiveAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IBotSession> = {};
