import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from 'src/app/app.constants';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = `${API_URL}/api/users`;

  constructor(private http: HttpClient) { }

  registerUser(userRegistrationData: any): Observable<any> {
    const registerUrl = `${this.apiUrl}/register`;
    return this.http.post(registerUrl, userRegistrationData);
  }

  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  updateUser(userId: number, updatedUser: any): Observable<any> {
    const updateUserUrl = `${this.apiUrl}/${userId}`;
    return this.http.put(updateUserUrl, updatedUser);
  }
}
