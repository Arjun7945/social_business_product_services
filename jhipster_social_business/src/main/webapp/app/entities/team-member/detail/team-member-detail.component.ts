import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ITeamMember } from '../team-member.model';

@Component({
  selector: 'jhi-team-member-detail',
  templateUrl: './team-member-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class TeamMemberDetailComponent {
  teamMember = input<ITeamMember | null>(null);

  previousState(): void {
    window.history.back();
  }
}
