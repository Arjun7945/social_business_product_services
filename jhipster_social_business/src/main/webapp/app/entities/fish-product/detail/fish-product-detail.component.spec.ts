import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { FishProductDetailComponent } from './fish-product-detail.component';

describe('FishProduct Management Detail Component', () => {
  let comp: FishProductDetailComponent;
  let fixture: ComponentFixture<FishProductDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FishProductDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./fish-product-detail.component').then(m => m.FishProductDetailComponent),
              resolve: { fishProduct: () => of({ id: 7384 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(FishProductDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FishProductDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load fishProduct on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', FishProductDetailComponent);

      // THEN
      expect(instance.fishProduct()).toEqual(expect.objectContaining({ id: 7384 }));
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
