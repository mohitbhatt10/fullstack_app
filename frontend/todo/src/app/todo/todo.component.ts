import { ActivatedRoute, Router } from '@angular/router';
import { TodoDataService } from './../service/data/todo-data.service';
import { Component, OnInit } from '@angular/core';
import { Todo } from '../list-todos/list-todos.component';
import { AUTHENTICATED_USER } from '../app.constants';

@Component({
  selector: 'app-todo',
  templateUrl: './todo.component.html',
  styleUrls: ['./todo.component.css']
})
export class TodoComponent implements OnInit {

  id: number = 0;
  todo: Todo = new Todo(this.id, '', false, new Date());
  username : string  = '' ; 
  selectedStatus: boolean = false; // Introduce the selectedStatus variable

  // Define the options for the 'Mark Completed' dropdown
  statusOptions: { value: boolean; label: string }[] = [
    { value: false, label: 'No' },
    { value: true, label: 'Yes' }
  ];

  constructor(
    private todoService: TodoDataService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit() {

    this.id = this.route.snapshot.params['id'];

    this.todo = new Todo(this.id, '', false, new Date());

    const user = sessionStorage.getItem(AUTHENTICATED_USER);
    if(user) {
      this.username = user;
      console.log(`username in todo-component: ${this.username}`);
    } 

    if (this.id != -1) {
      this.todoService.retrieveTodo(this.username, this.id)
        .subscribe(
          data => {
            this.todo = data;
            this.selectedStatus = this.todo.done; // Set the initial value for the dropdown
          }
        )
    }
  }

  saveTodo() {
    if (this.id == -1) { //=== ==
      this.todoService.createTodo(this.username, this.todo)
        .subscribe(
          data => {
            console.log(data)
            this.router.navigate(['todos'])
          }
        )
    } else {
      this.todoService.updateTodo(this.username, this.id, this.todo)
        .subscribe(
          data => {
            console.log(data)
            this.router.navigate(['todos'])
          }
        )
    }
  }

  // Function to handle the 'Mark Completed' dropdown change
  onStatusChange() {
    this.todo.done = this.selectedStatus;
  }

}
