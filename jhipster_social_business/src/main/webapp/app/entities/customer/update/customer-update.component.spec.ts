import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITeamMember } from 'app/entities/team-member/team-member.model';
import { TeamMemberService } from 'app/entities/team-member/service/team-member.service';
import { CustomerService } from '../service/customer.service';
import { ICustomer } from '../customer.model';
import { CustomerFormService } from './customer-form.service';

import { CustomerUpdateComponent } from './customer-update.component';

describe('Customer Management Update Component', () => {
  let comp: CustomerUpdateComponent;
  let fixture: ComponentFixture<CustomerUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let customerFormService: CustomerFormService;
  let customerService: CustomerService;
  let teamMemberService: TeamMemberService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CustomerUpdateComponent],
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
      .overrideTemplate(CustomerUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CustomerUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    customerFormService = TestBed.inject(CustomerFormService);
    customerService = TestBed.inject(CustomerService);
    teamMemberService = TestBed.inject(TeamMemberService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TeamMember query and add missing value', () => {
      const customer: ICustomer = { id: 21032 };
      const addedBy: ITeamMember = { id: 11287 };
      customer.addedBy = addedBy;

      const teamMemberCollection: ITeamMember[] = [{ id: 11287 }];
      jest.spyOn(teamMemberService, 'query').mockReturnValue(of(new HttpResponse({ body: teamMemberCollection })));
      const additionalTeamMembers = [addedBy];
      const expectedCollection: ITeamMember[] = [...additionalTeamMembers, ...teamMemberCollection];
      jest.spyOn(teamMemberService, 'addTeamMemberToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ customer });
      comp.ngOnInit();

      expect(teamMemberService.query).toHaveBeenCalled();
      expect(teamMemberService.addTeamMemberToCollectionIfMissing).toHaveBeenCalledWith(
        teamMemberCollection,
        ...additionalTeamMembers.map(expect.objectContaining),
      );
      expect(comp.teamMembersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const customer: ICustomer = { id: 21032 };
      const addedBy: ITeamMember = { id: 11287 };
      customer.addedBy = addedBy;

      activatedRoute.data = of({ customer });
      comp.ngOnInit();

      expect(comp.teamMembersSharedCollection).toContainEqual(addedBy);
      expect(comp.customer).toEqual(customer);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICustomer>>();
      const customer = { id: 26915 };
      jest.spyOn(customerFormService, 'getCustomer').mockReturnValue(customer);
      jest.spyOn(customerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: customer }));
      saveSubject.complete();

      // THEN
      expect(customerFormService.getCustomer).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(customerService.update).toHaveBeenCalledWith(expect.objectContaining(customer));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICustomer>>();
      const customer = { id: 26915 };
      jest.spyOn(customerFormService, 'getCustomer').mockReturnValue({ id: null });
      jest.spyOn(customerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customer: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: customer }));
      saveSubject.complete();

      // THEN
      expect(customerFormService.getCustomer).toHaveBeenCalled();
      expect(customerService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICustomer>>();
      const customer = { id: 26915 };
      jest.spyOn(customerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(customerService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTeamMember', () => {
      it('Should forward to teamMemberService', () => {
        const entity = { id: 11287 };
        const entity2 = { id: 22246 };
        jest.spyOn(teamMemberService, 'compareTeamMember');
        comp.compareTeamMember(entity, entity2);
        expect(teamMemberService.compareTeamMember).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
