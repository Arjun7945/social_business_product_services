import dayjs from 'dayjs/esm';

export interface IButtonAction {
  id: number;
  waMessageId?: string | null;
  buttonId?: string | null;
  clickedAt?: dayjs.Dayjs | null;
  clickedBy?: string | null;
}

export type NewButtonAction = Omit<IButtonAction, 'id'> & { id: null };
