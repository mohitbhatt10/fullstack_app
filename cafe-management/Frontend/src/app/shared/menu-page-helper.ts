import { MenuItem, MenuCategory } from '../models/menu.model';

/**
 * Utility class for menu page calculations and formatting
 * Handles page break logic for printing and PDF generation
 */
export class MenuPageHelper {
  
  /**
   * Calculate optimal page breaks for menu printing
   * Ensures categories don't split awkwardly
   * 
   * @param categories Array of menu categories
   * @param itemsPerPage Maximum items per page (default: 20)
   * @returns Array of category indices where page breaks should occur
   */
  static calculatePageBreaks(categories: MenuCategory[], itemsPerPage: number = 20): number[] {
    const breakIndices: number[] = [];
    let currentPageItems = 0;
    
    for (let i = 0; i < categories.length; i++) {
      const category = categories[i];
      const categoryItemCount = category.items.length;
      
      // If current page has items and adding this category would exceed limit
      if (currentPageItems > 0 && currentPageItems + categoryItemCount > itemsPerPage) {
        // Add page break before this category
        breakIndices.push(i);
        currentPageItems = categoryItemCount;
      } else {
        currentPageItems += categoryItemCount;
      }
      
      // Reset counter if we've filled a page
      if (currentPageItems >= itemsPerPage) {
        currentPageItems = 0;
      }
    }
    
    return breakIndices;
  }
  
  /**
   * Split menu into pages for PDF generation
   * Each page contains complete categories (no mid-category splits)
   * 
   * @param categories Array of menu categories
   * @param itemsPerPage Maximum items per page
   * @returns Array of pages, each containing categories
   */
  static splitMenuIntoPages(categories: MenuCategory[], itemsPerPage: number = 20): MenuCategory[][] {
    const pages: MenuCategory[][] = [];
    let currentPage: MenuCategory[] = [];
    let currentPageItems = 0;
    
    for (const category of categories) {
      const categoryItemCount = category.items.length;
      
      // If category is too large, split it
      if (categoryItemCount > itemsPerPage) {
        // Save current page if it has items
        if (currentPage.length > 0) {
          pages.push(currentPage);
          currentPage = [];
          currentPageItems = 0;
        }
        
        // Split large category into multiple pages
        const splitCategories = this.splitLargeCategory(category, itemsPerPage);
        pages.push(...splitCategories.map(cat => [cat]));
        
      } else if (currentPageItems + categoryItemCount > itemsPerPage) {
        // Start new page with this category
        pages.push(currentPage);
        currentPage = [category];
        currentPageItems = categoryItemCount;
        
      } else {
        // Add to current page
        currentPage.push(category);
        currentPageItems += categoryItemCount;
      }
    }
    
    // Add last page if it has items
    if (currentPage.length > 0) {
      pages.push(currentPage);
    }
    
    return pages;
  }
  
  /**
   * Split a large category into multiple smaller categories
   * Used when a single category has more items than can fit on one page
   * 
   * @param category Category to split
   * @param itemsPerPage Items per page
   * @returns Array of split categories
   */
  private static splitLargeCategory(category: MenuCategory, itemsPerPage: number): MenuCategory[] {
    const splitCategories: MenuCategory[] = [];
    const items = category.items;
    
    for (let i = 0; i < items.length; i += itemsPerPage) {
      const pageItems = items.slice(i, i + itemsPerPage);
      const pageNumber = Math.floor(i / itemsPerPage) + 1;
      const totalPages = Math.ceil(items.length / itemsPerPage);
      
      splitCategories.push({
        categoryId: category.categoryId,
        categoryName: `${category.categoryName} (${pageNumber}/${totalPages})`,
        displayOrder: category.displayOrder,
        items: pageItems
      });
    }
    
    return splitCategories;
  }
  
  /**
   * Calculate total number of pages needed
   * 
   * @param categories Array of menu categories
   * @param itemsPerPage Items per page
   * @returns Total number of pages
   */
  static calculateTotalPages(categories: MenuCategory[], itemsPerPage: number = 20): number {
    const pages = this.splitMenuIntoPages(categories, itemsPerPage);
    return pages.length;
  }
  
  /**
   * Get page number for a specific category
   * 
   * @param categories All categories
   * @param categoryIndex Index of category to find
   * @param itemsPerPage Items per page
   * @returns Page number (1-indexed)
   */
  static getPageNumberForCategory(
    categories: MenuCategory[], 
    categoryIndex: number, 
    itemsPerPage: number = 20
  ): number {
    let currentPage = 1;
    let currentPageItems = 0;
    
    for (let i = 0; i < categoryIndex; i++) {
      const category = categories[i];
      const categoryItemCount = category.items.length;
      
      if (currentPageItems + categoryItemCount > itemsPerPage) {
        currentPage++;
        currentPageItems = categoryItemCount;
      } else {
        currentPageItems += categoryItemCount;
      }
      
      if (currentPageItems >= itemsPerPage) {
        currentPage++;
        currentPageItems = 0;
      }
    }
    
    return currentPage;
  }
  
  /**
   * Format price with INR symbol
   * 
   * @param price Price in rupees
   * @returns Formatted price string
   */
  static formatPrice(price: number): string {
    return `₹${price.toLocaleString('en-IN')}`;
  }
  
  /**
   * Format price with decimal places
   * 
   * @param price Price in rupees
   * @param decimals Number of decimal places (default: 0)
   * @returns Formatted price string
   */
  static formatPriceWithDecimals(price: number, decimals: number = 0): string {
    return `₹${price.toLocaleString('en-IN', { minimumFractionDigits: decimals, maximumFractionDigits: decimals })}`;
  }
  
  /**
   * Get veg/non-veg symbol
   * 
   * @param isVeg Vegetarian flag
   * @returns Unicode symbol
   */
  static getVegSymbol(isVeg: boolean): string {
    return isVeg ? '🟢' : '🔴';
  }
  
  /**
   * Get veg/non-veg text
   * 
   * @param isVeg Vegetarian flag
   * @returns Text label
   */
  static getVegText(isVeg: boolean): string {
    return isVeg ? 'Vegetarian' : 'Non-Vegetarian';
  }
  
  /**
   * Truncate description to fit in menu card
   * 
   * @param description Full description
   * @param maxLength Maximum length (default: 100)
   * @returns Truncated description
   */
  static truncateDescription(description: string, maxLength: number = 100): string {
    if (description.length <= maxLength) {
      return description;
    }
    return description.substring(0, maxLength - 3) + '...';
  }
  
  /**
   * Group items by veg/non-veg within a category
   * 
   * @param items Array of menu items
   * @returns Object with veg and nonVeg arrays
   */
  static groupByVegType(items: MenuItem[]): { veg: MenuItem[], nonVeg: MenuItem[] } {
    return {
      veg: items.filter(item => item.isVeg),
      nonVeg: items.filter(item => !item.isVeg)
    };
  }
  
  /**
   * Sort categories by display order
   * 
   * @param categories Array of categories
   * @returns Sorted array
   */
  static sortCategoriesByOrder(categories: MenuCategory[]): MenuCategory[] {
    return [...categories].sort((a, b) => {
      const orderA = a.displayOrder ?? 999;
      const orderB = b.displayOrder ?? 999;
      return orderA - orderB;
    });
  }
  
  /**
   * Filter active items only
   * 
   * @param items Array of menu items
   * @returns Filtered array of active items
   */
  static filterActiveItems(items: MenuItem[]): MenuItem[] {
    return items.filter(item => item.isActive !== false);
  }
  
  /**
   * Calculate menu statistics
   * 
   * @param categories Array of categories
   * @returns Statistics object
   */
  static calculateMenuStatistics(categories: MenuCategory[]): {
    totalCategories: number;
    totalItems: number;
    vegItems: number;
    nonVegItems: number;
    averagePrice: number;
    priceRange: { min: number; max: number };
  } {
    let totalItems = 0;
    let vegItems = 0;
    let nonVegItems = 0;
    let totalPrice = 0;
    let minPrice = Infinity;
    let maxPrice = -Infinity;
    
    for (const category of categories) {
      for (const item of category.items) {
        totalItems++;
        totalPrice += item.price;
        
        if (item.isVeg) {
          vegItems++;
        } else {
          nonVegItems++;
        }
        
        if (item.price < minPrice) minPrice = item.price;
        if (item.price > maxPrice) maxPrice = item.price;
      }
    }
    
    return {
      totalCategories: categories.length,
      totalItems,
      vegItems,
      nonVegItems,
      averagePrice: totalItems > 0 ? totalPrice / totalItems : 0,
      priceRange: {
        min: minPrice === Infinity ? 0 : minPrice,
        max: maxPrice === -Infinity ? 0 : maxPrice
      }
    };
  }
}
