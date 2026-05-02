import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user.model';
import { DefaultService } from 'src/app/services/default/default.service';
import { StudentService } from 'src/app/services/student/student.service';
import { forkJoin, of, Subject } from 'rxjs';
import { catchError, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-admin-all-students',
  templateUrl: './admin-all-students.component.html',
  styleUrls: ['./admin-all-students.component.css', '../../../../../../styles.css']
})
export class AdminAllStudentsComponent implements OnInit {
  allStudentsData: any[] = []
  shouldLoadContent: boolean = false
  doActiveStudentsExist: boolean = false

  private ngUnsubscribe = new Subject<void>();

  constructor(private studentService: StudentService, private defaultService: DefaultService) { }

  ngOnInit(): void {
    this.fetchTeachersData()
  }

  fetchTeachersData() {
    this.studentService.getAllStudents()
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(
        (students: User[]) => {
          if (students.length > 0) {
            this.doActiveStudentsExist = true
          } else {
            this.doActiveStudentsExist = false
          }
          this.shouldLoadContent = true
          const requests = students.map((student: User) =>
            this.defaultService.getProfilePicture(student.profilePicturePath)
              .pipe(
                catchError((error) => {
                  console.error(`Error loading picture for student ${student.username}:`, error);
                  // Returning an observable with `null` to keep the array length consistent
                  return of(null);
                })
              )
          );

          forkJoin(requests).subscribe(
            (profilePictures: Blob[] | any) => {
              students.forEach((student, index) => {
                const profilePictureFile = profilePictures[index];
                if (profilePictureFile) {
                  const fileReader = new FileReader();
                  fileReader.onload = (e) => {
                    this.allStudentsData.push(
                      {
                        student: student,
                        profilePictureUrl: e.target?.result
                      }
                    )
                  };
                  fileReader.readAsDataURL(
                    new Blob(
                      [profilePictureFile],
                      { type: 'image/' + student.profilePicturePath.split('.').pop()?.toLocaleLowerCase() }
                    )
                  );
                }
              });
            }
          );
        }
      );
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
