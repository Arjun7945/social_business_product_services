import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IFishProduct } from 'app/entities/fish-product/fish-product.model';
import { FishProductService } from 'app/entities/fish-product/service/fish-product.service';
import { IShoppingCart } from 'app/entities/shopping-cart/shopping-cart.model';
import { ShoppingCartService } from 'app/entities/shopping-cart/service/shopping-cart.service';
import { ICartItem } from '../cart-item.model';
import { CartItemService } from '../service/cart-item.service';
import { CartItemFormService } from './cart-item-form.service';

import { CartItemUpdateComponent } from './cart-item-update.component';

describe('CartItem Management Update Component', () => {
  let comp: CartItemUpdateComponent;
  let fixture: ComponentFixture<CartItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cartItemFormService: CartItemFormService;
  let cartItemService: CartItemService;
  let fishProductService: FishProductService;
  let shoppingCartService: ShoppingCartService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CartItemUpdateComponent],
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
      .overrideTemplate(CartItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CartItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cartItemFormService = TestBed.inject(CartItemFormService);
    cartItemService = TestBed.inject(CartItemService);
    fishProductService = TestBed.inject(FishProductService);
    shoppingCartService = TestBed.inject(ShoppingCartService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call FishProduct query and add missing value', () => {
      const cartItem: ICartItem = { id: 7701 };
      const product: IFishProduct = { id: 7384 };
      cartItem.product = product;

      const fishProductCollection: IFishProduct[] = [{ id: 7384 }];
      jest.spyOn(fishProductService, 'query').mockReturnValue(of(new HttpResponse({ body: fishProductCollection })));
      const additionalFishProducts = [product];
      const expectedCollection: IFishProduct[] = [...additionalFishProducts, ...fishProductCollection];
      jest.spyOn(fishProductService, 'addFishProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ cartItem });
      comp.ngOnInit();

      expect(fishProductService.query).toHaveBeenCalled();
      expect(fishProductService.addFishProductToCollectionIfMissing).toHaveBeenCalledWith(
        fishProductCollection,
        ...additionalFishProducts.map(expect.objectContaining),
      );
      expect(comp.fishProductsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ShoppingCart query and add missing value', () => {
      const cartItem: ICartItem = { id: 7701 };
      const cart: IShoppingCart = { id: 21091 };
      cartItem.cart = cart;

      const shoppingCartCollection: IShoppingCart[] = [{ id: 21091 }];
      jest.spyOn(shoppingCartService, 'query').mockReturnValue(of(new HttpResponse({ body: shoppingCartCollection })));
      const additionalShoppingCarts = [cart];
      const expectedCollection: IShoppingCart[] = [...additionalShoppingCarts, ...shoppingCartCollection];
      jest.spyOn(shoppingCartService, 'addShoppingCartToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ cartItem });
      comp.ngOnInit();

      expect(shoppingCartService.query).toHaveBeenCalled();
      expect(shoppingCartService.addShoppingCartToCollectionIfMissing).toHaveBeenCalledWith(
        shoppingCartCollection,
        ...additionalShoppingCarts.map(expect.objectContaining),
      );
      expect(comp.shoppingCartsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const cartItem: ICartItem = { id: 7701 };
      const product: IFishProduct = { id: 7384 };
      cartItem.product = product;
      const cart: IShoppingCart = { id: 21091 };
      cartItem.cart = cart;

      activatedRoute.data = of({ cartItem });
      comp.ngOnInit();

      expect(comp.fishProductsSharedCollection).toContainEqual(product);
      expect(comp.shoppingCartsSharedCollection).toContainEqual(cart);
      expect(comp.cartItem).toEqual(cartItem);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICartItem>>();
      const cartItem = { id: 2227 };
      jest.spyOn(cartItemFormService, 'getCartItem').mockReturnValue(cartItem);
      jest.spyOn(cartItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cartItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cartItem }));
      saveSubject.complete();

      // THEN
      expect(cartItemFormService.getCartItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cartItemService.update).toHaveBeenCalledWith(expect.objectContaining(cartItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICartItem>>();
      const cartItem = { id: 2227 };
      jest.spyOn(cartItemFormService, 'getCartItem').mockReturnValue({ id: null });
      jest.spyOn(cartItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cartItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cartItem }));
      saveSubject.complete();

      // THEN
      expect(cartItemFormService.getCartItem).toHaveBeenCalled();
      expect(cartItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICartItem>>();
      const cartItem = { id: 2227 };
      jest.spyOn(cartItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cartItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cartItemService.update).toHaveBeenCalled();
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

    describe('compareShoppingCart', () => {
      it('Should forward to shoppingCartService', () => {
        const entity = { id: 21091 };
        const entity2 = { id: 19091 };
        jest.spyOn(shoppingCartService, 'compareShoppingCart');
        comp.compareShoppingCart(entity, entity2);
        expect(shoppingCartService.compareShoppingCart).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
