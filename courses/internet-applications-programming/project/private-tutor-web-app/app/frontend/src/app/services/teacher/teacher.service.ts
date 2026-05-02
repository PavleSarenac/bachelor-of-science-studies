import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Class } from 'src/app/models/class.model';
import { Message } from 'src/app/models/message.model';
import { User } from 'src/app/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class TeacherService {
  backendUrl = "http://localhost:4000/teacher"

  constructor(private httpClient: HttpClient) { }

  uploadCv(formData: FormData) {
    return this.httpClient.post<Message>(`${this.backendUrl}/uploadCv`, formData)
  }

  getAllActiveTeachers() {
    return this.httpClient.get<User[]>(`${this.backendUrl}/getAllActiveTeachers`)
  }

  getAllPendingTeachers() {
    return this.httpClient.get<User[]>(`${this.backendUrl}/getAllPendingTeachers`)
  }

  updateTeacherInfo(teacher: User) {
    return this.httpClient.post<Message>(`${this.backendUrl}/updateTeacherInfo`, teacher)
  }

  getTeachersTeachingSpecificStudentAge(studentAge: string) {
    return this.httpClient.get<User[]>(`${this.backendUrl}/getTeachersTeachingSpecificStudentAge?studentAge=${studentAge}`)
  }

  updateWorktime(teacher: User) {
    return this.httpClient.post<Message>(`${this.backendUrl}/updateWorktime`, teacher)
  }

  getAllPendingClassRequests(teacherUsername: string) {
    return this.httpClient.get<Class[]>(`${this.backendUrl}/getAllPendingClassRequests?teacherUsername=${teacherUsername}`)
  }

  deleteExpiredClassRequests() {
    return this.httpClient.get<Message>(`${this.backendUrl}/deleteExpiredClassRequests`)
  }

  acceptClassRequest(classRequest: Class) {
    return this.httpClient.post<Message>(`${this.backendUrl}/acceptClassRequest`, classRequest)
  }

  rejectClassRequest(classRequest: Class) {
    return this.httpClient.post<Message>(`${this.backendUrl}/rejectClassRequest`, classRequest)
  }

  cancelClass(upcomingClass: Class) {
    return this.httpClient.post<Message>(`${this.backendUrl}/cancelClass`, upcomingClass)
  }

  getAllAcceptedClassesForNextThreeDays(teacherUsername: string) {
    return this.httpClient.get<Class[]>(`${this.backendUrl}/getAllAcceptedClassesForNextThreeDays?teacherUsername=${teacherUsername}`)
  }

  getAllDoneClasses(teacherUsername: string) {
    return this.httpClient.get<Class[]>(`${this.backendUrl}/getAllDoneClasses?teacherUsername=${teacherUsername}`)
  }

  rateStudent(doneClass: Class) {
    return this.httpClient.post<Message>(`${this.backendUrl}/rateStudent`, doneClass)
  }
}
