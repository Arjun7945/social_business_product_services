import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { BotSessionService } from '../service/bot-session.service';
import { IBotSession } from '../bot-session.model';
import { BotSessionFormGroup, BotSessionFormService } from './bot-session-form.service';

@Component({
  selector: 'jhi-bot-session-update',
  templateUrl: './bot-session-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BotSessionUpdateComponent implements OnInit {
  isSaving = false;
  botSession: IBotSession | null = null;

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected botSessionService = inject(BotSessionService);
  protected botSessionFormService = inject(BotSessionFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BotSessionFormGroup = this.botSessionFormService.createBotSessionFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ botSession }) => {
      this.botSession = botSession;
      if (botSession) {
        this.updateForm(botSession);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(
          new EventWithContent<AlertError>('whatsappProductServiceApp.error', { ...err, key: `error.file.${err.key}` }),
        ),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const botSession = this.botSessionFormService.getBotSession(this.editForm);
    if (botSession.id !== null) {
      this.subscribeToSaveResponse(this.botSessionService.update(botSession));
    } else {
      this.subscribeToSaveResponse(this.botSessionService.create(botSession));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBotSession>>): void {
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

  protected updateForm(botSession: IBotSession): void {
    this.botSession = botSession;
    this.botSessionFormService.resetForm(this.editForm, botSession);
  }
}
