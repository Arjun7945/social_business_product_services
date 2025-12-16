import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITeamMember } from '../team-member.model';
import { TeamMemberService } from '../service/team-member.service';

@Component({
  templateUrl: './team-member-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TeamMemberDeleteDialogComponent {
  teamMember?: ITeamMember;

  protected teamMemberService = inject(TeamMemberService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.teamMemberService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
