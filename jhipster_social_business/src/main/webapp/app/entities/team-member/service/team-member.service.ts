import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITeamMember, NewTeamMember } from '../team-member.model';

export type PartialUpdateTeamMember = Partial<ITeamMember> & Pick<ITeamMember, 'id'>;

export type EntityResponseType = HttpResponse<ITeamMember>;
export type EntityArrayResponseType = HttpResponse<ITeamMember[]>;

@Injectable({ providedIn: 'root' })
export class TeamMemberService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/team-members');

  create(teamMember: NewTeamMember): Observable<EntityResponseType> {
    return this.http.post<ITeamMember>(this.resourceUrl, teamMember, { observe: 'response' });
  }

  update(teamMember: ITeamMember): Observable<EntityResponseType> {
    return this.http.put<ITeamMember>(`${this.resourceUrl}/${this.getTeamMemberIdentifier(teamMember)}`, teamMember, {
      observe: 'response',
    });
  }

  partialUpdate(teamMember: PartialUpdateTeamMember): Observable<EntityResponseType> {
    return this.http.patch<ITeamMember>(`${this.resourceUrl}/${this.getTeamMemberIdentifier(teamMember)}`, teamMember, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITeamMember>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITeamMember[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTeamMemberIdentifier(teamMember: Pick<ITeamMember, 'id'>): number {
    return teamMember.id;
  }

  compareTeamMember(o1: Pick<ITeamMember, 'id'> | null, o2: Pick<ITeamMember, 'id'> | null): boolean {
    return o1 && o2 ? this.getTeamMemberIdentifier(o1) === this.getTeamMemberIdentifier(o2) : o1 === o2;
  }

  addTeamMemberToCollectionIfMissing<Type extends Pick<ITeamMember, 'id'>>(
    teamMemberCollection: Type[],
    ...teamMembersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const teamMembers: Type[] = teamMembersToCheck.filter(isPresent);
    if (teamMembers.length > 0) {
      const teamMemberCollectionIdentifiers = teamMemberCollection.map(teamMemberItem => this.getTeamMemberIdentifier(teamMemberItem));
      const teamMembersToAdd = teamMembers.filter(teamMemberItem => {
        const teamMemberIdentifier = this.getTeamMemberIdentifier(teamMemberItem);
        if (teamMemberCollectionIdentifiers.includes(teamMemberIdentifier)) {
          return false;
        }
        teamMemberCollectionIdentifiers.push(teamMemberIdentifier);
        return true;
      });
      return [...teamMembersToAdd, ...teamMemberCollection];
    }
    return teamMemberCollection;
  }
}
