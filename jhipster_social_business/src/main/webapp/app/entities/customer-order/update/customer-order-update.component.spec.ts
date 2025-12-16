import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITeamMember } from 'app/entities/team-member/team-member.model';
import { TeamMemberService } from 'app/entities/team-member/service/team-member.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { ICustomerOrder } from '../customer-order.model';
import { CustomerOrderService } from '../service/customer-order.service';
import { CustomerOrderFormService } from './customer-order-form.service';

import { CustomerOrderUpdateComponent } from './customer-order-update.component';

describe('CustomerOrder Management Update Component', () => {
  let comp: CustomerOrderUpdateComponent;
  let fixture: ComponentFixture<CustomerOrderUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let customerOrderFormService: CustomerOrderFormService;
  let customerOrderService: CustomerOrderService;
  let teamMemberService: TeamMemberService;
  let customerService: CustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CustomerOrderUpdateComponent],
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
      .overrideTemplate(CustomerOrderUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CustomerOrderUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    customerOrderFormService = TestBed.inject(CustomerOrderFormService);
    customerOrderService = TestBed.inject(CustomerOrderService);
    teamMemberService = TestBed.inject(TeamMemberService);
    customerService = TestBed.inject(CustomerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TeamMember query and add missing value', () => {
      const customerOrder: ICustomerOrder = { id: 9643 };
      const deliveryPerson: ITeamMember = { id: 11287 };
      customerOrder.deliveryPerson = deliveryPerson;

      const teamMemberCollection: ITeamMember[] = [{ id: 11287 }];
      jest.spyOn(teamMemberService, 'query').mockReturnValue(of(new HttpResponse({ body: teamMemberCollection })));
      const additionalTeamMembers = [deliveryPerson];
      const expectedCollection: ITeamMember[] = [...additionalTeamMembers, ...teamMemberCollection];
      jest.spyOn(teamMemberService, 'addTeamMemberToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ customerOrder });
      comp.ngOnInit();

      expect(teamMemberService.query).toHaveBeenCalled();
      expect(teamMemberService.addTeamMemberToCollectionIfMissing).toHaveBeenCalledWith(
        teamMemberCollection,
        ...additionalTeamMembers.map(expect.objectContaining),
      );
      expect(comp.teamMembersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Customer query and add missing value', () => {
      const customerOrder: ICustomerOrder = { id: 9643 };
      const customer: ICustomer = { id: 26915 };
      customerOrder.customer = customer;

      const customerCollection: ICustomer[] = [{ id: 26915 }];
      jest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      jest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ customerOrder });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(
        customerCollection,
        ...additionalCustomers.map(expect.objectContaining),
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const customerOrder: ICustomerOrder = { id: 9643 };
      const deliveryPerson: ITeamMember = { id: 11287 };
      customerOrder.deliveryPerson = deliveryPerson;
      const customer: ICustomer = { id: 26915 };
      customerOrder.customer = customer;

      activatedRoute.data = of({ customerOrder });
      comp.ngOnInit();

      expect(comp.teamMembersSharedCollection).toContainEqual(deliveryPerson);
      expect(comp.customersSharedCollection).toContainEqual(customer);
      expect(comp.customerOrder).toEqual(customerOrder);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICustomerOrder>>();
      const customerOrder = { id: 18791 };
      jest.spyOn(customerOrderFormService, 'getCustomerOrder').mockReturnValue(customerOrder);
      jest.spyOn(customerOrderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customerOrder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: customerOrder }));
      saveSubject.complete();

      // THEN
      expect(customerOrderFormService.getCustomerOrder).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(customerOrderService.update).toHaveBeenCalledWith(expect.objectContaining(customerOrder));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICustomerOrder>>();
      const customerOrder = { id: 18791 };
      jest.spyOn(customerOrderFormService, 'getCustomerOrder').mockReturnValue({ id: null });
      jest.spyOn(customerOrderService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customerOrder: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: customerOrder }));
      saveSubject.complete();

      // THEN
      expect(customerOrderFormService.getCustomerOrder).toHaveBeenCalled();
      expect(customerOrderService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICustomerOrder>>();
      const customerOrder = { id: 18791 };
      jest.spyOn(customerOrderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customerOrder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(customerOrderService.update).toHaveBeenCalled();
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

    describe('compareCustomer', () => {
      it('Should forward to customerService', () => {
        const entity = { id: 26915 };
        const entity2 = { id: 21032 };
        jest.spyOn(customerService, 'compareCustomer');
        comp.compareCustomer(entity, entity2);
        expect(customerService.compareCustomer).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
