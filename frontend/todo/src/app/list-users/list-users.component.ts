import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../service/data/user.service';

@Component({
  selector: 'app-list-users',
  templateUrl: './list-users.component.html',
  styleUrls: ['./list-users.component.css']
})
export class ListUsersComponent implements OnInit {
  users: any[] = [];
  selectedUsers: any[] = [];
  message : string = '';
  messageVisible: boolean = false;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe(
      (response) => {
        this.users = response;
      },
      (error) => {
        console.error('Error loading users:', error);
      }
    );
  }

  getRolesAsString(roles: any[]): string {
    // if(roles.every(element => typeof element === 'number' && Number.isInteger(element))){
    //   //iterate over the roles array and convert each number to a string
    //   return roles.map(role => {
    //     if(role === 1){
    //       return 'ROLE_USER';
    //     }
    //     else if(role === 2){
    //       return 'ROLE_ADMIN';
    //     }
    //     else if( role === 3){
    //       return 'ROLE_MODERATOR';
    //     }
    //     else{
    //       return '';
    //     }
    //   }).join(', ');
    // }
    return roles.map(role => role.roleName).join(', ');
  }

  updateUserDetails(user: any): void {
    // Update the selectedUsers array based on user selection
    const index = this.selectedUsers.findIndex((u) => u.userId === user.userId);

    if (index !== -1) {
      this.selectedUsers.splice(index, 1);
    } else {
      this.selectedUsers.push(user);
    }
  }

  updateSelectedUsers(): void {
    // Update the status and roles of selected users
    for (const user of this.selectedUsers) {
      const updatedUser = {
        firstName: user.firstName,
        lastName: user.lastName,
        username: user.username,
        //password: 'dummy', // You might need to handle password differently
        email: user.email,
        phoneNumber: user.phoneNumber,
        enabled: user.enabled,
        roles: user.roles // Retrieve the current roles from the user object // Update with the desired roles
      };

      // Call the update API for each user
      this.userService.updateUser(user.userId, updatedUser).subscribe(
        (response) => {
          console.log('User updated successfully:', response);
          this.message = "Selected Users Updated Successfully";
          this.messageVisible = true;
          this.loadUsers(); // Refresh the user list after updating
          // Hide the message after 5 seconds
          setTimeout(() => {
            this.messageVisible = false;
          }, 5000);
        },
        (error) => {
          console.error('Error updating user:', error);
          this.message = "Error updating selected users, error is " + error.message ;
          this.messageVisible = true;
          // Hide the message after 5 seconds
          setTimeout(() => {
            this.messageVisible = false;
          }, 5000);
        }
      );
    }

    // Clear the selectedUsers array after updating
    this.selectedUsers = [];
  }

  onUserSelect(user: any) {
    // Toggle the selected state of the user
    console.log(user.selected);  
    // Update the selectedUsers array based on the selection state
    if (user.selected) {
      this.selectedUsers.push(user);
    } else {
      // Remove the user from selectedUsers if unchecked
      const index = this.selectedUsers.findIndex(selectedUser => selectedUser.id === user.id);
      if (index !== -1) {
        this.selectedUsers.splice(index, 1);
      }
      this.loadUsers();
    }
    console.log(this.selectedUsers);
  }
}
