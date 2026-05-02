import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Data } from 'src/app/models/data.model';
import { Message } from 'src/app/models/message.model';
import { User } from 'src/app/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class DefaultService {
  backendUrl = "http://localhost:4000"

  constructor(private httpClient: HttpClient) { }

  login(username: string, password: string) {
    const body = {
      username: username,
      password: password
    }
    return this.httpClient.post<User>(`${this.backendUrl}/login`, body)
  }

  uploadProfilePicture(formData: FormData) {
    return this.httpClient.post<Message>(`${this.backendUrl}/uploadProfilePicture`, formData)
  }

  deleteProfilePicture(profilePicturePath: string) {
    const body = {
      profilePicturePath: profilePicturePath
    }
    return this.httpClient.post<Message>(`${this.backendUrl}/deleteProfilePicture`, body)
  }

  register(user: User) {
    return this.httpClient.post<Message>(`${this.backendUrl}/register`, user)
  }

  getNumberOfStudents() {
    return this.httpClient.get<Message>(`${this.backendUrl}/getNumberOfStudents`)
  }

  getNumberOfTeachers() {
    return this.httpClient.get<Message>(`${this.backendUrl}/getNumberOfTeachers`)
  }

  getNumberOfDoneClassesLastWeek() {
    return this.httpClient.get<Message>(`${this.backendUrl}/getNumberOfDoneClassesLastWeek`)
  }

  getNumberOfDoneClassesLastMonth() {
    return this.httpClient.get<Message>(`${this.backendUrl}/getNumberOfDoneClassesLastMonth`)
  }

  checkOldPassword(username: string, oldPassword: string) {
    const body = {
      username: username,
      oldPassword: oldPassword
    }
    return this.httpClient.post<User>(`${this.backendUrl}/checkOldPassword`, body)
  }

  getData() {
    return this.httpClient.get<Data[]>(`${this.backendUrl}/getData`)
  }

  changePassword(username: string, newPassword: string) {
    const body = {
      username: username,
      newPassword: newPassword
    }
    return this.httpClient.post<User>(`${this.backendUrl}/changePassword`, body)
  }

  getUser(username: string) {
    return this.httpClient.get<User>(`${this.backendUrl}/getUser?username=${username}`)
  }

  getProfilePicture(filepath: string) {
    const body = {
      filepath: filepath
    }
    return this.httpClient.post(`${this.backendUrl}/getProfilePicture`, body, { responseType: "blob" })
  }

  checkIfUserWithEmailExists(email: string) {
    return this.httpClient.get<User>(`${this.backendUrl}/checkIfUserWithEmailExists?email=${email}`)
  }
}
