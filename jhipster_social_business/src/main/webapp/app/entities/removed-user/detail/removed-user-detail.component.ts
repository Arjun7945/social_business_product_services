import { Component, input, inject } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IRemovedUser } from '../removed-user.model';
import { RemovedUserService } from '../service/removed-user.service';

@Component({
  selector: 'jhi-removed-user-detail',
  templateUrl: './removed-user-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class RemovedUserDetailComponent {
  removedUser = input<IRemovedUser | null>(null);
  protected removedUserService = inject(RemovedUserService);

  previousState(): void {
    window.history.back();
  }

  restore(): void {
    const user = this.removedUser();
    if (user?.id) {
      if (confirm('Are you sure you want to restore this user?')) {
        this.removedUserService.restore(user.id).subscribe(() => {
          this.previousState();
        });
      }
    }
  }
}
