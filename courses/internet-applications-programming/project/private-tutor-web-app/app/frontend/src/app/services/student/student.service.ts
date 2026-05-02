import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Class } from 'src/app/models/class.model';
import { Message } from 'src/app/models/message.model';
import { User } from 'src/app/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  backendUrl = "http://localhost:4000/student"

  constructor(private httpClient: HttpClient) { }

  updateStudentInfo(user: User) {
    return this.httpClient.post<Message>(`${this.backendUrl}/updateStudentInfo`, user)
  }

  getAllStudents() {
    return this.httpClient.get<User[]>(`${this.backendUrl}/getAllStudents`)
  }

  scheduleClass(classModel: Class) {
    return this.httpClient.post<Message>(`${this.backendUrl}/scheduleClass`, classModel)
  }

  isTimeSlotTaken(classModel: Class) {
    return this.httpClient.post<Class[]>(`${this.backendUrl}/isTimeSlotTaken`, classModel)
  }

  getAllUpcomingClasses(studentUsername: string) {
    return this.httpClient.get<Class[]>(`${this.backendUrl}/getAllUpcomingClasses?studentUsername=${studentUsername}`)
  }

  getAllPastClasses(studentUsername: string) {
    return this.httpClient.get<Class[]>(`${this.backendUrl}/getAllPastClasses?studentUsername=${studentUsername}`)
  }

  getRelevantClassesForNotifications(studentUsername: string) {
    return this.httpClient.get<Class[]>(`${this.backendUrl}/getRelevantClassesForNotifications?studentUsername=${studentUsername}`)
  }

  readNotification(classId: number) {
    return this.httpClient.get<Message>(`${this.backendUrl}/readNotification?classId=${classId}`)
  }

  rateTeacher(pastClass: Class) {
    return this.httpClient.post<Message>(`${this.backendUrl}/rateTeacher`, pastClass)
  }

  getClassesForCalendar(teacherUsername: string) {
    return this.httpClient.get<Class[]>(`${this.backendUrl}/getClassesForCalendar?teacherUsername=${teacherUsername}`)
  }
}
