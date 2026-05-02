import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user.model';
import { DefaultService } from 'src/app/services/default/default.service';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css']
})
export class LoginFormComponent implements OnInit {
  username: string = ""
  password: string = ""

  usernameError: string = "Please enter your username."
  passwordError: string = "Please enter your password."

  buttonText: string = ""

  constructor(private deafultService: DefaultService, private router: Router) { }

  ngOnInit(): void {
    if (this.router.routerState.snapshot.url == "/public-login") {
      this.buttonText = "Login As Student/Teacher"
    } else {
      this.buttonText = "Login As Admin"
    }
  }

  login() {
    if (this.username == "" || this.password == "") {
      this.usernameError = "Please enter your username."
      this.passwordError = "Please enter your password."
      return
    }
    this.deafultService.login(this.username, this.password).subscribe(
      (user: User) => {
        let validUserTypes = this.router.routerState.snapshot.url == "/public-login" ? ["student", "teacher"] : ["admin"]
        if (user == null || (user != null && !validUserTypes.includes(user.userType))) {
          this.usernameError = "Username or password is wrong."
          this.passwordError = "Username or password is wrong."
          this.username = ""
          this.password = ""
          return
        }
        localStorage.setItem("loggedInUser", JSON.stringify(
          {
            username: user.username,
            userType: user.userType
          }
        ))
        if (user.userType == "student") {
          this.router.navigate(["student-index"])
        } else if (user.userType == "teacher") {
          this.router.navigate(["teacher-index"])
        } else {
          this.router.navigate(["admin-index"])
        }
      }
    )
  }
}
