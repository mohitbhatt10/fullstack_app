import { Router } from '@angular/router';
import { AUTHENTICATED_USER } from '../app.constants';
import { HardcodedAuthenticationService } from './../service/hardcoded-authentication.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {
  //isUserLoggedIn: boolean = false;
  username: string | null = "";
  
  constructor(public hardcodedAuthenticationService
    : HardcodedAuthenticationService, private router: Router) { }

  ngOnInit() {
    //this.isUserLoggedIn = this.hardcodedAuthenticationService.isUserLoggedIn();
    //this.username = sessionStorage.getItem(AUTHENTICATED_USER);
    //console.log(`username is manu: ${this.username}`);
  }

  goToHomePage(){
    this.username = sessionStorage.getItem(AUTHENTICATED_USER);
    this.router.navigate(['welcome', this.username])
  }

  goToProfilePage(){
    this.username = sessionStorage.getItem(AUTHENTICATED_USER);
    this.router.navigate(['profile', this.username])
  
  }
}
