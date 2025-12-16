import { ITeamMember, NewTeamMember } from './team-member.model';

export const sampleWithRequiredData: ITeamMember = {
  id: 2894,
  name: 'but morbidity',
  phoneNumber: 'little hm kinase',
  role: 'DEVELOPER',
  isActive: true,
};

export const sampleWithPartialData: ITeamMember = {
  id: 24739,
  name: 'upliftingly',
  phoneNumber: 'forenenst towards until',
  role: 'ASSISTANT_ADMIN',
  isActive: false,
};

export const sampleWithFullData: ITeamMember = {
  id: 10195,
  name: 'scheme till backburn',
  waPhoneNumber: 'inasmuch',
  phoneNumber: 'noteworthy',
  role: 'ADMIN',
  isActive: false,
};

export const sampleWithNewData: NewTeamMember = {
  name: 'opposite',
  phoneNumber: 'fortunately scaffold',
  role: 'CUSTOMER',
  isActive: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
