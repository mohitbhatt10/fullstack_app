import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../service/data/user.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup = this.formBuilder.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    username: ['', Validators.required],
    password: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: [''],
    roles: ['']
  });
  username: string = '';

  constructor(
    private formBuilder: FormBuilder, 
    private userProfileService: UserService,
    private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.username = this.route.snapshot.params['username'];
    this.initForm();
  }

  initForm(): void {
    // Initialize the form with user data
    // Fetch user details using the UserProfileService
    this.userProfileService.getUserDetails(this.username).subscribe(
      userData => {
        this.profileForm = this.formBuilder.group({
          firstName: [userData.firstName],
          lastName: [userData.lastName],
          username: [userData.username],
          password: [userData.password],
          email: [userData.email],
          phoneNumber: [userData.phoneNumber],
          roles: [userData.roles]
        });
      },
      error => {
        console.error('Error fetching user details:', error);
      }
    );
  }

  updateProfile(): void {
    // Implement logic to update the user profile
    const updatedUserData = this.profileForm.value;
    this.userProfileService.updateUserProfile(updatedUserData);
  }
}