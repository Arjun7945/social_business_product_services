import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IFishProduct } from 'app/entities/fish-product/fish-product.model';
import { FishProductService } from 'app/entities/fish-product/service/fish-product.service';
import { IProductImage } from '../product-image.model';
import { ProductImageService } from '../service/product-image.service';
import { ProductImageFormGroup, ProductImageFormService } from './product-image-form.service';

@Component({
  selector: 'jhi-product-image-update',
  templateUrl: './product-image-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ProductImageUpdateComponent implements OnInit {
  isSaving = false;
  productImage: IProductImage | null = null;

  fishProductsSharedCollection: IFishProduct[] = [];

  protected productImageService = inject(ProductImageService);
  protected productImageFormService = inject(ProductImageFormService);
  protected fishProductService = inject(FishProductService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProductImageFormGroup = this.productImageFormService.createProductImageFormGroup();

  compareFishProduct = (o1: IFishProduct | null, o2: IFishProduct | null): boolean => this.fishProductService.compareFishProduct(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productImage }) => {
      this.productImage = productImage;
      if (productImage) {
        this.updateForm(productImage);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productImage = this.productImageFormService.getProductImage(this.editForm);
    if (productImage.id !== null) {
      this.subscribeToSaveResponse(this.productImageService.update(productImage));
    } else {
      this.subscribeToSaveResponse(this.productImageService.create(productImage));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductImage>>): void {
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

  protected updateForm(productImage: IProductImage): void {
    this.productImage = productImage;
    this.productImageFormService.resetForm(this.editForm, productImage);

    this.fishProductsSharedCollection = this.fishProductService.addFishProductToCollectionIfMissing<IFishProduct>(
      this.fishProductsSharedCollection,
      productImage.product,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.fishProductService
      .query()
      .pipe(map((res: HttpResponse<IFishProduct[]>) => res.body ?? []))
      .pipe(
        map((fishProducts: IFishProduct[]) =>
          this.fishProductService.addFishProductToCollectionIfMissing<IFishProduct>(fishProducts, this.productImage?.product),
        ),
      )
      .subscribe((fishProducts: IFishProduct[]) => (this.fishProductsSharedCollection = fishProducts));
  }
}
