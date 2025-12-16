import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBotSession, NewBotSession } from '../bot-session.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBotSession for edit and NewBotSessionFormGroupInput for create.
 */
type BotSessionFormGroupInput = IBotSession | PartialWithRequiredKeyOf<NewBotSession>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBotSession | NewBotSession> = Omit<T, 'lastActiveAt'> & {
  lastActiveAt?: string | null;
};

type BotSessionFormRawValue = FormValueOf<IBotSession>;

type NewBotSessionFormRawValue = FormValueOf<NewBotSession>;

type BotSessionFormDefaults = Pick<NewBotSession, 'id' | 'lastActiveAt'>;

type BotSessionFormGroupContent = {
  id: FormControl<BotSessionFormRawValue['id'] | NewBotSession['id']>;
  waPhoneNumber: FormControl<BotSessionFormRawValue['waPhoneNumber']>;
  currentState: FormControl<BotSessionFormRawValue['currentState']>;
  sessionData: FormControl<BotSessionFormRawValue['sessionData']>;
  lastActiveAt: FormControl<BotSessionFormRawValue['lastActiveAt']>;
};

export type BotSessionFormGroup = FormGroup<BotSessionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BotSessionFormService {
  createBotSessionFormGroup(botSession: BotSessionFormGroupInput = { id: null }): BotSessionFormGroup {
    const botSessionRawValue = this.convertBotSessionToBotSessionRawValue({
      ...this.getFormDefaults(),
      ...botSession,
    });
    return new FormGroup<BotSessionFormGroupContent>({
      id: new FormControl(
        { value: botSessionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      waPhoneNumber: new FormControl(botSessionRawValue.waPhoneNumber, {
        validators: [Validators.required],
      }),
      currentState: new FormControl(botSessionRawValue.currentState, {
        validators: [Validators.required],
      }),
      sessionData: new FormControl(botSessionRawValue.sessionData),
      lastActiveAt: new FormControl(botSessionRawValue.lastActiveAt),
    });
  }

  getBotSession(form: BotSessionFormGroup): IBotSession | NewBotSession {
    return this.convertBotSessionRawValueToBotSession(form.getRawValue() as BotSessionFormRawValue | NewBotSessionFormRawValue);
  }

  resetForm(form: BotSessionFormGroup, botSession: BotSessionFormGroupInput): void {
    const botSessionRawValue = this.convertBotSessionToBotSessionRawValue({ ...this.getFormDefaults(), ...botSession });
    form.reset(
      {
        ...botSessionRawValue,
        id: { value: botSessionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BotSessionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      lastActiveAt: currentTime,
    };
  }

  private convertBotSessionRawValueToBotSession(
    rawBotSession: BotSessionFormRawValue | NewBotSessionFormRawValue,
  ): IBotSession | NewBotSession {
    return {
      ...rawBotSession,
      lastActiveAt: dayjs(rawBotSession.lastActiveAt, DATE_TIME_FORMAT),
    };
  }

  private convertBotSessionToBotSessionRawValue(
    botSession: IBotSession | (Partial<NewBotSession> & BotSessionFormDefaults),
  ): BotSessionFormRawValue | PartialWithRequiredKeyOf<NewBotSessionFormRawValue> {
    return {
      ...botSession,
      lastActiveAt: botSession.lastActiveAt ? botSession.lastActiveAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
