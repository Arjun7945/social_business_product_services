import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBotSession, NewBotSession } from '../bot-session.model';

export type PartialUpdateBotSession = Partial<IBotSession> & Pick<IBotSession, 'id'>;

type RestOf<T extends IBotSession | NewBotSession> = Omit<T, 'lastActiveAt'> & {
  lastActiveAt?: string | null;
};

export type RestBotSession = RestOf<IBotSession>;

export type NewRestBotSession = RestOf<NewBotSession>;

export type PartialUpdateRestBotSession = RestOf<PartialUpdateBotSession>;

export type EntityResponseType = HttpResponse<IBotSession>;
export type EntityArrayResponseType = HttpResponse<IBotSession[]>;

@Injectable({ providedIn: 'root' })
export class BotSessionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/bot-sessions');

  create(botSession: NewBotSession): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(botSession);
    return this.http
      .post<RestBotSession>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(botSession: IBotSession): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(botSession);
    return this.http
      .put<RestBotSession>(`${this.resourceUrl}/${this.getBotSessionIdentifier(botSession)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(botSession: PartialUpdateBotSession): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(botSession);
    return this.http
      .patch<RestBotSession>(`${this.resourceUrl}/${this.getBotSessionIdentifier(botSession)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestBotSession>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestBotSession[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBotSessionIdentifier(botSession: Pick<IBotSession, 'id'>): number {
    return botSession.id;
  }

  compareBotSession(o1: Pick<IBotSession, 'id'> | null, o2: Pick<IBotSession, 'id'> | null): boolean {
    return o1 && o2 ? this.getBotSessionIdentifier(o1) === this.getBotSessionIdentifier(o2) : o1 === o2;
  }

  addBotSessionToCollectionIfMissing<Type extends Pick<IBotSession, 'id'>>(
    botSessionCollection: Type[],
    ...botSessionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const botSessions: Type[] = botSessionsToCheck.filter(isPresent);
    if (botSessions.length > 0) {
      const botSessionCollectionIdentifiers = botSessionCollection.map(botSessionItem => this.getBotSessionIdentifier(botSessionItem));
      const botSessionsToAdd = botSessions.filter(botSessionItem => {
        const botSessionIdentifier = this.getBotSessionIdentifier(botSessionItem);
        if (botSessionCollectionIdentifiers.includes(botSessionIdentifier)) {
          return false;
        }
        botSessionCollectionIdentifiers.push(botSessionIdentifier);
        return true;
      });
      return [...botSessionsToAdd, ...botSessionCollection];
    }
    return botSessionCollection;
  }

  protected convertDateFromClient<T extends IBotSession | NewBotSession | PartialUpdateBotSession>(botSession: T): RestOf<T> {
    return {
      ...botSession,
      lastActiveAt: botSession.lastActiveAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restBotSession: RestBotSession): IBotSession {
    return {
      ...restBotSession,
      lastActiveAt: restBotSession.lastActiveAt ? dayjs(restBotSession.lastActiveAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestBotSession>): HttpResponse<IBotSession> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestBotSession[]>): HttpResponse<IBotSession[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
