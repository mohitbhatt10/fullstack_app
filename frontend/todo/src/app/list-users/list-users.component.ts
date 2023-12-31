import { Component, OnInit } from '@angular/core';
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
  isUpdateButtonDisabled: boolean = true;

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
        this.message = "Error loading users, error is " + error.message ;
      }
    );
  }

  getRolesAsString(roles: any[]): string {
    return roles.map(role => role.roleName).join(', ');
  }

  updateSelectedUsers(): void {

    // Replace the roles of selected users with the updated roles from 'selectedUsers'
    for (const selectedUser of this.selectedUsers) {
      const index = this.users.findIndex(user => user.userId === selectedUser.userId);

      if (index !== -1) {
        // Replace the user in the 'users' array with the updated user from 'selectedUsers'
        let updatedUser = this.users[index]; 
        selectedUser.roles = updatedUser.selectedRoles;
        selectedUser.enabled = updatedUser.selectedEnabled;      
      }
    }

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
    this.isUpdateButtonDisabled = true;
  }

  onUserSelect(user: any) {
    // Toggle the selected state of the user
    console.log(user.selected);  
    // Update the selectedUsers array based on the selection state
    if (user.selected) {
      // Create a deep copy of the user and add it to selectedUsers
      const selectedUserCopy = { ...user, roles: [...user.roles] };
      this.selectedUsers.push(selectedUserCopy);

      // Preselect roles in the user's select dropdown
      user.selectedRoles = [...user.roles];
      user.selectedEnabled = user.enabled;
    } else {
      // Remove the user from selectedUsers if unchecked
      const index = this.selectedUsers.findIndex(selectedUser => selectedUser.userId === user.userId);
      if (index !== -1) {
        this.selectedUsers.splice(index, 1);
      }
      //this.loadUsers();
    }
    console.log(this.selectedUsers);
    this.isUpdateButtonDisabled = this.selectedUsers.length === 0;
  }
}
