import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { BotSessionService } from '../service/bot-session.service';
import { IBotSession } from '../bot-session.model';
import { BotSessionFormService } from './bot-session-form.service';

import { BotSessionUpdateComponent } from './bot-session-update.component';

describe('BotSession Management Update Component', () => {
  let comp: BotSessionUpdateComponent;
  let fixture: ComponentFixture<BotSessionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let botSessionFormService: BotSessionFormService;
  let botSessionService: BotSessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BotSessionUpdateComponent],
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
      .overrideTemplate(BotSessionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BotSessionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    botSessionFormService = TestBed.inject(BotSessionFormService);
    botSessionService = TestBed.inject(BotSessionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const botSession: IBotSession = { id: 26689 };

      activatedRoute.data = of({ botSession });
      comp.ngOnInit();

      expect(comp.botSession).toEqual(botSession);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBotSession>>();
      const botSession = { id: 30587 };
      jest.spyOn(botSessionFormService, 'getBotSession').mockReturnValue(botSession);
      jest.spyOn(botSessionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ botSession });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: botSession }));
      saveSubject.complete();

      // THEN
      expect(botSessionFormService.getBotSession).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(botSessionService.update).toHaveBeenCalledWith(expect.objectContaining(botSession));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBotSession>>();
      const botSession = { id: 30587 };
      jest.spyOn(botSessionFormService, 'getBotSession').mockReturnValue({ id: null });
      jest.spyOn(botSessionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ botSession: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: botSession }));
      saveSubject.complete();

      // THEN
      expect(botSessionFormService.getBotSession).toHaveBeenCalled();
      expect(botSessionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBotSession>>();
      const botSession = { id: 30587 };
      jest.spyOn(botSessionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ botSession });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(botSessionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
