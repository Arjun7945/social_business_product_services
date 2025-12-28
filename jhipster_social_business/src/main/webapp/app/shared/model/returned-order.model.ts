import dayjs from 'dayjs';
import { ICustomerOrder } from 'app/shared/model/customer-order.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { ReturnProductStatus } from 'app/shared/model/enumerations/return-product-status.model';

export interface IReturnedOrder {
  id?: number;
  returnDate?: dayjs.Dayjs;
  paymentReceivedMode?: string | null;
  paymentReturnedMode?: string | null;
  productClaimStatus?: keyof typeof ReturnProductStatus;
  refundAmount?: number | null;
  order?: ICustomerOrder;
  customer?: ICustomer;
}

export const defaultValue: Readonly<IReturnedOrder> = {};
