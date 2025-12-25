/**
 * Menu item model matching backend MenuItemWrapper
 */
export interface MenuItem {
  id: number;
  categoryId: number;
  categoryName: string;
  productName: string;
  description: string;
  price: number;
  isVeg: boolean;
  isActive: boolean;
}

/**
 * Menu category with items
 */
export interface MenuCategory {
  categoryId: number;
  categoryName: string;
  displayOrder?: number;
  items: MenuItem[];
}

/**
 * Pagination information
 */
export interface PaginationInfo {
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
}

/**
 * Complete menu response from API
 */
export interface MenuResponse {
  categories: MenuCategory[];
  totalCategories: number;
  totalItems: number;
  pagination?: PaginationInfo;
}
