import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITeamMember } from 'app/entities/team-member/team-member.model';
import { TeamMemberService } from 'app/entities/team-member/service/team-member.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';
import { CustomerOrderService } from '../service/customer-order.service';
import { ICustomerOrder } from '../customer-order.model';
import { CustomerOrderFormGroup, CustomerOrderFormService } from './customer-order-form.service';

@Component({
  selector: 'jhi-customer-order-update',
  templateUrl: './customer-order-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CustomerOrderUpdateComponent implements OnInit {
  isSaving = false;
  customerOrder: ICustomerOrder | null = null;
  orderStatusValues = Object.keys(OrderStatus);

  teamMembersSharedCollection: ITeamMember[] = [];
  customersSharedCollection: ICustomer[] = [];

  protected customerOrderService = inject(CustomerOrderService);
  protected customerOrderFormService = inject(CustomerOrderFormService);
  protected teamMemberService = inject(TeamMemberService);
  protected customerService = inject(CustomerService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CustomerOrderFormGroup = this.customerOrderFormService.createCustomerOrderFormGroup();

  compareTeamMember = (o1: ITeamMember | null, o2: ITeamMember | null): boolean => this.teamMemberService.compareTeamMember(o1, o2);

  compareCustomer = (o1: ICustomer | null, o2: ICustomer | null): boolean => this.customerService.compareCustomer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customerOrder }) => {
      this.customerOrder = customerOrder;
      if (customerOrder) {
        this.updateForm(customerOrder);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const customerOrder = this.customerOrderFormService.getCustomerOrder(this.editForm);
    if (customerOrder.id !== null) {
      this.subscribeToSaveResponse(this.customerOrderService.update(customerOrder));
    } else {
      this.subscribeToSaveResponse(this.customerOrderService.create(customerOrder));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICustomerOrder>>): void {
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

  protected updateForm(customerOrder: ICustomerOrder): void {
    this.customerOrder = customerOrder;
    this.customerOrderFormService.resetForm(this.editForm, customerOrder);

    this.teamMembersSharedCollection = this.teamMemberService.addTeamMemberToCollectionIfMissing<ITeamMember>(
      this.teamMembersSharedCollection,
      customerOrder.deliveryPerson,
    );
    this.customersSharedCollection = this.customerService.addCustomerToCollectionIfMissing<ICustomer>(
      this.customersSharedCollection,
      customerOrder.customer,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.teamMemberService
      .query()
      .pipe(map((res: HttpResponse<ITeamMember[]>) => res.body ?? []))
      .pipe(
        map((teamMembers: ITeamMember[]) =>
          this.teamMemberService.addTeamMemberToCollectionIfMissing<ITeamMember>(teamMembers, this.customerOrder?.deliveryPerson),
        ),
      )
      .subscribe((teamMembers: ITeamMember[]) => (this.teamMembersSharedCollection = teamMembers));

    this.customerService
      .query()
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) =>
          this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, this.customerOrder?.customer),
        ),
      )
      .subscribe((customers: ICustomer[]) => (this.customersSharedCollection = customers));
  }
}
