import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { throwError } from 'rxjs';
import { AUTHENTICATION_ERROR_MESSAGE, BACKEND_CONNECTION_ERROR_MESSAGE } from '../app.constants';

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {
  constructor() {}

  handleHttpError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    let errorType = 'generic';

    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
      errorType = 'client';
    } else {
      // Server-side error
      errorMessage = BACKEND_CONNECTION_ERROR_MESSAGE;
      errorType = 'server';
    }

    // You can add more conditions to categorize errors based on your needs
    if (error.status === 401) {
      errorMessage = AUTHENTICATION_ERROR_MESSAGE;
      errorType = 'authentication';
    }

    // Handle the error in your application (e.g., display a message to the user)
    console.error(errorMessage);

    // Rethrow the error to let the calling code handle it further
    return throwError({ type: errorType, message: errorMessage });
  }
}