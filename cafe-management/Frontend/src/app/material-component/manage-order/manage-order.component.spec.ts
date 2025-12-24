import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageOrderComponent } from './manage-order.component';
import { FormBuilder } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { CategoryService } from 'src/app/services/category.service';
import { ProductService } from 'src/app/services/product.service';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { SnackBarService } from 'src/app/services/snack-bar.service';
import { BillService } from 'src/app/services/bill.service';

class MockCategoryService { getFilteredCategory(){ return { subscribe: () => {} }; } }
class MockProductService {
  getProductByCategory(){ return { subscribe: () => {} }; }
  getById(){ return { subscribe: () => {} }; }
}
class MockNgxUiLoaderService { start(){ } stop(){ } }
class MockSnackBarService { openSnackBar(){ } }
class MockBillService {
  generateReport(){ return { subscribe: () => {} }; }
  getPdf(){ return { subscribe: () => {} }; }
  saveDraft(){ return { subscribe: () => {} }; }
  getDrafts(){ return { subscribe: () => {} }; }
  getDraftById(){ return { subscribe: () => {} }; }
  deleteDraft(){ return { subscribe: () => {} }; }
}

describe('ManageOrderComponent', () => {
  let component: ManageOrderComponent;
  let fixture: ComponentFixture<ManageOrderComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ManageOrderComponent],
      providers: [
        FormBuilder,
        { provide: CategoryService, useClass: MockCategoryService },
        { provide: ProductService, useClass: MockProductService },
        { provide: NgxUiLoaderService, useClass: MockNgxUiLoaderService },
        { provide: SnackBarService, useClass: MockSnackBarService },
        { provide: BillService, useClass: MockBillService },
      ],
      schemas: [NO_ERRORS_SCHEMA]
    });
    fixture = TestBed.createComponent(ManageOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
