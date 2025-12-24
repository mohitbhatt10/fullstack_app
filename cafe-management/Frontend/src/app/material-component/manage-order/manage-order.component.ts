import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { saveAs } from 'file-saver';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { BillService } from 'src/app/services/bill.service';
import { CategoryService } from 'src/app/services/category.service';
import { ProductService } from 'src/app/services/product.service';
import { SnackBarService } from 'src/app/services/snack-bar.service';
import { GlobalConstants } from 'src/app/shared/global-constants';

@Component({
  selector: 'app-manage-order',
  templateUrl: './manage-order.component.html',
  styleUrls: ['./manage-order.component.scss']
})
export class ManageOrderComponent {

  displayedColumns: string[] = ['name', 'category', 'price', 'quantity', 'total', 'edit'];
  draftDisplayedColumns: string[] = ['id', 'name', 'email', 'total', 'actions'];
  dataSource:any = [];
  drafts:any = [];
  manageOrderForm:any = FormGroup;
  categories:any = [];
  products:any = [];
  price:any;
  totalAmount:number = 0;
  responseMessage:any;
  currentDraftId: number | null = null;

  constructor(
    private formBuider:FormBuilder,
    private categoryService: CategoryService,
      private productService: ProductService,
      private ngxService: NgxUiLoaderService,
      private snackBarService: SnackBarService,
      private billService: BillService
    ) {}

  ngOnInit(): void {
    this.ngxService.start();
    this.getCategories();
    this.getDrafts();
    this.manageOrderForm = this.formBuider.group({
      name: [null, [Validators.required, Validators.pattern(GlobalConstants.nameRegex)]],
      email: [null, [Validators.required, Validators.pattern(GlobalConstants.emailRegex)]],
      contactNumber: [null, [Validators.required, Validators.pattern(GlobalConstants.contactRegex)]],
      paymentMethod: [null, [Validators.required]],
      product: [null, [Validators.required]],
      category: [null, [Validators.required]],
      quantity: [null, [Validators.required]],
      price: [null, [Validators.required]],
      total: [0, [Validators.required]]
    });
  }

  getDrafts(){
    this.billService.getDrafts().subscribe((response:any)=>{
      this.drafts = response;
    },(error:any)=>{
      // drafts are optional UI, so keep this quiet but still show a toast
      console.log(error);
    })
  }

  getCategories(){
    this.categoryService.getFilteredCategory().subscribe((response:any)=>{
      this.ngxService.stop();
      this.categories = response;
    },(error:any)=>{
      this.ngxService.stop();
      console.log(error);
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackBarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }

  getProductsByCategory(value:any){
    this.productService.getProductByCategory(value.id).subscribe((response:any)=>{
      this.products = response;
      this.manageOrderForm.controls['price'].setValue('');
      this.manageOrderForm.controls['quantity'].setValue('');
      this.manageOrderForm.controls['total'].setValue(0);
    },(error:any)=>{
      this.ngxService.stop();
      console.log(error);
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackBarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }

  getProductDetails(value:any){
    this.productService.getById(value.id).subscribe((response:any)=>{
      this.price = response.price;
      this.manageOrderForm.controls['price'].setValue(response.price);
      this.manageOrderForm.controls['quantity'].setValue('1');
      this.manageOrderForm.controls['total'].setValue(this.price * 1);
    },(error:any)=>{
      this.ngxService.stop();
      console.log(error);
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackBarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }

  setQuantity(vlaue:any){
    var temp = this.manageOrderForm.controls['quantity'].value;
    if(temp > 0){
      this.manageOrderForm.controls['total'].setValue(this.manageOrderForm.controls['quantity'].value * this.manageOrderForm.controls['price'].value);
    }
    else if(temp != ''){
      this.manageOrderForm.controls['quantity'].setValue('1');
      this.manageOrderForm.controls['total'].setValue(this.manageOrderForm.controls['quantity'].value * this.manageOrderForm.controls['price'].value);
    }
  }

  validateProductAdd(){
    if(this.manageOrderForm.controls['total'].value === 0 || this.manageOrderForm.controls['total'].value === null || this.manageOrderForm.controls['quantity'].value <= 0){
      return true;
    }else{
      return false;
    }
  }

  validateSubmit(){
    if(this.totalAmount === 0 || this.manageOrderForm.controls['name'].value === null || this.manageOrderForm.controls['email'].value === null || this.manageOrderForm.controls['contactNumber'].value === null || this.manageOrderForm.controls['paymentMethod'].value === null){
      return true;
    }else{
      return false;
    }
  }

  add(){
    var formData = this.manageOrderForm.value;
    var productName = this.dataSource.find((e:{id:number}) => e.id === formData.product.id);
    if(productName === undefined){
      this.totalAmount = this.totalAmount + formData.total;
      this.dataSource.push({id:formData.product.id, name:formData.product.name, category:formData.category.name, quantity:formData.quantity, price:formData.price, total:formData.total})
      this.dataSource = [...this.dataSource];
      this.snackBarService.openSnackBar(GlobalConstants.productAdded, "success");
    }else{
      this.snackBarService.openSnackBar(GlobalConstants.productExistError, GlobalConstants.error);
    }
  }

  validateDraftSave(){
    if(this.totalAmount === 0 || this.manageOrderForm.controls['name'].value === null || this.manageOrderForm.controls['email'].value === null || this.manageOrderForm.controls['contactNumber'].value === null || this.manageOrderForm.controls['paymentMethod'].value === null){
      return true;
    }
    return false;
  }

  saveDraftAction(){
    var formData = this.manageOrderForm.value;
    var data:any = {
      id: this.currentDraftId,
      name: formData.name,
      email: formData.email,
      contactNumber: formData.contactNumber,
      paymentMethod: formData.paymentMethod,
      totalAmount: this.totalAmount.toString(),
      productDetails: JSON.stringify(this.dataSource)
    };

    // remove null id so backend treats it as create
    if(data.id === null){
      delete data.id;
    }

    this.ngxService.start();
    this.billService.saveDraft(data).subscribe((response:any)=>{
      this.ngxService.stop();
      this.currentDraftId = response?.id ?? this.currentDraftId;
      this.snackBarService.openSnackBar("Draft saved successfully.", "success");
      this.getDrafts();
    },(error:any)=>{
      this.ngxService.stop();
      console.log(error);
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackBarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }

  loadDraft(draft:any){
    if(!draft?.id){
      return;
    }
    this.ngxService.start();
    this.billService.getDraftById(draft.id).subscribe((response:any)=>{
      this.ngxService.stop();
      if(!response){
        this.snackBarService.openSnackBar("Draft not found.", GlobalConstants.error);
        return;
      }

      this.currentDraftId = response.id;
      this.manageOrderForm.controls['name'].setValue(response.name);
      this.manageOrderForm.controls['email'].setValue(response.email);
      this.manageOrderForm.controls['contactNumber'].setValue(response.contactNumber);
      this.manageOrderForm.controls['paymentMethod'].setValue(response.paymentMethod);

      // reset product selection controls; items come from draft payload
      this.manageOrderForm.controls['category'].setValue(null);
      this.manageOrderForm.controls['product'].setValue(null);
      this.manageOrderForm.controls['price'].setValue('');
      this.manageOrderForm.controls['quantity'].setValue('');
      this.manageOrderForm.controls['total'].setValue(0);

      try{
        this.dataSource = JSON.parse(response.productDetails || '[]');
      }catch(e){
        this.dataSource = [];
      }
      this.dataSource = [...this.dataSource];
      this.totalAmount = Number(response.total || 0);

      this.snackBarService.openSnackBar("Draft loaded.", "success");
    },(error:any)=>{
      this.ngxService.stop();
      console.log(error);
      this.snackBarService.openSnackBar(GlobalConstants.genericError, GlobalConstants.error);
    })
  }

  deleteDraft(draft:any){
    if(!draft?.id){
      return;
    }
    this.ngxService.start();
    this.billService.deleteDraft(draft.id).subscribe(()=>{
      this.ngxService.stop();
      if(this.currentDraftId === draft.id){
        this.resetOrder();
      }
      this.getDrafts();
      this.snackBarService.openSnackBar("Draft deleted.", "success");
    },(error:any)=>{
      this.ngxService.stop();
      console.log(error);
      this.snackBarService.openSnackBar(GlobalConstants.genericError, GlobalConstants.error);
    })
  }

  resetOrder(){
    this.currentDraftId = null;
    this.manageOrderForm.reset();
    this.dataSource = [];
    this.totalAmount = 0;
    this.products = [];
    this.price = null;
  }

  handleDeleteAction(value:any, element:any){
    this.totalAmount = this.totalAmount - element.total;
    this.dataSource.splice(value, 1)
    this.dataSource = [...this.dataSource];
  }

  submitAction(){
    var formData = this.manageOrderForm.value;
    var data = {
      name: formData.name,
      email: formData.email,
      contactNumber: formData.contactNumber,
      paymentMethod: formData.paymentMethod,
      totalAmount: this.totalAmount.toString(),
      productDetails: JSON.stringify(this.dataSource),
      draftId: this.currentDraftId
    }
    this.ngxService.start();
    this.billService.generateReport(data).subscribe((response:any)=>{
      this.downloadFile(response?.uuid);
      this.resetOrder();
      this.getDrafts();
    },(error:any)=>{
      this.ngxService.stop();
      console.log(error);
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackBarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }

  downloadFile(fileName:string){
    var data = {
      uuid: fileName
    }

    this.billService.getPdf(data).subscribe((response:any)=>{
      saveAs(response, fileName + '.pdf');
      this.ngxService.stop();
    })
  }

}
