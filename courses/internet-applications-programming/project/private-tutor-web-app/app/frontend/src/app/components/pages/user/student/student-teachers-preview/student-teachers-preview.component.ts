import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Data } from 'src/app/models/data.model';
import { User } from 'src/app/models/user.model';
import { DefaultService } from 'src/app/services/default/default.service';
import { TeacherService } from 'src/app/services/teacher/teacher.service';

@Component({
  selector: 'app-student-teachers-preview',
  templateUrl: './student-teachers-preview.component.html',
  styleUrls: ['./student-teachers-preview.component.css', '../../../../../../styles.css']
})
export class StudentTeachersPreviewComponent implements OnInit {
  user: User = new User()

  data: Data = new Data()
  allTeachers: User[] = []
  shouldShowSubjects: boolean = false

  subjectsSorted: string[] = []
  allTeachersSorted: User[] = []

  shouldShowSortedSubjects: boolean = false
  shouldSortSubjectsBySubject: boolean = false
  shouldSortSubjectsByTeacherName: boolean = false
  shouldSortSubjectsByTeacherSurname: boolean = false

  shouldShowSearchResults: boolean = false
  searchTeacherName: string = ""
  searchTeacherSurname: string = ""
  searchSubject: string = ""

  subjectsSearched: string[] = []
  allTeachersSearched: User[] = []

  subjectsSearchedSorted: string[] = []
  allTeachersSearchedSorted: User[] = []

  shouldShowSortedTeachers: boolean = false
  shouldSortTeachersByTeacherName: boolean = false
  shouldSortTeachersByTeacherSurname: boolean = false
  shouldSortTeachersBySubject: boolean = false

  constructor(
    private defaultService: DefaultService,
    private teacherService: TeacherService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.defaultService.getData().subscribe(
      (data: Data[]) => {
        this.data = data[0]
        this.defaultService.getUser(JSON.parse(localStorage.getItem("loggedInUser")!).username).subscribe(
          (user) => {
            this.user = user
            let studentAge = ""
            if (this.user.schoolType == this.data.schoolTypes[0]) {
              if (this.user.currentGrade >= "1" && this.user.currentGrade <= "4") {
                studentAge = this.data.studentAges[0]
              } else {
                studentAge = this.data.studentAges[1]
              }
            } else {
              studentAge = this.data.studentAges[2]
            }
            this.teacherService.getTeachersTeachingSpecificStudentAge(studentAge).subscribe(
              (teachers: User[]) => {
                this.allTeachers = teachers
              }
            )
          }
        )
      }
    )
  }

  seeTeacherDetails(teacherUsername: string) {
    localStorage.setItem("studentSeeTeacherDetailsUsername", teacherUsername)
    this.router.navigate(["student-see-teacher-details"])
  }

  sortSubjects() {
    if (this.shouldSortSubjectsByTeacherName || this.shouldSortSubjectsByTeacherSurname || this.shouldSortSubjectsBySubject) {
      this.shouldShowSortedSubjects = true
    }
    else {
      this.shouldShowSortedSubjects = false
      return
    }
    this.subjectsSorted = []
    this.allTeachersSorted = []
    this.data.subjects.forEach((subject) => this.subjectsSorted.push(subject))
    this.allTeachers.forEach((teacher) => this.allTeachersSorted.push(teacher))
    if (this.shouldSortSubjectsBySubject) {
      this.subjectsSorted = this.subjectsSorted.sort((subject1, subject2) => subject1.localeCompare(subject2))
    }
    if (this.shouldSortSubjectsByTeacherName && !this.shouldSortSubjectsByTeacherSurname) {
      this.allTeachersSorted = this.allTeachersSorted.sort((teacher1, teacher2) => teacher1.name.localeCompare(teacher2.name))
    }
    if (!this.shouldSortSubjectsByTeacherName && this.shouldSortSubjectsByTeacherSurname) {
      this.allTeachersSorted = this.allTeachersSorted.sort((teacher1, teacher2) => teacher1.surname.localeCompare(teacher2.surname))
    }
    if (this.shouldSortSubjectsByTeacherName && this.shouldSortSubjectsByTeacherSurname) {
      this.allTeachersSorted = this.allTeachersSorted.sort((teacher1, teacher2) => {
        const nameComparisonResult = teacher1.name.localeCompare(teacher2.name)
        if (nameComparisonResult == 0) {
          return teacher1.surname.localeCompare(teacher2.surname)
        } else {
          return nameComparisonResult
        }
      })
    }
  }

  sortTeachers() {
    if (this.shouldSortTeachersByTeacherName || this.shouldSortTeachersByTeacherSurname || this.shouldSortTeachersBySubject) {
      this.shouldShowSortedTeachers = true
    }
    else {
      this.shouldShowSortedTeachers = false
      return
    }
    this.subjectsSearchedSorted = []
    this.allTeachersSearchedSorted = []
    this.data.subjects.forEach((subject) => this.subjectsSearchedSorted.push(subject))
    this.allTeachersSearched.forEach((teacher) => this.allTeachersSearchedSorted.push(teacher))
    if (this.shouldSortTeachersByTeacherName && !this.shouldSortTeachersByTeacherSurname) {
      this.allTeachersSearchedSorted = this.allTeachersSearchedSorted.sort((teacher1, teacher2) => teacher1.name.localeCompare(teacher2.name))
    }
    if (!this.shouldSortTeachersByTeacherName && this.shouldSortTeachersByTeacherSurname) {
      this.allTeachersSearchedSorted = this.allTeachersSearchedSorted.sort((teacher1, teacher2) => teacher1.surname.localeCompare(teacher2.surname))
    }
    if (this.shouldSortTeachersByTeacherName && this.shouldSortTeachersByTeacherSurname) {
      this.allTeachersSearchedSorted = this.allTeachersSearchedSorted.sort((teacher1, teacher2) => {
        const nameComparisonResult = teacher1.name.localeCompare(teacher2.name)
        if (nameComparisonResult == 0) {
          return teacher1.surname.localeCompare(teacher2.surname)
        } else {
          return nameComparisonResult
        }
      })
    }
    if (this.shouldSortTeachersBySubject) {
      this.subjectsSearchedSorted = this.subjectsSearchedSorted.sort((subject1, subject2) => subject1.localeCompare(subject2))
    }

  }

  search() {
    this.subjectsSearched = []
    this.allTeachersSearched = []
    this.data.subjects.forEach((subject) => this.subjectsSearched.push(subject))
    this.allTeachers.forEach((teacher) => this.allTeachersSearched.push(teacher))
    if (this.searchTeacherName != "") {
      this.allTeachersSearched = this.allTeachersSearched.filter(
        (teacher) => teacher.name.toLocaleLowerCase().includes(this.searchTeacherName.toLocaleLowerCase())
      )
    }
    if (this.searchTeacherSurname != "") {
      this.allTeachersSearched = this.allTeachersSearched.filter(
        (teacher) => teacher.surname.toLocaleLowerCase().includes(this.searchTeacherSurname.toLocaleLowerCase())
      )
    }
    if (this.searchSubject != "") {
      this.allTeachersSearched = this.allTeachersSearched.filter((teacher) => {
        let foundSubject = false
        teacher.teacherSubjects.forEach(
          (subject) => {
            if (subject.toLocaleLowerCase().includes(this.searchSubject.toLocaleLowerCase())) {
              foundSubject = true
            }
          }
        )
        return foundSubject
      })
    }
    this.sortTeachers()
  }

  showSubjects() {
    this.shouldShowSubjects = true
  }

  hideSubjects() {
    this.shouldShowSubjects = false
    this.shouldShowSortedSubjects = false
    this.shouldSortSubjectsByTeacherName = this.shouldSortSubjectsByTeacherSurname = this.shouldSortSubjectsBySubject = false

  }

  showSearchResults() {
    this.search()
    this.shouldShowSearchResults = true
  }

  hideSearchResults() {
    this.shouldShowSearchResults = false
    this.shouldShowSortedTeachers = false
    this.shouldSortTeachersByTeacherName = this.shouldSortTeachersByTeacherSurname = this.shouldSortTeachersBySubject = false
  }
}
