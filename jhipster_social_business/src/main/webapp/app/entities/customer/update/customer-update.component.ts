import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITeamMember } from 'app/entities/team-member/team-member.model';
import { TeamMemberService } from 'app/entities/team-member/service/team-member.service';
import { UserRole } from 'app/entities/enumerations/user-role.model';
import { CustomerService } from '../service/customer.service';
import { ICustomer } from '../customer.model';
import { CustomerFormGroup, CustomerFormService } from './customer-form.service';

@Component({
  selector: 'jhi-customer-update',
  templateUrl: './customer-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CustomerUpdateComponent implements OnInit {
  isSaving = false;
  customer: ICustomer | null = null;
  userRoleValues = Object.keys(UserRole);

  teamMembersSharedCollection: ITeamMember[] = [];

  protected customerService = inject(CustomerService);
  protected customerFormService = inject(CustomerFormService);
  protected teamMemberService = inject(TeamMemberService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CustomerFormGroup = this.customerFormService.createCustomerFormGroup();

  compareTeamMember = (o1: ITeamMember | null, o2: ITeamMember | null): boolean => this.teamMemberService.compareTeamMember(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customer }) => {
      this.customer = customer;
      if (customer) {
        this.updateForm(customer);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const customer = this.customerFormService.getCustomer(this.editForm);
    if (customer.id !== null) {
      this.subscribeToSaveResponse(this.customerService.update(customer));
    } else {
      this.subscribeToSaveResponse(this.customerService.create(customer));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICustomer>>): void {
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

  protected updateForm(customer: ICustomer): void {
    this.customer = customer;
    this.customerFormService.resetForm(this.editForm, customer);

    this.teamMembersSharedCollection = this.teamMemberService.addTeamMemberToCollectionIfMissing<ITeamMember>(
      this.teamMembersSharedCollection,
      customer.addedBy,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.teamMemberService
      .query()
      .pipe(map((res: HttpResponse<ITeamMember[]>) => res.body ?? []))
      .pipe(
        map((teamMembers: ITeamMember[]) =>
          this.teamMemberService.addTeamMemberToCollectionIfMissing<ITeamMember>(teamMembers, this.customer?.addedBy),
        ),
      )
      .subscribe((teamMembers: ITeamMember[]) => (this.teamMembersSharedCollection = teamMembers));
  }
}
