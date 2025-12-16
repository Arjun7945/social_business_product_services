import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../bot-session.test-samples';

import { BotSessionFormService } from './bot-session-form.service';

describe('BotSession Form Service', () => {
  let service: BotSessionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BotSessionFormService);
  });

  describe('Service methods', () => {
    describe('createBotSessionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBotSessionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            waPhoneNumber: expect.any(Object),
            currentState: expect.any(Object),
            sessionData: expect.any(Object),
            lastActiveAt: expect.any(Object),
          }),
        );
      });

      it('passing IBotSession should create a new form with FormGroup', () => {
        const formGroup = service.createBotSessionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            waPhoneNumber: expect.any(Object),
            currentState: expect.any(Object),
            sessionData: expect.any(Object),
            lastActiveAt: expect.any(Object),
          }),
        );
      });
    });

    describe('getBotSession', () => {
      it('should return NewBotSession for default BotSession initial value', () => {
        const formGroup = service.createBotSessionFormGroup(sampleWithNewData);

        const botSession = service.getBotSession(formGroup) as any;

        expect(botSession).toMatchObject(sampleWithNewData);
      });

      it('should return NewBotSession for empty BotSession initial value', () => {
        const formGroup = service.createBotSessionFormGroup();

        const botSession = service.getBotSession(formGroup) as any;

        expect(botSession).toMatchObject({});
      });

      it('should return IBotSession', () => {
        const formGroup = service.createBotSessionFormGroup(sampleWithRequiredData);

        const botSession = service.getBotSession(formGroup) as any;

        expect(botSession).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBotSession should not enable id FormControl', () => {
        const formGroup = service.createBotSessionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBotSession should disable id FormControl', () => {
        const formGroup = service.createBotSessionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
