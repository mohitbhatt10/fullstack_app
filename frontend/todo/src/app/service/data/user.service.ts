import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = 'http://localhost:8080/api/users/register';

  constructor(private http: HttpClient) { }

  registerUser(userRegistrationData: any): Observable<any> {
    return this.http.post(this.apiUrl, userRegistrationData);
  }
}