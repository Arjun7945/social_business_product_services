import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IFishProduct } from 'app/entities/fish-product/fish-product.model';
import { FishProductService } from 'app/entities/fish-product/service/fish-product.service';
import { ICustomerOrder } from 'app/entities/customer-order/customer-order.model';
import { CustomerOrderService } from 'app/entities/customer-order/service/customer-order.service';
import { IOrderItem } from '../order-item.model';
import { OrderItemService } from '../service/order-item.service';
import { OrderItemFormService } from './order-item-form.service';

import { OrderItemUpdateComponent } from './order-item-update.component';

describe('OrderItem Management Update Component', () => {
  let comp: OrderItemUpdateComponent;
  let fixture: ComponentFixture<OrderItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orderItemFormService: OrderItemFormService;
  let orderItemService: OrderItemService;
  let fishProductService: FishProductService;
  let customerOrderService: CustomerOrderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [OrderItemUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(OrderItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orderItemFormService = TestBed.inject(OrderItemFormService);
    orderItemService = TestBed.inject(OrderItemService);
    fishProductService = TestBed.inject(FishProductService);
    customerOrderService = TestBed.inject(CustomerOrderService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call FishProduct query and add missing value', () => {
      const orderItem: IOrderItem = { id: 123 };
      const product: IFishProduct = { id: 7384 };
      orderItem.product = product;

      const fishProductCollection: IFishProduct[] = [{ id: 7384 }];
      jest.spyOn(fishProductService, 'query').mockReturnValue(of(new HttpResponse({ body: fishProductCollection })));
      const additionalFishProducts = [product];
      const expectedCollection: IFishProduct[] = [...additionalFishProducts, ...fishProductCollection];
      jest.spyOn(fishProductService, 'addFishProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      expect(fishProductService.query).toHaveBeenCalled();
      expect(fishProductService.addFishProductToCollectionIfMissing).toHaveBeenCalledWith(
        fishProductCollection,
        ...additionalFishProducts.map(expect.objectContaining),
      );
      expect(comp.fishProductsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call CustomerOrder query and add missing value', () => {
      const orderItem: IOrderItem = { id: 123 };
      const order: ICustomerOrder = { id: 18791 };
      orderItem.order = order;

      const customerOrderCollection: ICustomerOrder[] = [{ id: 18791 }];
      jest.spyOn(customerOrderService, 'query').mockReturnValue(of(new HttpResponse({ body: customerOrderCollection })));
      const additionalCustomerOrders = [order];
      const expectedCollection: ICustomerOrder[] = [...additionalCustomerOrders, ...customerOrderCollection];
      jest.spyOn(customerOrderService, 'addCustomerOrderToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      expect(customerOrderService.query).toHaveBeenCalled();
      expect(customerOrderService.addCustomerOrderToCollectionIfMissing).toHaveBeenCalledWith(
        customerOrderCollection,
        ...additionalCustomerOrders.map(expect.objectContaining),
      );
      expect(comp.customerOrdersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const orderItem: IOrderItem = { id: 123 };
      const product: IFishProduct = { id: 7384 };
      orderItem.product = product;
      const order: ICustomerOrder = { id: 18791 };
      orderItem.order = order;

      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      expect(comp.fishProductsSharedCollection).toContainEqual(product);
      expect(comp.customerOrdersSharedCollection).toContainEqual(order);
      expect(comp.orderItem).toEqual(orderItem);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderItem>>();
      const orderItem = { id: 25971 };
      jest.spyOn(orderItemFormService, 'getOrderItem').mockReturnValue(orderItem);
      jest.spyOn(orderItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderItem }));
      saveSubject.complete();

      // THEN
      expect(orderItemFormService.getOrderItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(orderItemService.update).toHaveBeenCalledWith(expect.objectContaining(orderItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderItem>>();
      const orderItem = { id: 25971 };
      jest.spyOn(orderItemFormService, 'getOrderItem').mockReturnValue({ id: null });
      jest.spyOn(orderItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderItem }));
      saveSubject.complete();

      // THEN
      expect(orderItemFormService.getOrderItem).toHaveBeenCalled();
      expect(orderItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderItem>>();
      const orderItem = { id: 25971 };
      jest.spyOn(orderItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orderItemService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareFishProduct', () => {
      it('Should forward to fishProductService', () => {
        const entity = { id: 7384 };
        const entity2 = { id: 15405 };
        jest.spyOn(fishProductService, 'compareFishProduct');
        comp.compareFishProduct(entity, entity2);
        expect(fishProductService.compareFishProduct).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCustomerOrder', () => {
      it('Should forward to customerOrderService', () => {
        const entity = { id: 18791 };
        const entity2 = { id: 9643 };
        jest.spyOn(customerOrderService, 'compareCustomerOrder');
        comp.compareCustomerOrder(entity, entity2);
        expect(customerOrderService.compareCustomerOrder).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
