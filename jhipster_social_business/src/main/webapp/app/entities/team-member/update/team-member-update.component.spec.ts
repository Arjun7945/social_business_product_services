import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { TeamMemberService } from '../service/team-member.service';
import { ITeamMember } from '../team-member.model';
import { TeamMemberFormService } from './team-member-form.service';

import { TeamMemberUpdateComponent } from './team-member-update.component';

describe('TeamMember Management Update Component', () => {
  let comp: TeamMemberUpdateComponent;
  let fixture: ComponentFixture<TeamMemberUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let teamMemberFormService: TeamMemberFormService;
  let teamMemberService: TeamMemberService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TeamMemberUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(TeamMemberUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeamMemberUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    teamMemberFormService = TestBed.inject(TeamMemberFormService);
    teamMemberService = TestBed.inject(TeamMemberService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const teamMember: ITeamMember = { id: 22246 };

      activatedRoute.data = of({ teamMember });
      comp.ngOnInit();

      expect(comp.teamMember).toEqual(teamMember);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamMember>>();
      const teamMember = { id: 11287 };
      jest.spyOn(teamMemberFormService, 'getTeamMember').mockReturnValue(teamMember);
      jest.spyOn(teamMemberService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamMember });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamMember }));
      saveSubject.complete();

      // THEN
      expect(teamMemberFormService.getTeamMember).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(teamMemberService.update).toHaveBeenCalledWith(expect.objectContaining(teamMember));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamMember>>();
      const teamMember = { id: 11287 };
      jest.spyOn(teamMemberFormService, 'getTeamMember').mockReturnValue({ id: null });
      jest.spyOn(teamMemberService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamMember: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamMember }));
      saveSubject.complete();

      // THEN
      expect(teamMemberFormService.getTeamMember).toHaveBeenCalled();
      expect(teamMemberService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamMember>>();
      const teamMember = { id: 11287 };
      jest.spyOn(teamMemberService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamMember });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(teamMemberService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
