import { Component, OnInit } from '@angular/core';
import { Class } from 'src/app/models/class.model';
import { User } from 'src/app/models/user.model';
import { DefaultService } from 'src/app/services/default/default.service';
import { TeacherService } from 'src/app/services/teacher/teacher.service';

@Component({
  selector: 'app-teacher-my-students',
  templateUrl: './teacher-my-students.component.html',
  styleUrls: ['./teacher-my-students.component.css']
})
export class TeacherMyStudentsComponent implements OnInit {
  teacher: User = new User()

  allDoneClasses: Class[] = []
  studentToClassesMappings: Map<string, Class[]> = new Map<string, Class[]>()
  studentToClassesArray: any[] = []

  teacherToStudentGrade: number = 0
  ratingError: string = "Please give this student a grade."

  initializationDone: boolean = false

  constructor(
    private defaultService: DefaultService,
    private teacherService: TeacherService
  ) { }

  ngOnInit(): void {
    this.defaultService.getUser(JSON.parse(localStorage.getItem("loggedInUser")!).username).subscribe(
      (teacher: User) => {
        this.teacher = teacher
        this.fetchClasses()
      }
    )
  }

  fetchClasses() {
    this.teacherService.getAllDoneClasses(this.teacher.username).subscribe(
      (classes: Class[]) => {
        this.allDoneClasses = classes
        this.mapStudentsToClasses()
        this.initializationDone = true
      }
    )
  }

  setRating(rating: number) {
    this.teacherToStudentGrade = rating
  }

  rateStudent(doneClass: Class) {
    if (this.teacherToStudentGrade == 0) return
    doneClass.teacherToStudentGrade = this.teacherToStudentGrade
    this.teacherService.rateStudent(doneClass).subscribe()
  }

  mapStudentsToClasses() {
    this.allDoneClasses.forEach(
      (doneClass: Class) => {
        let key = doneClass.studentUsername + ";" + doneClass.studentName + ";" + doneClass.studentSurname
        if (this.studentToClassesMappings.has(key)) {
          this.studentToClassesMappings.get(key)!.push(doneClass)
        } else {
          this.studentToClassesMappings.set(key, [doneClass])
        }
      }
    )
    this.studentToClassesArray = Array.from(this.studentToClassesMappings).map(
      ([studentData, classes]) => ({ studentData, classes })
    )
  }
}
