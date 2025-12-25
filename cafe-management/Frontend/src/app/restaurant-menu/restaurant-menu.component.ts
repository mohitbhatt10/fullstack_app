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
  displayedCategories: MenuCategory[] = [];
  
  // Pagination
  currentPage: number = 0;
  pageSize: number = 30;
  totalPages: number = 0;
  isPaginated: boolean = false;
  
  // Filtering
  selectedCategoryId: number | null = null;
  
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
        this.displayedCategories = response.categories || [];
        this.isLoading = false;
        
        if (this.displayedCategories.length === 0) {
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
   * Load paginated menu
   */
  loadPaginatedMenu(page: number): void {
    this.isLoading = true;
    this.currentPage = page;
    this.isPaginated = true;
    
    this.menuService.getMenuPaginated(page, this.pageSize).subscribe(
      (response: MenuResponse) => {
        this.menuResponse = response;
        this.displayedCategories = response.categories || [];
        
        if (response.pagination) {
          this.totalPages = response.pagination.totalPages;
        }
        
        this.isLoading = false;
      },
      (error: any) => {
        this.isLoading = false;
        console.error('Error loading paginated menu:', error);
        this.snackbarService.openSnackBar(GlobalConstants.genericError, 'error');
      }
    );
  }

  /**
   * Reset to full menu view
   */
  resetFilter(): void {
    this.loadMenu();
  }

  /**
   * Navigate to next page
   */
  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.loadPaginatedMenu(this.currentPage + 1);
    }
  }

  /**
   * Navigate to previous page
   */
  previousPage(): void {
    if (this.currentPage > 0) {
      this.loadPaginatedMenu(this.currentPage - 1);
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
   * Print menu
   */
  printMenu(): void {
    window.print();
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
