import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user.model';
import { DefaultService } from 'src/app/services/default/default.service';
import { forkJoin, of, Subject } from 'rxjs';
import { catchError, takeUntil } from 'rxjs/operators';
import { TeacherService } from 'src/app/services/teacher/teacher.service';
import { AdminService } from 'src/app/services/admin/admin.service';

@Component({
  selector: 'app-admin-pending-teachers',
  templateUrl: './admin-pending-teachers.component.html',
  styleUrls: ['./admin-pending-teachers.component.css', '../../../../../../styles.css']
})
export class AdminPendingTeachersComponent implements OnInit {
  allTeachersData: any[] = []
  shouldLoadContent: boolean = false
  doActiveTeachersExist: boolean = false

  private ngUnsubscribe = new Subject<void>();

  constructor(
    private teacherService: TeacherService,
    private defaultService: DefaultService,
    private adminService: AdminService
  ) { }

  ngOnInit(): void {
    this.fetchTeachersData()
  }

  fetchTeachersData() {
    this.teacherService.getAllPendingTeachers()
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(
        (teachers: User[]) => {
          if (teachers.length > 0) {
            this.doActiveTeachersExist = true
          } else {
            this.doActiveTeachersExist = false
          }
          this.shouldLoadContent = true
          const requests = teachers.map((teacher: User) =>
            this.defaultService.getProfilePicture(teacher.profilePicturePath)
              .pipe(
                catchError((error) => {
                  console.error(`Error loading picture for student ${teacher.username}:`, error);
                  // Returning an observable with `null` to keep the array length consistent
                  return of(null);
                })
              )
          );

          forkJoin(requests).subscribe(
            (profilePictures: Blob[] | any) => {
              teachers.forEach((teacher, index) => {
                const profilePictureFile = profilePictures[index];
                if (profilePictureFile) {
                  const fileReader = new FileReader();
                  fileReader.onload = (e) => {
                    this.allTeachersData.push(
                      {
                        teacher: teacher,
                        profilePictureUrl: e.target?.result
                      }
                    )
                  };
                  fileReader.readAsDataURL(
                    new Blob(
                      [profilePictureFile],
                      { type: 'image/' + teacher.profilePicturePath.split('.').pop()?.toLocaleLowerCase() }
                    )
                  );
                }
              });
            }
          );
        }
      );
  }

  downloadPdf(teacher: User) {
    this.adminService.downloadPdf(teacher).subscribe(
      (data) => {
        const blob = new Blob([data], { type: "application/pdf" });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `${teacher.username}_CV.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
      }
    )
  }

  approveTeacherRegistration(teacher: User) {
    this.adminService.approveTeacherRegistration(teacher).subscribe(
      () => {
        this.shouldLoadContent = false
        this.allTeachersData = []
        this.fetchTeachersData()
      }
    )
  }

  banTeacherAccount(username: string) {
    this.adminService.banTeacherAccount(username).subscribe(
      () => {
        this.shouldLoadContent = false
        this.allTeachersData = []
        this.fetchTeachersData()
      }
    )
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
