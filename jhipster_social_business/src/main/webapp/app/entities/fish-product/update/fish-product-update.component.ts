import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IFishProduct } from '../fish-product.model';
import { FishProductService } from '../service/fish-product.service';
import { FishProductFormGroup, FishProductFormService } from './fish-product-form.service';

@Component({
  selector: 'jhi-fish-product-update',
  templateUrl: './fish-product-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class FishProductUpdateComponent implements OnInit {
  isSaving = false;
  fishProduct: IFishProduct | null = null;

  protected fishProductService = inject(FishProductService);
  protected fishProductFormService = inject(FishProductFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FishProductFormGroup = this.fishProductFormService.createFishProductFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ fishProduct }) => {
      this.fishProduct = fishProduct;
      if (fishProduct) {
        this.updateForm(fishProduct);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const fishProduct = this.fishProductFormService.getFishProduct(this.editForm);
    if (fishProduct.id !== null) {
      this.subscribeToSaveResponse(this.fishProductService.update(fishProduct));
    } else {
      this.subscribeToSaveResponse(this.fishProductService.create(fishProduct));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFishProduct>>): void {
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

  protected updateForm(fishProduct: IFishProduct): void {
    this.fishProduct = fishProduct;
    this.fishProductFormService.resetForm(this.editForm, fishProduct);
  }
}
