import { Component, OnInit } from '@angular/core';
import { Class } from 'src/app/models/class.model';
import { User } from 'src/app/models/user.model';
import { DefaultService } from 'src/app/services/default/default.service';
import { StudentService } from 'src/app/services/student/student.service';

@Component({
  selector: 'app-student-notifications',
  templateUrl: './student-notifications.component.html',
  styleUrls: ['./student-notifications.component.css']
})
export class StudentNotificationsComponent implements OnInit {
  student: User = new User()
  relevantClasses: Class[] = []
  initializationDone: boolean = false

  constructor(
    private defaultService: DefaultService,
    private studentService: StudentService
  ) { }

  ngOnInit(): void {
    this.defaultService.getUser(JSON.parse(localStorage.getItem("loggedInUser")!).username).subscribe(
      (student: User) => {
        this.student = student
        this.fetchNotifications()
      }
    )
  }

  fetchNotifications() {
    this.studentService.getRelevantClassesForNotifications(this.student.username).subscribe(
      (classes: Class[]) => {
        this.relevantClasses = classes
        this.initializationDone = true
      }
    )
  }

  readNotification(classObject: Class) {
    this.studentService.readNotification(classObject.id).subscribe(
      () => {
        this.fetchNotifications()
      }
    )
  }

}
