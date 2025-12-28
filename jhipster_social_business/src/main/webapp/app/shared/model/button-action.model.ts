import dayjs from 'dayjs';

export interface IButtonAction {
  id?: number;
  waMessageId?: string;
  buttonId?: string | null;
  clickedAt?: dayjs.Dayjs | null;
  clickedBy?: string | null;
}

export const defaultValue: Readonly<IButtonAction> = {};
