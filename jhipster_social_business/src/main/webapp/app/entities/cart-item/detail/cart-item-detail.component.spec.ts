import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CartItemDetailComponent } from './cart-item-detail.component';

describe('CartItem Management Detail Component', () => {
  let comp: CartItemDetailComponent;
  let fixture: ComponentFixture<CartItemDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CartItemDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./cart-item-detail.component').then(m => m.CartItemDetailComponent),
              resolve: { cartItem: () => of({ id: 2227 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CartItemDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CartItemDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load cartItem on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CartItemDetailComponent);

      // THEN
      expect(instance.cartItem()).toEqual(expect.objectContaining({ id: 2227 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
