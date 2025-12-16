import dayjs from 'dayjs/esm';

import { IBotSession, NewBotSession } from './bot-session.model';

export const sampleWithRequiredData: IBotSession = {
  id: 2444,
  waPhoneNumber: 'plus fraternise',
  currentState: 'upset',
};

export const sampleWithPartialData: IBotSession = {
  id: 17906,
  waPhoneNumber: 'burgeon unnecessarily',
  currentState: 'yowza indeed truthfully',
  sessionData: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: IBotSession = {
  id: 13536,
  waPhoneNumber: 'dicker aw',
  currentState: 'thin',
  sessionData: '../fake-data/blob/hipster.txt',
  lastActiveAt: dayjs('2025-12-15T12:53'),
};

export const sampleWithNewData: NewBotSession = {
  waPhoneNumber: 'spook',
  currentState: 'however duh hmph',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
