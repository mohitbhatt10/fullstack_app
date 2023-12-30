import { Injectable } from '@angular/core';
import { AUTHENTICATED_USER } from '../app.constants';
import { ROLES } from './basic-authentication.service';

@Injectable({
  providedIn: 'root'
})
export class HardcodedAuthenticationService {

  constructor() { }

  authenticate(username: string, password: string) {
    //console.log('before ' + this.isUserLoggedIn());
    if (username === "in28minutes" && password === 'dummy') {
      sessionStorage.setItem('authenticaterUser', username);
      //console.log('after ' + this.isUserLoggedIn());
      return true;
    }
    return false;
  }

  isUserLoggedIn() {
    let user = sessionStorage.getItem('authenticaterUser')
    return !(user === null)
  }

  logout() {
    sessionStorage.removeItem(AUTHENTICATED_USER)
  }

  hasRole(role: string): boolean {
    // Retrieve roles from session storage
    const roles: string[] = sessionStorage.getItem(ROLES)?.split(" ") || [];
    // Check if the specified role is present in the user's roles
    return this.isUserLoggedIn() && roles.includes(role);
  }
}
