import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { signal, ChangeDetectorRef } from '@angular/core';
import { CalendarOptions, DateSelectArg, EventApi, EventInput } from '@fullcalendar/core';
import interactionPlugin from '@fullcalendar/interaction';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import listPlugin from '@fullcalendar/list';
import { User } from 'src/app/models/user.model';
import { DefaultService } from 'src/app/services/default/default.service';
import { Class } from 'src/app/models/class.model';
import { StudentService } from 'src/app/services/student/student.service';

const NUMBER_OF_MINUTES_IN_ONE_HOUR = 60
const NUMBER_OF_SECONDS_IN_ONE_MINUTE = 60
const NUMBER_OF_MILLISECONDS_IN_ONE_SECOND = 1000

const NUMBER_OF_MILLISECONDS_IN_ONE_HOUR = NUMBER_OF_MINUTES_IN_ONE_HOUR * NUMBER_OF_SECONDS_IN_ONE_MINUTE * NUMBER_OF_MILLISECONDS_IN_ONE_SECOND

let EVENT_ID = 0

@Component({
  selector: 'app-student-schedule-class-calendar',
  templateUrl: './student-schedule-class-calendar.component.html',
  styleUrls: ['./student-schedule-class-calendar.component.css']
})
export class StudentScheduleClassCalendarComponent implements OnInit {
  teacher: User = new User()
  class: Class = new Class()

  successMessage: string = "Thanks!"
  subjectError: string = "Please choose a subject."
  dateError: string = "Please choose a date."
  timeError: string = "Please choose class time."

  didUserSelectTime: boolean = false

  @ViewChild("openModalButton") openModalButton: ElementRef | undefined
  @ViewChild("hideModalButton") hideModalButton: ElementRef | undefined

  numberToStringDaysMappings: Map<number, string> = new Map<number, string>()

  initialEventsList: EventInput[] = []
  currentSelectInfo: DateSelectArg | any

  calendarOptions = signal<CalendarOptions>({
    plugins: [
      interactionPlugin,
      dayGridPlugin,
      timeGridPlugin,
      listPlugin,
    ],
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek'
    },
    initialView: 'dayGridMonth',
    timeZone: "UTC",
    events: [],
    weekends: true,
    editable: false,
    selectable: true,
    selectMirror: true,
    dayMaxEvents: true,
    height: 480,
    select: this.handleDateSelect.bind(this),
    eventsSet: this.handleEvents.bind(this),
  });
  currentEvents = signal<EventApi[]>([]);

  constructor(
    private changeDetector: ChangeDetectorRef,
    private defaultService: DefaultService,
    private studentService: StudentService
  ) { }

  ngOnInit(): void {
    this.initializeDaysMappings()
    this.defaultService.getUser(localStorage.getItem("studentSeeTeacherDetailsUsername")!).subscribe(
      (teacher) => {
        this.teacher = teacher
        let notWorkingDays: string[] = []
        let workingDays: string[] = []
        this.numberToStringDaysMappings.forEach((dayName: string, dayId: number) => {
          if (!teacher.workingDays.includes(dayName)) notWorkingDays.push(String(dayId))
          else workingDays.push(String(dayId))
        })
        if (notWorkingDays.length > 0) {
          this.initialEventsList.push(
            {
              title: "Not working",
              daysOfWeek: notWorkingDays,
              allDay: true,
              backgroundColor: "red",
              color: "black",
              textColor: "black",
            }
          )
        }
        let teacherWorkTimeStart = teacher.workingHours.substring(0, teacher.workingHours.indexOf("-"))
        let teacherWorkTimeEnd = teacher.workingHours.substring(teacher.workingHours.indexOf("-") + 1)
        if (teacherWorkTimeEnd == "00:00") teacherWorkTimeEnd = "24:00"
        if (teacherWorkTimeStart > "00:00") {
          this.initialEventsList.push(
            {
              title: "Not working",
              startTime: "00:00:00",
              endTime: teacherWorkTimeStart + ":00",
              daysOfWeek: workingDays,
              allDay: false,
              backgroundColor: "red",
              color: "black",
              textColor: "black",
            }
          )
        }
        if (teacherWorkTimeEnd < "24:00") {
          this.initialEventsList.push(
            {
              title: "Not working",
              startTime: teacherWorkTimeEnd + "00:00",
              endTime: "24:00:00",
              daysOfWeek: workingDays,
              allDay: false,
              backgroundColor: "red",
              color: "black",
              textColor: "black",
            }
          )
        }
        this.initializeClass()
        this.studentService.getClassesForCalendar(this.teacher.username).subscribe(
          (classes: Class[]) => {
            classes.forEach(
              (currentClass: Class) => {
                this.initialEventsList.push(
                  {
                    id: String(EVENT_ID++),
                    title: currentClass.subject,
                    start: new Date(this.getDateTimeIsoString(currentClass.startDate, currentClass.startTime)),
                    end: new Date(this.getDateTimeIsoString(currentClass.endDate, currentClass.endTime)),
                    allDay: false,
                    backgroundColor: currentClass.isClassAccepted ? "limegreen" : "yellow",
                    color: "black",
                    textColor: "black",
                  }
                )
              }
            )
            this.calendarOptions.mutate((options) => {
              options.events = this.initialEventsList
            });
          }
        )
      }
    )
  }

  getDateTimeIsoString(dateString: string, timeString: string): string {
    return dateString + "T" + timeString + ":" + "00.000Z"
  }

  handleDateSelect(selectInfo: DateSelectArg) {
    this.currentSelectInfo = selectInfo
    this.class.startDate = ""
    this.class.startTime = ""
    if (this.teacher.teacherSubjects.length > 1) this.class.subject = ""
    if (selectInfo.startStr.includes("T")) {
      // User selected both date and time.
      this.class.startDate = selectInfo.startStr.substring(0, selectInfo.startStr.indexOf("T"))
      this.class.startTime = selectInfo.startStr.substring(selectInfo.startStr.indexOf("T") + 1,
        selectInfo.startStr.indexOf("T") + 6)
      this.didUserSelectTime = true
    } else {
      // User selected only date.
      this.class.startDate = selectInfo.startStr
      this.didUserSelectTime = false
    }
    this.openModal()
  }

  handleEvents(events: EventApi[]) {
    this.currentEvents.set(events);
    this.changeDetector.detectChanges();
  }

  initializeClass() {
    this.class = new Class()
    this.class.teacherUsername = this.teacher.username
    this.class.studentUsername = JSON.parse(localStorage.getItem("loggedInUser")!).username
    if (this.teacher.teacherSubjects.length == 1) this.class.subject = this.teacher.teacherSubjects[0]
  }

  async scheduleClass() {
    let isSchedulingInputValid = await this.isSchedulingInputValid()
    if (!isSchedulingInputValid) return
    this.studentService.scheduleClass(this.class).subscribe(
      () => {
        this.hideModal()
        const calendarApi = this.currentSelectInfo.view.calendar
        calendarApi.addEvent({
          id: String(EVENT_ID++),
          title: this.class.subject,
          start: new Date(this.getClassStartDateTimeInMillis()),
          end: new Date(this.getClassStartDateTimeInMillis()),
          allDay: false,
          backgroundColor: "yellow",
          color: "black",
          textColor: "black"
        });
        this.initializeClass()
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

  isSomeClassDataMissing(): boolean {
    return this.class.subject == "" || this.class.startDate == "" || this.class.startTime == ""
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

  setClassEndTime() {
    let startTimeHour: string = this.class.startTime.substring(0, this.class.startTime.indexOf(":"))
    let startTimeMinute: string = this.class.startTime.substring(this.class.startTime.indexOf(":") + 1)
    let endTimeHour: string = String((Number(startTimeHour) + 1) % 24)
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
      endDateInMillis += NUMBER_OF_MILLISECONDS_IN_ONE_HOUR
      let endDateTime = new Date(endDateInMillis).toISOString()
      let endDate = endDateTime.substring(0, endDateTime.indexOf("T"))
      this.class.endDate = endDate
    }
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

  isTimeSlotAlreadyTaken(): Promise<boolean> {
    return new Promise<boolean>((resolve) => {
      this.studentService.isTimeSlotTaken(this.class).subscribe(
        (classRequests: Class[]) => {
          resolve(classRequests.length > 0)
        }
      )
    })
  }

  openModal() {
    this.openModalButton!.nativeElement.click()
  }

  hideModal() {
    this.hideModalButton!.nativeElement.click()
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

  getClassStartDateTimeInMillis(): number {
    let dayMillis = new Date(this.class.startDate).getTime()
    let hourMillis = (Number(this.class.startTime.substring(0, this.class.startTime.indexOf(":")))) * NUMBER_OF_MILLISECONDS_IN_ONE_HOUR
    let minuteMillis = Number(this.class.startTime.substring(this.class.startTime.indexOf(":") + 1)) * NUMBER_OF_SECONDS_IN_ONE_MINUTE * NUMBER_OF_MILLISECONDS_IN_ONE_SECOND
    return dayMillis + hourMillis + minuteMillis
  }

  getClassEndDateTimeInMillis(): number {
    let dayMillis = new Date(this.class.endDate).getTime()
    let hourMillis = (Number(this.class.endTime.substring(0, this.class.endTime.indexOf(":")))) * NUMBER_OF_MILLISECONDS_IN_ONE_HOUR
    let minuteMillis = Number(this.class.endTime.substring(this.class.endTime.indexOf(":") + 1)) * NUMBER_OF_SECONDS_IN_ONE_MINUTE * NUMBER_OF_MILLISECONDS_IN_ONE_SECOND
    return dayMillis + hourMillis + minuteMillis
  }

  updateChosenSubject(subject: string) {
    this.class.subject = subject
  }
}
