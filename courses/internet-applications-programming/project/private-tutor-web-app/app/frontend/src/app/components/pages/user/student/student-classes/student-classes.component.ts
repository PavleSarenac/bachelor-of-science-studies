import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Class } from 'src/app/models/class.model';
import { User } from 'src/app/models/user.model';
import { DefaultService } from 'src/app/services/default/default.service';
import { StudentService } from 'src/app/services/student/student.service';

const NUMBER_OF_MINUTES_IN_ONE_HOUR = 60
const NUMBER_OF_SECONDS_IN_ONE_MINUTE = 60
const NUMBER_OF_MILLISECONDS_IN_ONE_SECOND = 1000

const NUMBER_OF_MILLISECONDS_IN_ONE_HOUR =
  NUMBER_OF_MINUTES_IN_ONE_HOUR * NUMBER_OF_SECONDS_IN_ONE_MINUTE * NUMBER_OF_MILLISECONDS_IN_ONE_SECOND

@Component({
  selector: 'app-student-classes',
  templateUrl: './student-classes.component.html',
  styleUrls: ['./student-classes.component.css']
})
export class StudentClassesComponent implements OnInit {
  student: User = new User()

  allUpcomingClasses: Class[] = []
  shouldShowUpcomingClasses: boolean = false

  allPastClasses: Class[] = []
  shouldShowPastClasses: boolean = false

  ratingError: string = "Please give this teacher a grade."
  studentToTeacherGrade: number = 0

  constructor(
    private studentService: StudentService,
    private defaultService: DefaultService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.defaultService.getUser(JSON.parse(localStorage.getItem("loggedInUser")!).username).subscribe(
      (student: User) => {
        this.student = student
        this.fetchClasses()
      }
    )
  }

  rateTeacher(pastClass: Class) {
    if (this.studentToTeacherGrade == 0) return
    pastClass.studentToTeacherGrade = this.studentToTeacherGrade
    this.studentService.rateTeacher(pastClass).subscribe()
  }

  setRating(rating: number) {
    this.studentToTeacherGrade = rating
  }

  fetchClasses() {
    this.studentService.getAllUpcomingClasses(this.student.username).subscribe(
      (upcomingClasses: Class[]) => {
        this.allUpcomingClasses = upcomingClasses
        this.studentService.getAllPastClasses(this.student.username).subscribe(
          (pastClasses: Class[]) => {
            this.allPastClasses = pastClasses
          }
        )
      }
    )
  }

  joinClass() {
    this.router.navigate(["jitsi"])
  }

  getTimeDifferenceInMinutes(classStartDate: string, classStartTime: string): number {
    let classDateTimeStringIsoFormat = classStartDate + "T" + classStartTime + ":00.000Z"
    let classDateTimeMillis = new Date(classDateTimeStringIsoFormat).getTime()
    let currentDateTimeMillis = Date.now() + NUMBER_OF_MILLISECONDS_IN_ONE_HOUR
    let timeDifferenceInMinutes = (classDateTimeMillis - currentDateTimeMillis) / (NUMBER_OF_MILLISECONDS_IN_ONE_SECOND * NUMBER_OF_SECONDS_IN_ONE_MINUTE)
    return timeDifferenceInMinutes
  }

  showUpcomingClasses() {
    this.shouldShowUpcomingClasses = true
  }

  hideUpcomingClasses() {
    this.shouldShowUpcomingClasses = false
  }

  showPastClasses() {
    this.shouldShowPastClasses = true
  }

  hidePastClasses() {
    this.shouldShowPastClasses = false
  }
}
