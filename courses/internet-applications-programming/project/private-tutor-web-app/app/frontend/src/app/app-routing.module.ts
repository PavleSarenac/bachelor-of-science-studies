import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IndexComponent } from './components/pages/guest/index/index.component';
import { PublicLoginComponent } from './components/pages/guest/public-login/public-login.component';
import { PrivateLoginComponent } from './components/pages/guest/private-login/private-login.component';
import { isStudentGuard } from './guards/is-student/is-student.guard';
import { isTeacherGuard } from './guards/is-teacher/is-teacher.guard';
import { isAdminGuard } from './guards/is-admin/is-admin.guard';
import { isGuestGuard } from './guards/is-guest/is-guest.guard';
import { TeacherRegistrationComponent } from './components/pages/guest/teacher-registration/teacher-registration.component';
import { StudentRegistrationComponent } from './components/pages/guest/student-registration/student-registration.component';
import { StudentIndexComponent } from './components/pages/user/student/student-index/student-index.component';
import { TeacherIndexComponent } from './components/pages/user/teacher/teacher-index/teacher-index.component';
import { AdminIndexComponent } from './components/pages/user/admin/admin-index/admin-index.component';
import { ChangePasswordComponent } from './components/pages/guest/change-password/change-password.component';
import { UpdateStudentInfoComponent } from './components/pages/user/student/update-student-info/update-student-info.component';
import { AdminAllStudentsComponent } from './components/pages/user/admin/admin-all-students/admin-all-students.component';
import { AdminAllTeachersComponent } from './components/pages/user/admin/admin-all-teachers/admin-all-teachers.component';
import { AdminPendingTeachersComponent } from './components/pages/user/admin/admin-pending-teachers/admin-pending-teachers.component';
import { AdminUpdateTeacherInfoComponent } from './components/pages/user/admin/admin-update-teacher-info/admin-update-teacher-info.component';
import { AdminAddSubjectsComponent } from './components/pages/user/admin/admin-add-subjects/admin-add-subjects.component';
import { TeacherUpdateTeacherInfoComponent } from './components/pages/user/teacher/teacher-update-teacher-info/teacher-update-teacher-info.component';
import { StudentTeachersPreviewComponent } from './components/pages/user/student/student-teachers-preview/student-teachers-preview.component';
import { StudentSeeTeacherDetailsComponent } from './components/pages/user/student/student-see-teacher-details/student-see-teacher-details.component';
import { TeacherClassesComponent } from './components/pages/user/teacher/teacher-classes/teacher-classes.component';
import { StudentClassesComponent } from './components/pages/user/student/student-classes/student-classes.component';
import { StudentNotificationsComponent } from './components/pages/user/student/student-notifications/student-notifications.component';
import { TeacherMyStudentsComponent } from './components/pages/user/teacher/teacher-my-students/teacher-my-students.component';
import { JitsiComponent } from './components/parts/jitsi/jitsi.component';
import { StudentScheduleClassCalendarComponent } from './components/pages/user/student/student-schedule-class-calendar/student-schedule-class-calendar.component';

const routes: Routes = [
  { path: "", component: IndexComponent, canActivate: [isGuestGuard] },
  { path: "public-login", component: PublicLoginComponent, canActivate: [isGuestGuard] },
  { path: "private-login", component: PrivateLoginComponent, canActivate: [isGuestGuard] },
  { path: "student-index", component: StudentIndexComponent, canActivate: [isStudentGuard] },
  { path: "teacher-index", component: TeacherIndexComponent, canActivate: [isTeacherGuard] },
  { path: "admin-index", component: AdminIndexComponent, canActivate: [isAdminGuard] },
  { path: "teacher-registration", component: TeacherRegistrationComponent, canActivate: [isGuestGuard] },
  { path: "student-registration", component: StudentRegistrationComponent, canActivate: [isGuestGuard] },
  { path: "change-password", component: ChangePasswordComponent, canActivate: [isGuestGuard] },
  { path: "update-student-info", component: UpdateStudentInfoComponent, canActivate: [isStudentGuard] },
  { path: "admin-all-students", component: AdminAllStudentsComponent, canActivate: [isAdminGuard] },
  { path: "admin-all-teachers", component: AdminAllTeachersComponent, canActivate: [isAdminGuard] },
  { path: "admin-pending-teachers", component: AdminPendingTeachersComponent, canActivate: [isAdminGuard] },
  { path: "admin-update-teacher-info", component: AdminUpdateTeacherInfoComponent, canActivate: [isAdminGuard] },
  { path: "admin-add-subjects", component: AdminAddSubjectsComponent, canActivate: [isAdminGuard] },
  { path: "teacher-update-teacher-info", component: TeacherUpdateTeacherInfoComponent, canActivate: [isTeacherGuard] },
  { path: "student-teachers-preview", component: StudentTeachersPreviewComponent, canActivate: [isStudentGuard] },
  { path: "student-see-teacher-details", component: StudentSeeTeacherDetailsComponent, canActivate: [isStudentGuard] },
  { path: "teacher-classes", component: TeacherClassesComponent, canActivate: [isTeacherGuard] },
  { path: "student-classes", component: StudentClassesComponent, canActivate: [isStudentGuard] },
  { path: "student-notifications", component: StudentNotificationsComponent, canActivate: [isStudentGuard] },
  { path: "teacher-my-students", component: TeacherMyStudentsComponent, canActivate: [isTeacherGuard] },
  { path: "jitsi", component: JitsiComponent },
  { path: "student-schedule-class-calendar", component: StudentScheduleClassCalendarComponent, canActivate: [isStudentGuard] },
  { path: "**", redirectTo: "" }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
