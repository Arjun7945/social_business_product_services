import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { UserRole } from 'app/entities/enumerations/user-role.model';
import { ITeamMember } from '../team-member.model';
import { TeamMemberService } from '../service/team-member.service';
import { TeamMemberFormGroup, TeamMemberFormService } from './team-member-form.service';

@Component({
  selector: 'jhi-team-member-update',
  templateUrl: './team-member-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TeamMemberUpdateComponent implements OnInit {
  isSaving = false;
  teamMember: ITeamMember | null = null;
  userRoleValues = Object.keys(UserRole);

  protected teamMemberService = inject(TeamMemberService);
  protected teamMemberFormService = inject(TeamMemberFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TeamMemberFormGroup = this.teamMemberFormService.createTeamMemberFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teamMember }) => {
      this.teamMember = teamMember;
      if (teamMember) {
        this.updateForm(teamMember);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const teamMember = this.teamMemberFormService.getTeamMember(this.editForm);
    if (teamMember.id !== null) {
      this.subscribeToSaveResponse(this.teamMemberService.update(teamMember));
    } else {
      this.subscribeToSaveResponse(this.teamMemberService.create(teamMember));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeamMember>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(teamMember: ITeamMember): void {
    this.teamMember = teamMember;
    this.teamMemberFormService.resetForm(this.editForm, teamMember);
  }
}
