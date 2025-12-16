import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IBotSession } from '../bot-session.model';
import { BotSessionService } from '../service/bot-session.service';

@Component({
  templateUrl: './bot-session-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class BotSessionDeleteDialogComponent {
  botSession?: IBotSession;

  protected botSessionService = inject(BotSessionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.botSessionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
