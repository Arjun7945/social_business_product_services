import dayjs from 'dayjs/esm';

export interface IFishProduct {
  id: number;
  name?: string | null;
  pricePerKg?: number | null;
  imageUrl?: string | null;
  description?: string | null;
  isAvailable?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
  version?: number | null;
}

export type NewFishProduct = Omit<IFishProduct, 'id'> & { id: null };
