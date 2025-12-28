import dayjs from 'dayjs';
import { IProductImage } from 'app/shared/model/product-image.model';

export interface IFishProduct {
  id?: number;
  name?: string;
  pricePerKg?: number;
  availableQuantity?: number | null;
  description?: string | null;
  isAvailable?: boolean;
  createdAt?: dayjs.Dayjs | null;
  image?: IProductImage | null;
}

export const defaultValue: Readonly<IFishProduct> = {
  isAvailable: false,
};
