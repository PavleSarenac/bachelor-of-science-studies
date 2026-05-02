import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-student-header',
  templateUrl: './student-header.component.html',
  styleUrls: ['./student-header.component.css']
})
export class StudentHeaderComponent {
  constructor(private router: Router) { }

  logout() {
    localStorage.clear()
    this.router.navigate([""])
  }
}
