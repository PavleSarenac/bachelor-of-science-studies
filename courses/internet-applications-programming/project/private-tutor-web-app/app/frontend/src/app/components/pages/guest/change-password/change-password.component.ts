import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user.model';
import { DefaultService } from 'src/app/services/default/default.service';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css', '../../../../../styles.css']
})
export class ChangePasswordComponent {
  doesUserRemember: boolean = false
  didGetSecurityQuestion: boolean = false
  didKnowSecurityAnswer: boolean = false

  username: string = ""
  oldPassword: string = ""
  newPassword: string = ""

  usernameError: string = "Please enter your username."
  oldPasswordError: string = "Please enter your old password."
  newPasswordError: string = "Please enter your new password."

  username2: string = ""
  securityQuestion: string = ""
  securityAnswer: string = ""
  newPassword2: string = ""
  newPassword2Repeated: string = ""

  username2Error: string = "Please enter your username."
  securityAnswerError: string = "Please enter your security answer."
  newPassword2Error: string = "Please enter your new password."
  newPassword2RepeatedError: string = "Please enter your new password again."

  successMessage: string = "Thanks!"

  user: User = new User()

  constructor(private defaultService: DefaultService, private router: Router) { }

  getSecurityQuestion() {
    if (this.username2 == "") return
    this.defaultService.getUser(this.username2).subscribe(
      (user) => {
        if (user == null) {
          this.username2 = ""
          this.username2Error = "That username doesn't exist in the database."
          return
        }
        this.user = user
        this.securityQuestion = user.securityQuestion
        this.didGetSecurityQuestion = true
      }
    )
  }

  confirmSecurityAnswer() {
    if (this.securityAnswer == "") return
    if (this.user.securityAnswer == this.securityAnswer) {
      this.didKnowSecurityAnswer = true
    } else {
      this.securityAnswer = ""
      this.securityAnswerError = "Wrong answer."
    }
  }

  changePassword() {
    if (this.doesUserRemember) {
      this.changePasswordFirstWay()
    } else {
      this.changePasswordSecondWay()
    }
  }

  changePasswordFirstWay() {
    if (this.username == "" || this.oldPassword == "" || this.newPassword == "") return
    if (this.oldPassword == this.newPassword) {
      this.newPasswordError = "Your new password can't be the same as the old one."
      this.newPassword = ""
      return
    }
    if (!this.isPasswordValid(this.newPassword)) {
      this.newPasswordError = "Invalid password format."
      this.newPassword = ""
      return
    }
    this.defaultService.checkOldPassword(this.username, this.oldPassword).subscribe(
      (user) => {
        if (user == null) {
          this.oldPasswordError = "Username or old password is not good."
          this.oldPassword = ""
          this.usernameError = "Username or old password is not good."
          this.username = ""
          return
        }
        this.defaultService.changePassword(this.username, this.newPassword).subscribe(
          () => {
            if (user.userType == "admin") {
              this.router.navigate(["private-login"])
            } else {
              this.router.navigate(["public-login"])
            }
          }
        )
      }
    )
  }

  changePasswordSecondWay() {
    if (this.newPassword2 == "" || this.newPassword2Repeated == "") return
    if (this.newPassword2 != this.newPassword2Repeated) {
      this.newPassword2 = this.newPassword2Repeated = ""
      this.newPassword2Error = this.newPassword2RepeatedError = "Passwords have to be identical."
      return
    }
    if (!this.isPasswordValid(this.newPassword2)) {
      this.newPassword2 = this.newPassword2Repeated = ""
      this.newPassword2Error = this.newPassword2RepeatedError = "Invalid password format."
      return
    }
    this.defaultService.changePassword(this.user.username, this.newPassword2).subscribe(
      () => {
        if (this.user.userType == "admin") {
          this.router.navigate(["private-login"])
        } else {
          this.router.navigate(["public-login"])
        }
      }
    )
  }

  isPasswordValid(password: string): boolean {
    const passwordRegex = /^(?=[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[A-Z])(?=.*[a-z].*[a-z].*[a-z]).{6,10}$/
    const isValid = passwordRegex.test(password)
    return isValid
  }
}
