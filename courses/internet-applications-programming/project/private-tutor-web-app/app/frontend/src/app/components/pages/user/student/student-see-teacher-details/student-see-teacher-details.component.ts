import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Class } from 'src/app/models/class.model';
import { User } from 'src/app/models/user.model';
import { DefaultService } from 'src/app/services/default/default.service';
import { StudentService } from 'src/app/services/student/student.service';
import { TeacherService } from 'src/app/services/teacher/teacher.service';

const NUMBER_OF_MINUTES_IN_ONE_HOUR = 60
const NUMBER_OF_SECONDS_IN_ONE_MINUTE = 60
const NUMBER_OF_MILLISECONDS_IN_ONE_SECOND = 1000

const NUMBER_OF_MILLISECONDS_IN_ONE_HOUR = NUMBER_OF_MINUTES_IN_ONE_HOUR * NUMBER_OF_SECONDS_IN_ONE_MINUTE * NUMBER_OF_MILLISECONDS_IN_ONE_SECOND

@Component({
  selector: 'app-student-see-teacher-details',
  templateUrl: './student-see-teacher-details.component.html',
  styleUrls: ['./student-see-teacher-details.component.css']
})
export class StudentSeeTeacherDetailsComponent implements OnInit {
  teacher: User = new User()
  profilePictureUrl: any = null

  allReviewedClasses: Class[] = []

  successMessage: string = "Thanks!"
  subjectError: string = "Please choose a subject."
  dateError: string = "Please choose a date."
  timeError: string = "Please choose class time."

  class: Class = new Class()
  isDoubleClass: boolean = false

  @ViewChild("closeModalButton") closeModal: ElementRef | undefined

  numberToStringDaysMappings: Map<number, string> = new Map<number, string>()

  constructor(
    private defaultService: DefaultService,
    private studentService: StudentService,
    private teacherService: TeacherService
  ) { }

  ngOnInit(): void {
    this.initializeDaysMappings()
    this.defaultService.getUser(localStorage.getItem("studentSeeTeacherDetailsUsername")!).subscribe(
      (teacher) => {
        this.teacher = teacher
        this.initializeClass()
        this.defaultService.getProfilePicture(this.teacher.profilePicturePath).subscribe(
          (profilePictureFile: Blob) => {
            const fileReader = new FileReader()
            fileReader.onload = (e) => {
              this.profilePictureUrl = e.target?.result
            }
            fileReader.readAsDataURL(
              new Blob(
                [profilePictureFile],
                { type: "image/" + this.teacher.profilePicturePath.split(".").pop()?.toLocaleLowerCase() }
              )
            )
            this.teacherService.getAllDoneClasses(this.teacher.username).subscribe(
              (classes: Class[]) => {
                classes.forEach(
                  (doneClass: Class) => {
                    if (doneClass.studentToTeacherGrade != 0) {
                      this.allReviewedClasses.push(doneClass)
                    }
                  }
                )
                this.allReviewedClasses.sort((class1, class2) => {
                  const dateComparison = class2.startDate.localeCompare(class1.startDate)
                  if (dateComparison == 0) {
                    return class2.startTime.localeCompare(class1.startTime)
                  }
                  return dateComparison
                })
              }
            )
          }
        )
      }
    )
  }

  hideModal() {
    this.closeModal!.nativeElement.click()
    this.subjectError = "Please choose a subject."
    this.dateError = "Please choose a date."
    this.timeError = "Please choose class time."
  }

  initializeDaysMappings() {
    this.numberToStringDaysMappings.set(1, "Monday")
    this.numberToStringDaysMappings.set(2, "Tuesday")
    this.numberToStringDaysMappings.set(3, "Wednesday")
    this.numberToStringDaysMappings.set(4, "Thursday")
    this.numberToStringDaysMappings.set(5, "Friday")
    this.numberToStringDaysMappings.set(6, "Saturday")
    this.numberToStringDaysMappings.set(0, "Sunday")
  }

  async scheduleClass() {
    let isSchedulingInputValid = await this.isSchedulingInputValid()
    if (!isSchedulingInputValid) return
    this.studentService.scheduleClass(this.class).subscribe(
      () => {
        this.hideModal()
        setTimeout(() => {
          this.initializeClass()
        }, 1500)
      }
    )
  }

  async isSchedulingInputValid(): Promise<boolean> {
    if (this.isSomeClassDataMissing()) return false
    if (this.isClassDateTimeStartInThePast()) return false
    this.setClassEndTime()
    this.setClassEndDate()
    if (!this.isClassAtHalfOrFullHour()) return false
    if (!this.isTeacherWorking()) {
      this.class.startDate = ""
      this.class.startTime = ""
      this.dateError = this.timeError = `${this.teacher.name} ${this.teacher.surname} isn't working at that time.`
      return false
    }
    if (await this.isTimeSlotAlreadyTaken()) {
      this.class.startDate = ""
      this.class.startTime = ""
      this.dateError = this.timeError = `There is already a class request for that time.`
      return false
    }
    return true
  }

  isTimeSlotAlreadyTaken(): Promise<boolean> {
    return new Promise<boolean>((resolve) => {
      this.studentService.isTimeSlotTaken(this.class).subscribe(
        (classRequests: Class[]) => {
          resolve(classRequests.length > 0)
        }
      )
    })
  }

  isClassDateTimeStartInThePast(): boolean {
    let currentDateTimeInMillis = Date.now() + NUMBER_OF_MILLISECONDS_IN_ONE_HOUR
    let classStartDateTimeInMillis = this.getClassStartDateTimeInMillis()
    if (classStartDateTimeInMillis < currentDateTimeInMillis) {
      this.class.startDate = ""
      this.class.startTime = ""
      this.dateError = this.timeError = "You have to schedule a class in the future."
      return true
    }
    return false
  }

  getClassStartDateTimeInMillis(): number {
    let dayMillis = new Date(this.class.startDate).getTime()
    let hourMillis = (Number(this.class.startTime.substring(0, this.class.startTime.indexOf(":")))) * NUMBER_OF_MILLISECONDS_IN_ONE_HOUR
    let minuteMillis = Number(this.class.startTime.substring(this.class.startTime.indexOf(":") + 1)) * NUMBER_OF_SECONDS_IN_ONE_MINUTE * NUMBER_OF_MILLISECONDS_IN_ONE_SECOND
    return dayMillis + hourMillis + minuteMillis
  }

  setClassEndTime() {
    let startTimeHour: string = this.class.startTime.substring(0, this.class.startTime.indexOf(":"))
    let startTimeMinute: string = this.class.startTime.substring(this.class.startTime.indexOf(":") + 1)
    let endTimeHour: string = String((Number(startTimeHour) + (this.isDoubleClass ? 2 : 1)) % 24)
    if (endTimeHour.length == 1) {
      endTimeHour = "0" + endTimeHour
    }
    let endTimeMinute: string = startTimeMinute
    this.class.endTime = endTimeHour + ":" + endTimeMinute
  }

  setClassEndDate() {
    if (this.class.endTime > this.class.startTime) {
      this.class.endDate = this.class.startDate
    } else {
      let endDateInMillis = this.getClassStartDateTimeInMillis()
      endDateInMillis += NUMBER_OF_MILLISECONDS_IN_ONE_HOUR * (this.isDoubleClass ? 2 : 1)
      let endDateTime = new Date(endDateInMillis).toISOString()
      let endDate = endDateTime.substring(0, endDateTime.indexOf("T"))
      this.class.endDate = endDate
    }
  }

  isTeacherWorking(): boolean {
    let classStartDay: number = new Date(this.class.startDate).getDay()
    let classEndDay: number = new Date(this.class.endDate).getDay()

    let teacherWorktimeStart = this.teacher.workingHours.substring(0, this.teacher.workingHours.indexOf("-"))
    let teacherWorktimeEnd = this.teacher.workingHours.substring(this.teacher.workingHours.indexOf("-") + 1)
    let classStartTime = this.class.startTime
    let classEndtime = this.class.endTime

    if (teacherWorktimeEnd == "00:00") teacherWorktimeEnd = "24:00"
    if (classEndtime == "00:00") classEndtime = "24:00"

    // If class spans across two days, and teacher isn't working on the second day.
    if ((!this.teacher.workingDays.includes(this.numberToStringDaysMappings.get(classStartDay)!) ||
      !this.teacher.workingDays.includes(this.numberToStringDaysMappings.get(classEndDay)!)) &&
      (!(classEndtime == "24:00" && teacherWorktimeEnd == "24:00"))
    ) {
      return false
    }

    // If class begins on one day, and ends on that same day.
    if (classStartDay == classEndDay && (classStartTime < teacherWorktimeStart || classEndtime > teacherWorktimeEnd))
      return false

    // If class lasts until 00:00.
    if (classStartDay != classEndDay && classStartTime >= teacherWorktimeStart
      && classEndtime == "24:00" && teacherWorktimeEnd == "24:00")
      return true

    // If class spans across two different days (e.g 23:00-01:00) - for this to be possible, teacher has to work 24/7.
    if (classEndDay != classStartDay && (teacherWorktimeStart != "00:00" || teacherWorktimeEnd != "24:00"))
      return false

    return true
  }

  isClassAtHalfOrFullHour(): boolean {
    let startMinute = Number(this.class.startTime.substring(this.class.startTime.indexOf(":") + 1))
    let endMinute = Number(this.class.endTime.substring(this.class.endTime.indexOf(":") + 1))
    if (!((startMinute == 0 || startMinute == 30) && (endMinute == 0 || endMinute == 30))) {
      this.timeError = "Class has to start at the full hour or at the half-hour!"
      this.class.startTime = ""
      return false
    }
    return true
  }

  isSomeClassDataMissing(): boolean {
    return this.class.subject == "" || this.class.startDate == "" || this.class.startTime == ""
  }

  initializeClass() {
    this.class = new Class()
    this.class.teacherUsername = this.teacher.username
    this.class.studentUsername = JSON.parse(localStorage.getItem("loggedInUser")!).username
    if (this.teacher.teacherSubjects.length == 1) this.class.subject = this.teacher.teacherSubjects[0]
  }

  updateChosenSubject(subject: string) {
    this.class.subject = subject
  }
}
