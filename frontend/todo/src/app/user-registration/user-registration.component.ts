import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../service/data/user.service';
import { BACKEND_CONNECTION_ERROR_MESSAGE } from '../app.constants';


@Component({
  selector: 'app-user-registration',
  templateUrl: './user-registration.component.html',
  styleUrls: ['./user-registration.component.css']
})
export class UserRegistrationComponent {

  userRegistrationBackendError: string = '';
  isUserRegistrationSuccessful: boolean = false;

  registrationForm: FormGroup = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    username: ['', Validators.required],
    password: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: ['']
  });

  constructor(private fb: FormBuilder, private userService: UserService) {}

  onSubmit() {
    if (this.registrationForm.valid) {
      // Call the user service to send the registration data to the backend
      this.userService.registerUser(this.registrationForm.value).subscribe(
        response => {
          console.log('User registered successfully:', response);
          this.userRegistrationBackendError = '';
          this.isUserRegistrationSuccessful = true;
          // Optionally, you can redirect the user to a login page or show a success message
        },
        error => {
          console.error('Error during user registration:', error);
          this.isUserRegistrationSuccessful = false;
          if(error.error.errorMessage){
            this.userRegistrationBackendError = error.error.errorMessage;
          } else{ 
            this.userRegistrationBackendError =  BACKEND_CONNECTION_ERROR_MESSAGE;
          }
          console.log(this.userRegistrationBackendError);
        }
      );
    }
  }
} 