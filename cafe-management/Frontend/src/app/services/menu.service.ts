import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

/**
 * Service for Menu API operations
 * Handles HTTP requests for fetching menu data
 */
@Injectable({
  providedIn: 'root'
})
export class MenuService {

  url = environment.apiUrl;

  constructor(private httpClient: HttpClient) { }

  /**
   * Get complete menu with all categories and items
   * @returns Observable with complete menu response
   */
  getMenu(): Observable<any> {
    return this.httpClient.get(this.url + "/menu/get");
  }

  /**
   * Get menu items filtered by category
   * @param categoryId Category ID to filter by
   * @returns Observable with filtered menu response
   */
  getMenuByCategory(categoryId: number): Observable<any> {
    const params = new HttpParams().set('categoryId', categoryId.toString());
    return this.httpClient.get(this.url + "/menu/getByCategory", { params });
  }

  /**
   * Get paginated menu
   * @param page Page number (0-indexed)
   * @param size Number of items per page
   * @returns Observable with paginated menu response
   */
  getMenuPaginated(page: number = 0, size: number = 20): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.httpClient.get(this.url + "/menu/getPaginated", { params });
  }

  /**
   * Generate menu PDF
   * @param showVegOnly Filter to show only vegetarian items
   * @returns Observable with PDF as blob
   */
  generateMenuPdf(showVegOnly: boolean = false): Observable<Blob> {
    const params = new HttpParams().set('showVegOnly', showVegOnly.toString());
    return this.httpClient.get(this.url + "/menu/generatePdf", { 
      params,
      responseType: 'blob'
    });
  }
}
