import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Data } from 'src/app/models/data.model';
import { Message } from 'src/app/models/message.model';
import { User } from 'src/app/models/user.model';
import { DefaultService } from 'src/app/services/default/default.service';
import { StudentService } from 'src/app/services/student/student.service';

@Component({
  selector: 'app-update-student-info',
  templateUrl: './update-student-info.component.html',
  styleUrls: ['./update-student-info.component.css', '../../../../../../styles.css']
})
export class UpdateStudentInfoComponent {
  user: User = new User()

  newName: string = ""
  newSurname: string = ""
  newAddress: string = ""
  newEmail: string = ""
  newPhone: string = ""
  newSchoolType: string = ""
  newCurrentGrade: string = ""

  isInvalidImage: boolean = false
  profilePictureFormData: FormData | null = null

  profilePictureError: string = "Please upload an image in jpg/png format with size ranging from 100x100 px to 300x300 px."
  newEmailError: string = ""
  newPhoneError: string = ""
  newCurrentGradeError: string = ""

  data: Data = new Data()

  constructor(
    private defaultService: DefaultService,
    private studentService: StudentService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.defaultService.getData().subscribe(
      (data: Data[]) => {
        this.data = data[0]
      }
    )
    this.defaultService.getUser(JSON.parse(localStorage.getItem("loggedInUser")!).username).subscribe(
      (user) => {
        this.user = user
      }
    )
  }

  updateStudentInfo() {
    if (this.isEverythingEmpty() || !this.isEmailValid() || !this.isPhoneValid() || !this.areSchoolAndGradeValid()) return
    this.defaultService.checkIfUserWithEmailExists(this.newEmail).subscribe(
      (user: User) => {
        if (user != null) {
          this.newEmailError = "This email is not available."
          this.newEmail = ""
        } else {
          if (this.newAddress != "") this.user.address = this.newAddress
          if (this.newName != "") this.user.name = this.newName
          if (this.newSurname != "") this.user.surname = this.newSurname
          if (this.profilePictureFormData == null) {
            this.studentService.updateStudentInfo(this.user).subscribe(
              () => {
                this.router.navigate(["student-index"])
              }
            )
          } else {
            let oldProfilePicturePath = this.user.profilePicturePath
            this.defaultService.uploadProfilePicture(this.profilePictureFormData!).subscribe(
              (imageMessage: Message) => {
                if (this.didProfilePictureUploadFail(imageMessage)) return
                this.defaultService.deleteProfilePicture(oldProfilePicturePath).subscribe(
                  () => {
                    this.studentService.updateStudentInfo(this.user).subscribe(
                      () => {
                        this.router.navigate(["student-index"])
                      }
                    )
                  }
                )
              }
            )
          }
        }
      }
    )
  }

  didProfilePictureUploadFail(imageMessage: Message): boolean {
    let responseType = imageMessage.content.split("|")[0]
    let response = imageMessage.content.split("|")[1]
    if (responseType == "ERROR") {
      this.profilePictureError = response
      this.isInvalidImage = true
      return true
    }
    this.user.profilePicturePath = response
    return false
  }

  isEverythingEmpty(): boolean {
    return this.profilePictureFormData == null && this.newAddress == "" && this.newEmail == "" && this.newPhone == "" && this.newCurrentGrade == "" && this.newSchoolType == "" && this.newName == "" && this.newSurname == ""
  }

  onImageSelected(event: any) {
    let profilePictureFile = event.target.files[0] as File
    if (!this.isValidFileFormat(profilePictureFile, ["jpg", "png"])) {
      this.isInvalidImage = true
      this.profilePictureError = "Please upload an image in jpg/png format with size ranging from 100x100 px to 300x300 px."
      return
    }
    this.isInvalidImage = false
    this.profilePictureFormData = new FormData()
    this.profilePictureFormData.append(
      "profilePicture",
      profilePictureFile as Blob,
      profilePictureFile.name
    )
  }

  isValidFileFormat(file: File, allowedFileExtensions: string[]): boolean {
    const fileExtension = file.name.split(".").pop()?.toLocaleLowerCase()
    if (fileExtension == null) return false
    return allowedFileExtensions.includes(fileExtension)
  }

  updateSchoolType(schoolType: string) {
    this.newCurrentGrade = ""
    this.newSchoolType = schoolType
  }

  updateCurrentGrade(currentGrade: string) {
    this.newCurrentGrade = currentGrade
  }

  isEmailValid(): boolean {
    if (this.newEmail == "") return true
    const emailRegex = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$/i
    const isValid = emailRegex.test(this.newEmail)
    if (!isValid) {
      this.newEmailError = "Invalid email format."
      this.newEmail = ""
    } else {
      this.user.email = this.newEmail
    }
    return isValid
  }

  isPhoneValid(): boolean {
    if (this.newPhone == "") return true
    const phoneRegex = /^06[0-6]\/[0-9]{3}-[0-9]{3,4}$/
    const isValid = phoneRegex.test(this.newPhone)
    if (!isValid) {
      this.newPhoneError = "Invalid phone format."
      this.newPhone = ""
    } else {
      this.user.phone = this.newPhone
    }
    return isValid
  }

  areSchoolAndGradeValid(): boolean {
    if (this.newSchoolType == "" && this.newCurrentGrade == "") return true
    let areValid = true
    // Student can't go back to primary school from secondary school.
    if (this.newSchoolType != "" && this.newSchoolType == this.data.schoolTypes[0] && this.newSchoolType != this.user.schoolType)
      areValid = false
    if (this.newCurrentGrade != "") {
      // Student that is in secondary school can't be in grades 5-8.
      if (this.newSchoolType == "" && this.user.schoolType != this.data.schoolTypes[0] && this.newCurrentGrade >= "4")
        areValid = false
      // Student that doesn't want to change his school, just his current grade, can't go backwards regarding his current grade.
      if (this.newSchoolType == "" && this.newCurrentGrade < this.user.currentGrade)
        areValid = false
      // Student that wants to still be in primary school can't go backwards regarding his current grade.
      if (this.newSchoolType != "" && this.newSchoolType == this.data.schoolTypes[0]
        && this.user.schoolType == this.data.schoolTypes[0] && this.newCurrentGrade <= this.user.currentGrade)
        areValid = false
      // Student that wants to still be in secondary school can't go backwards regarding his current grade.
      if (this.newSchoolType != "" && this.newSchoolType != this.data.schoolTypes[0]
        && this.user.schoolType != this.data.schoolTypes[0] && this.newCurrentGrade <= this.user.currentGrade)
        areValid = false
    }
    if (areValid) {
      if (this.newSchoolType != "") this.user.schoolType = this.newSchoolType
      if (this.newCurrentGrade != "") this.user.currentGrade = this.newCurrentGrade
    } else {
      this.newSchoolType = ""
      this.newCurrentGrade = ""
      this.newCurrentGradeError = "Current grade can only be incremented."
    }
    return areValid
  }
}
