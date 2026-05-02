import { Component } from '@angular/core';
import { User } from 'src/app/models/user.model';
import { DefaultService } from 'src/app/services/default/default.service';

@Component({
  selector: 'app-teacher-index',
  templateUrl: './teacher-index.component.html',
  styleUrls: ['./teacher-index.component.css', '../../../../../../styles.css']
})
export class TeacherIndexComponent {
  user: User = new User()
  profilePictureUrl: any = null

  constructor(private defaultService: DefaultService) { }

  ngOnInit(): void {
    this.defaultService.getUser(JSON.parse(localStorage.getItem("loggedInUser")!).username).subscribe(
      (user) => {
        this.user = user
        this.defaultService.getProfilePicture(this.user.profilePicturePath).subscribe(
          (profilePictureFile: Blob) => {
            const fileReader = new FileReader()
            fileReader.onload = (e) => {
              this.profilePictureUrl = e.target?.result
            }
            fileReader.readAsDataURL(
              new Blob(
                [profilePictureFile],
                { type: "image/" + this.user.profilePicturePath.split(".").pop()?.toLocaleLowerCase() }
              )
            )
          }
        )
      }
    )
  }

  storeTeacherToBeUpdatedUsernameInLocalStorage() {
    localStorage.setItem("teacherToBeUpdatedUsername", this.user.username)
  }
}
