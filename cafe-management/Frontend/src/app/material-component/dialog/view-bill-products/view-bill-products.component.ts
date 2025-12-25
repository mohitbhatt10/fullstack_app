import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ProductComponent } from '../product/product.component';

@Component({
  selector: 'app-view-bill-products',
  templateUrl: './view-bill-products.component.html',
  styleUrls: ['./view-bill-products.component.scss']
})
export class ViewBillProductsComponent implements OnInit {

  displayedColumns: string[] = ['name', 'category', 'price', 'quantity', 'total'];
  dataSource:any;
  data:any;
  subtotal: number = 0;
  tax: number = 0;

  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData:any,
    public dialogRef: MatDialogRef<ProductComponent>
  ) {}

  ngOnInit(): void {
    this.data = this.dialogData.data;
    this.dataSource = JSON.parse(this.dialogData.data.productDetails);
    console.log(this.dialogData.data);
    
    // Calculate subtotal from product details
    this.calculateSummary();
  }
  
  calculateSummary(): void {
    this.subtotal = 0;
    
    // Calculate subtotal by summing all product totals
    if (this.dataSource && this.dataSource.length > 0) {
      this.dataSource.forEach((item: any) => {
        this.subtotal += parseFloat(item.total || 0);
      });
    }
    
    // Calculate tax (5% GST)
    this.tax = parseFloat((this.subtotal * 0.05).toFixed(2));
    
    // Round values for display
    this.subtotal = parseFloat(this.subtotal.toFixed(2));
  }
  
}
