import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { MenuService } from '../services/menu.service';
import { SnackBarService } from '../services/snack-bar.service';
import { MenuResponse, MenuCategory, MenuItem } from '../models/menu.model';
import { GlobalConstants } from '../shared/global-constants';

/**
 * Restaurant Menu Component
 * Displays menu items grouped by category
 * Supports printing and pagination
 */
@Component({
  selector: 'app-restaurant-menu',
  templateUrl: './restaurant-menu.component.html',
  styleUrls: ['./restaurant-menu.component.scss']
})
export class RestaurantMenuComponent implements OnInit {

  // Menu data
  menuResponse: MenuResponse | null = null;
  allCategories: MenuCategory[] = [];
  displayedCategories: MenuCategory[] = [];
  
  // Pagination
  currentPage: number = 0;
  categoriesPerPage: number = 3; // Show 2-3 categories per page
  totalPages: number = 0;
  isPaginated: boolean = true;
  
  // Filtering
  selectedCategoryId: number | null = null;
  showVegOnly: boolean = false;
  
  // Loading state
  isLoading: boolean = false;
  
  // Print mode
  isPrintMode: boolean = false;
  
  constructor(
    private menuService: MenuService,
    private snackbarService: SnackBarService,
    private dialog: MatDialog,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadMenu();
  }

  /**
   * Load complete menu from API
   */
  loadMenu(): void {
    this.isLoading = true;
    this.selectedCategoryId = null;
    
    this.menuService.getMenu().subscribe(
      (response: MenuResponse) => {
        this.menuResponse = response;
        this.allCategories = response.categories || [];
        this.applyFiltersAndPagination();
        this.isLoading = false;
        
        if (this.allCategories.length === 0) {
          this.snackbarService.openSnackBar('No menu items available', 'info');
        }
      },
      (error: any) => {
        this.isLoading = false;
        console.error('Error loading menu:', error);
        
        if (error.error?.message) {
          this.snackbarService.openSnackBar(error.error.message, 'error');
        } else {
          this.snackbarService.openSnackBar(GlobalConstants.genericError, 'error');
        }
      }
    );
  }

  /**
   * Load menu items for specific category
   */
  loadMenuByCategory(categoryId: number): void {
    this.isLoading = true;
    this.selectedCategoryId = categoryId;
    
    this.menuService.getMenuByCategory(categoryId).subscribe(
      (response: MenuResponse) => {
        this.menuResponse = response;
        this.displayedCategories = response.categories || [];
        this.isLoading = false;
      },
      (error: any) => {
        this.isLoading = false;
        console.error('Error loading category menu:', error);
        this.snackbarService.openSnackBar(GlobalConstants.genericError, 'error');
      }
    );
  }

  /**
   * Apply filters and pagination to display categories
   */
  applyFiltersAndPagination(): void {
    let filteredCategories = [...this.allCategories];
    
    // Apply vegetarian filter
    if (this.showVegOnly) {
      filteredCategories = filteredCategories.map(category => ({
        ...category,
        items: category.items.filter(item => {
          // Handle different data types for isVeg (boolean, string, number)
          const vegValue: any = item.isVeg;
          return vegValue === true || vegValue === 'true' || vegValue === 1;
        })
      })).filter(category => category.items.length > 0);
    }
    
    // Calculate pagination
    this.totalPages = Math.ceil(filteredCategories.length / this.categoriesPerPage);
    
    // Get categories for current page
    const startIndex = this.currentPage * this.categoriesPerPage;
    const endIndex = startIndex + this.categoriesPerPage;
    this.displayedCategories = filteredCategories.slice(startIndex, endIndex);
  }

  /**
   * Toggle vegetarian filter
   */
  toggleVegFilter(): void {
    this.currentPage = 0; // Reset to first page
    this.applyFiltersAndPagination();
  }

  /**
   * Reset to full menu view
   */
  resetFilter(): void {
    this.showVegOnly = false;
    this.selectedCategoryId = null;
    this.currentPage = 0;
    this.applyFiltersAndPagination();
  }

  /**
   * Navigate to next page
   */
  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.applyFiltersAndPagination();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  /**
   * Navigate to previous page
   */
  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.applyFiltersAndPagination();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  /**
   * Go to specific page
   */
  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.applyFiltersAndPagination();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  /**
   * Format price to INR currency
   */
  formatPrice(price: number): string {
    return `₹${price}`;
  }

  /**
   * Get veg/non-veg indicator class
   */
  getVegIndicatorClass(isVeg: boolean): string {
    return isVeg ? 'veg-indicator' : 'non-veg-indicator';
  }

  /**
   * Print menu using backend PDF generation
   */
  printMenu(): void {
    this.isLoading = true;
    
    this.menuService.generateMenuPdf(this.showVegOnly).subscribe(
      (blob: Blob) => {
        this.isLoading = false;
        
        // Create a blob URL and open in new window for printing
        const blobUrl = window.URL.createObjectURL(blob);
        const printWindow = window.open(blobUrl, '_blank');
        
        if (printWindow) {
          printWindow.onload = () => {
            printWindow.print();
          };
        } else {
          // If popup blocked, download the PDF instead
          const link = document.createElement('a');
          link.href = blobUrl;
          link.download = 'menu.pdf';
          link.click();
          window.URL.revokeObjectURL(blobUrl);
        }
      },
      (error: any) => {
        this.isLoading = false;
        console.error('Error generating menu PDF:', error);
        
        if (error.error?.message) {
          this.snackbarService.openSnackBar(error.error.message, 'error');
        } else {
          this.snackbarService.openSnackBar('Failed to generate menu PDF', 'error');
        }
      }
    );
  }

  /**
   * Calculate page breaks for printing
   * Returns array of item indices where page breaks should occur
   */
  calculatePageBreaks(): number[] {
    const itemsPerPage = 20; // Configurable items per page
    const breakIndices: number[] = [];
    let itemCount = 0;
    
    for (let i = 0; i < this.displayedCategories.length; i++) {
      const category = this.displayedCategories[i];
      
      // If adding this category exceeds page limit, add page break before it
      if (itemCount > 0 && itemCount + category.items.length > itemsPerPage) {
        breakIndices.push(i);
        itemCount = 0;
      }
      
      itemCount += category.items.length;
      
      // If category itself is too large, add breaks within it
      if (category.items.length > itemsPerPage) {
        // For large categories, split into multiple pages
        itemCount = category.items.length % itemsPerPage;
      }
    }
    
    return breakIndices;
  }

  /**
   * Check if page break should be added before this category
   */
  shouldAddPageBreak(categoryIndex: number): boolean {
    const pageBreaks = this.calculatePageBreaks();
    return pageBreaks.includes(categoryIndex);
  }
}
