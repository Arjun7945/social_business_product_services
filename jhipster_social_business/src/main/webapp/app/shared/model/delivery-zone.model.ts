export interface IDeliveryZone {
  id?: number;
  zoneName?: string;
  pincode?: string | null;
}

export const defaultValue: Readonly<IDeliveryZone> = {};
