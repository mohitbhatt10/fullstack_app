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
  message: string = '';

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
          roles: [ userData.roles.map((role: { roleName: string; }) => role.roleName).join(', ')]
        });
      },
      error => {
        console.error('Error fetching user details:', error);
      }
    );
  }

  updateProfile(): void {
    const updatedUserData = { ...this.profileForm.value }; // Create a copy to avoid modifying the original form value
    delete updatedUserData.roles; // Remove the roles property
    delete updatedUserData.password; // Remove the password property
    console.log('Updated user data:', updatedUserData);
    this.userProfileService.updateUserProfile(updatedUserData).subscribe(
      data => {
        console.log('User profile updated successfully:', data);
        this.message = 'User profile updated successfully.';       
      }
      , error => {
        console.error('Error updating user profile:', error);
        this.message = 'Error updating user profile.';
      }
    );
  }
}