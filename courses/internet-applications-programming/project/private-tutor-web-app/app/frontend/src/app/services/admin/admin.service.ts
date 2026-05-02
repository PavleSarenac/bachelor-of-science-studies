import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Class } from 'src/app/models/class.model';
import { Message } from 'src/app/models/message.model';
import { User } from 'src/app/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  backendUrl = "http://localhost:4000/admin"

  constructor(private httpClient: HttpClient) { }

  downloadPdf(teacher: User) {
    return this.httpClient.post(`${this.backendUrl}/downloadPdf`, teacher, { responseType: "blob" })
  }

  approveTeacherRegistration(teacher: User) {
    return this.httpClient.post<Message>(`${this.backendUrl}/approveTeacherRegistration`, teacher)
  }

  banTeacherAccount(teacherUsername: string) {
    return this.httpClient.get<Message>(`${this.backendUrl}/banTeacherAccount?teacherUsername=${teacherUsername}`)
  }

  updateSubjects(subjects: string[]) {
    return this.httpClient.post<Message>(`${this.backendUrl}/updateSubjects`, subjects)
  }

  getAverageClassesPerDay() {
    return this.httpClient.get<any>(`${this.backendUrl}/getAverageClassesPerDay`)
  }

  getMostWantedTeachers() {
    return this.httpClient.get<any>(`${this.backendUrl}/getMostWantedTeachers`)
  }

  getAllClasses() {
    return this.httpClient.get<Class[]>(`${this.backendUrl}/getAllClasses`)
  }

  getAllClassesWithRatedStudent() {
    return this.httpClient.get<Class[]>(`${this.backendUrl}/getAllClassesWithRatedStudent`)
  }

  getAllClassesWithRatedTeacher() {
    return this.httpClient.get<Class[]>(`${this.backendUrl}/getAllClassesWithRatedTeacher`)
  }
}
