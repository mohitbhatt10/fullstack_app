import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AUTHENTICATED_USER } from '../app.constants';
import { TodoDataService } from '../service/data/todo-data.service';

export class Todo {
  constructor(
    public id: number,
    public description: string,
    public done: boolean,
    public targetDate: Date
  ) {}
}

@Component({
  selector: 'app-list-todos',
  templateUrl: './list-todos.component.html',
  styleUrls: ['./list-todos.component.css']
})
export class ListTodosComponent implements OnInit {

  todos: Todo[] = [];
  filteredTodos: Todo[] = [];
  username: string = '';
  message: string = '';
  filterType = {
    name: "All" // Default filter type name: "All" }
  }; // Default filter type
  availStatus = [ 
    { name: "All" }, 
    { name: "Pending" }, 
    { name: "Completed" }
  ]
  // Pagination
  currentPage = 1;
  itemsPerPage = 5;

  constructor(
    private todoService: TodoDataService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const user = sessionStorage.getItem(AUTHENTICATED_USER);
    if (user) {
      this.username = user;
    }
    this.refreshTodos();
  }

  refreshTodos() {
    this.todoService.retrieveAllTodos(this.username).subscribe(
      response => {
        console.log(response);
        this.todos = response;
        this.applyFilter(); // Apply the initial filter
        this.cdr.detectChanges();  // Trigger change detection
      }
    );
  }

  deleteTodo(id: number) {
    console.log(`delete todo ${id}`);
    this.todoService.deleteTodo(this.username, id).subscribe(
      response => {
        console.log(response);
        this.message = `Delete of Todo ${id} Successful!`;
        this.refreshTodos();
      }
    );
  }

  updateTodo(id: number) {
    console.log(`update ${id}`);
    this.router.navigate(['todos', id]);
  }

  addTodo() {
    this.router.navigate(['todos', -1]);
  }

  // Function to apply the filter based on the selected type
  applyFilter() {
    switch (this.filterType.name) {
      case 'All':
        this.filteredTodos = this.todos;
        break;
      case 'Completed':
        this.filteredTodos = this.todos.filter(todo => todo.done);
        break;
      case 'Pending':
        this.filteredTodos = this.todos.filter(todo => !todo.done);
        break;
      default:
        this.filteredTodos = this.todos;
    }
  }

  // Function to determine whether the Update and Delete buttons should be disabled
  isActionsDisabled(todo: Todo): boolean {
    return todo.done; // Disable if the todo is completed
  }

  // Function to determine the row color based on the specified conditions
  getRowColor(todo: Todo): string {
    const targetDate = new Date(todo.targetDate);
    const today = new Date();

    if (todo.done) {
      return 'lightgreen';
    } else if (targetDate < today) {
      return '#ED7013';
    } else {
      return '#E4ED13';
    }
  }

  // Pagination event handler
  pageChanged(event: any): void {
    this.currentPage = event;
  }
}
