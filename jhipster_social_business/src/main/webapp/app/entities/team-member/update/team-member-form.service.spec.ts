import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../team-member.test-samples';

import { TeamMemberFormService } from './team-member-form.service';

describe('TeamMember Form Service', () => {
  let service: TeamMemberFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TeamMemberFormService);
  });

  describe('Service methods', () => {
    describe('createTeamMemberFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTeamMemberFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            waPhoneNumber: expect.any(Object),
            phoneNumber: expect.any(Object),
            role: expect.any(Object),
            isActive: expect.any(Object),
          }),
        );
      });

      it('passing ITeamMember should create a new form with FormGroup', () => {
        const formGroup = service.createTeamMemberFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            waPhoneNumber: expect.any(Object),
            phoneNumber: expect.any(Object),
            role: expect.any(Object),
            isActive: expect.any(Object),
          }),
        );
      });
    });

    describe('getTeamMember', () => {
      it('should return NewTeamMember for default TeamMember initial value', () => {
        const formGroup = service.createTeamMemberFormGroup(sampleWithNewData);

        const teamMember = service.getTeamMember(formGroup) as any;

        expect(teamMember).toMatchObject(sampleWithNewData);
      });

      it('should return NewTeamMember for empty TeamMember initial value', () => {
        const formGroup = service.createTeamMemberFormGroup();

        const teamMember = service.getTeamMember(formGroup) as any;

        expect(teamMember).toMatchObject({});
      });

      it('should return ITeamMember', () => {
        const formGroup = service.createTeamMemberFormGroup(sampleWithRequiredData);

        const teamMember = service.getTeamMember(formGroup) as any;

        expect(teamMember).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITeamMember should not enable id FormControl', () => {
        const formGroup = service.createTeamMemberFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTeamMember should disable id FormControl', () => {
        const formGroup = service.createTeamMemberFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
