import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITeamMember, NewTeamMember } from '../team-member.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITeamMember for edit and NewTeamMemberFormGroupInput for create.
 */
type TeamMemberFormGroupInput = ITeamMember | PartialWithRequiredKeyOf<NewTeamMember>;

type TeamMemberFormDefaults = Pick<NewTeamMember, 'id' | 'isActive'>;

type TeamMemberFormGroupContent = {
  id: FormControl<ITeamMember['id'] | NewTeamMember['id']>;
  name: FormControl<ITeamMember['name']>;
  waPhoneNumber: FormControl<ITeamMember['waPhoneNumber']>;
  phoneNumber: FormControl<ITeamMember['phoneNumber']>;
  role: FormControl<ITeamMember['role']>;
  isActive: FormControl<ITeamMember['isActive']>;
};

export type TeamMemberFormGroup = FormGroup<TeamMemberFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TeamMemberFormService {
  createTeamMemberFormGroup(teamMember: TeamMemberFormGroupInput = { id: null }): TeamMemberFormGroup {
    const teamMemberRawValue = {
      ...this.getFormDefaults(),
      ...teamMember,
    };
    return new FormGroup<TeamMemberFormGroupContent>({
      id: new FormControl(
        { value: teamMemberRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(teamMemberRawValue.name, {
        validators: [Validators.required],
      }),
      waPhoneNumber: new FormControl(teamMemberRawValue.waPhoneNumber),
      phoneNumber: new FormControl(teamMemberRawValue.phoneNumber, {
        validators: [Validators.required],
      }),
      role: new FormControl(teamMemberRawValue.role, {
        validators: [Validators.required],
      }),
      isActive: new FormControl(teamMemberRawValue.isActive, {
        validators: [Validators.required],
      }),
    });
  }

  getTeamMember(form: TeamMemberFormGroup): ITeamMember | NewTeamMember {
    return form.getRawValue() as ITeamMember | NewTeamMember;
  }

  resetForm(form: TeamMemberFormGroup, teamMember: TeamMemberFormGroupInput): void {
    const teamMemberRawValue = { ...this.getFormDefaults(), ...teamMember };
    form.reset(
      {
        ...teamMemberRawValue,
        id: { value: teamMemberRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TeamMemberFormDefaults {
    return {
      id: null,
      isActive: false,
    };
  }
}
