import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { FishProductService } from '../service/fish-product.service';
import { IFishProduct } from '../fish-product.model';
import { FishProductFormService } from './fish-product-form.service';

import { FishProductUpdateComponent } from './fish-product-update.component';

describe('FishProduct Management Update Component', () => {
  let comp: FishProductUpdateComponent;
  let fixture: ComponentFixture<FishProductUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let fishProductFormService: FishProductFormService;
  let fishProductService: FishProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FishProductUpdateComponent],
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
      .overrideTemplate(FishProductUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FishProductUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fishProductFormService = TestBed.inject(FishProductFormService);
    fishProductService = TestBed.inject(FishProductService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const fishProduct: IFishProduct = { id: 15405 };

      activatedRoute.data = of({ fishProduct });
      comp.ngOnInit();

      expect(comp.fishProduct).toEqual(fishProduct);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFishProduct>>();
      const fishProduct = { id: 7384 };
      jest.spyOn(fishProductFormService, 'getFishProduct').mockReturnValue(fishProduct);
      jest.spyOn(fishProductService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fishProduct });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fishProduct }));
      saveSubject.complete();

      // THEN
      expect(fishProductFormService.getFishProduct).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(fishProductService.update).toHaveBeenCalledWith(expect.objectContaining(fishProduct));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFishProduct>>();
      const fishProduct = { id: 7384 };
      jest.spyOn(fishProductFormService, 'getFishProduct').mockReturnValue({ id: null });
      jest.spyOn(fishProductService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fishProduct: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fishProduct }));
      saveSubject.complete();

      // THEN
      expect(fishProductFormService.getFishProduct).toHaveBeenCalled();
      expect(fishProductService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFishProduct>>();
      const fishProduct = { id: 7384 };
      jest.spyOn(fishProductService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fishProduct });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(fishProductService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
