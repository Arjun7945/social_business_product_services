import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IButtonAction, NewButtonAction } from '../button-action.model';

export type PartialUpdateButtonAction = Partial<IButtonAction> & Pick<IButtonAction, 'id'>;

type RestOf<T extends IButtonAction | NewButtonAction> = Omit<T, 'clickedAt'> & {
  clickedAt?: string | null;
};

export type RestButtonAction = RestOf<IButtonAction>;

export type NewRestButtonAction = RestOf<NewButtonAction>;

export type PartialUpdateRestButtonAction = RestOf<PartialUpdateButtonAction>;

export type EntityResponseType = HttpResponse<IButtonAction>;
export type EntityArrayResponseType = HttpResponse<IButtonAction[]>;

@Injectable({ providedIn: 'root' })
export class ButtonActionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/button-actions');

  create(buttonAction: NewButtonAction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(buttonAction);
    return this.http
      .post<RestButtonAction>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(buttonAction: IButtonAction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(buttonAction);
    return this.http
      .put<RestButtonAction>(`${this.resourceUrl}/${this.getButtonActionIdentifier(buttonAction)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(buttonAction: PartialUpdateButtonAction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(buttonAction);
    return this.http
      .patch<RestButtonAction>(`${this.resourceUrl}/${this.getButtonActionIdentifier(buttonAction)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestButtonAction>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestButtonAction[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getButtonActionIdentifier(buttonAction: Pick<IButtonAction, 'id'>): number {
    return buttonAction.id;
  }

  compareButtonAction(o1: Pick<IButtonAction, 'id'> | null, o2: Pick<IButtonAction, 'id'> | null): boolean {
    return o1 && o2 ? this.getButtonActionIdentifier(o1) === this.getButtonActionIdentifier(o2) : o1 === o2;
  }

  addButtonActionToCollectionIfMissing<Type extends Pick<IButtonAction, 'id'>>(
    buttonActionCollection: Type[],
    ...buttonActionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const buttonActions: Type[] = buttonActionsToCheck.filter(isPresent);
    if (buttonActions.length > 0) {
      const buttonActionCollectionIdentifiers = buttonActionCollection.map(buttonActionItem =>
        this.getButtonActionIdentifier(buttonActionItem),
      );
      const buttonActionsToAdd = buttonActions.filter(buttonActionItem => {
        const buttonActionIdentifier = this.getButtonActionIdentifier(buttonActionItem);
        if (buttonActionCollectionIdentifiers.includes(buttonActionIdentifier)) {
          return false;
        }
        buttonActionCollectionIdentifiers.push(buttonActionIdentifier);
        return true;
      });
      return [...buttonActionsToAdd, ...buttonActionCollection];
    }
    return buttonActionCollection;
  }

  protected convertDateFromClient<T extends IButtonAction | NewButtonAction | PartialUpdateButtonAction>(buttonAction: T): RestOf<T> {
    return {
      ...buttonAction,
      clickedAt: buttonAction.clickedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restButtonAction: RestButtonAction): IButtonAction {
    return {
      ...restButtonAction,
      clickedAt: restButtonAction.clickedAt ? dayjs(restButtonAction.clickedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestButtonAction>): HttpResponse<IButtonAction> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestButtonAction[]>): HttpResponse<IButtonAction[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
