import dayjs from 'dayjs/esm';

export interface IBotSession {
  id: number;
  waPhoneNumber?: string | null;
  currentState?: string | null;
  sessionData?: string | null;
  lastActiveAt?: dayjs.Dayjs | null;
}

export type NewBotSession = Omit<IBotSession, 'id'> & { id: null };
