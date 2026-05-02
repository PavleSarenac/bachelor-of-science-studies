import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { IndexComponent } from './components/pages/guest/index/index.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { PublicLoginComponent } from './components/pages/guest/public-login/public-login.component';
import { PrivateLoginComponent } from './components/pages/guest/private-login/private-login.component';
import { FooterComponent } from './components/parts/footer/footer.component';
import { GuestHeaderComponent } from './components/parts/headers/guest-header/guest-header.component';
import { LoginFormComponent } from './components/parts/forms/login-form/login-form.component';
import { TeacherRegistrationComponent } from './components/pages/guest/teacher-registration/teacher-registration.component';
import { StudentRegistrationComponent } from './components/pages/guest/student-registration/student-registration.component';
import { RegistrationSharedFormComponent } from './components/parts/forms/registration-shared-form/registration-shared-form.component';
import { AdminIndexComponent } from './components/pages/user/admin/admin-index/admin-index.component';
import { StudentIndexComponent } from './components/pages/user/student/student-index/student-index.component';
import { TeacherIndexComponent } from './components/pages/user/teacher/teacher-index/teacher-index.component';
import { ChangePasswordComponent } from './components/pages/guest/change-password/change-password.component';
import { StudentHeaderComponent } from './components/parts/headers/student-header/student-header.component';
import { UpdateStudentInfoComponent } from './components/pages/user/student/update-student-info/update-student-info.component';
import { AdminHeaderComponent } from './components/parts/headers/admin-header/admin-header.component';
import { AdminAllStudentsComponent } from './components/pages/user/admin/admin-all-students/admin-all-students.component';
import { AdminAllTeachersComponent } from './components/pages/user/admin/admin-all-teachers/admin-all-teachers.component';
import { AdminPendingTeachersComponent } from './components/pages/user/admin/admin-pending-teachers/admin-pending-teachers.component';
import { AdminUpdateTeacherInfoComponent } from './components/pages/user/admin/admin-update-teacher-info/admin-update-teacher-info.component';
import { AdminAddSubjectsComponent } from './components/pages/user/admin/admin-add-subjects/admin-add-subjects.component';
import { TeacherHeaderComponent } from './components/parts/headers/teacher-header/teacher-header.component';
import { TeacherUpdateTeacherInfoComponent } from './components/pages/user/teacher/teacher-update-teacher-info/teacher-update-teacher-info.component';
import { StudentTeachersPreviewComponent } from './components/pages/user/student/student-teachers-preview/student-teachers-preview.component';
import { StudentSeeTeacherDetailsComponent } from './components/pages/user/student/student-see-teacher-details/student-see-teacher-details.component';
import { BarChartTeachersPerSubjectComponent } from './components/parts/charts/bar-chart-teachers-per-subject/bar-chart-teachers-per-subject.component';
import { PieChartTeachersGenderComponent } from './components/parts/charts/pie-chart-teachers-gender/pie-chart-teachers-gender.component';
import { PieChartStudentsGenderComponent } from './components/parts/charts/pie-chart-students-gender/pie-chart-students-gender.component';
import { TeacherClassesComponent } from './components/pages/user/teacher/teacher-classes/teacher-classes.component';
import { StudentClassesComponent } from './components/pages/user/student/student-classes/student-classes.component';
import { StudentNotificationsComponent } from './components/pages/user/student/student-notifications/student-notifications.component';
import { TeacherMyStudentsComponent } from './components/pages/user/teacher/teacher-my-students/teacher-my-students.component';
import { StarRatingViewComponent } from './components/parts/star-rating-view/star-rating-view.component';
import { HistogramChartClassesPerDayComponent } from './components/parts/charts/histogram-chart-classes-per-day/histogram-chart-classes-per-day.component';
import { LineChartClassesPerMonthComponent } from './components/parts/charts/line-chart-classes-per-month/line-chart-classes-per-month.component';
import { JitsiComponent } from './components/parts/jitsi/jitsi.component';
import { StudentScheduleClassCalendarComponent } from './components/pages/user/student/student-schedule-class-calendar/student-schedule-class-calendar.component';
import { FullCalendarModule } from '@fullcalendar/angular';
import { DoughnutChartClassesStatusDistributionComponent } from './components/parts/charts/doughnut-chart-classes-status-distribution/doughnut-chart-classes-status-distribution.component';
import { PolarAreaChartTeacherRatingsComponent } from './components/parts/charts/polar-area-chart-teacher-ratings/polar-area-chart-teacher-ratings.component';
import { PolarAreaChartStudentRatingsComponent } from './components/parts/charts/polar-area-chart-student-ratings/polar-area-chart-student-ratings.component';

@NgModule({
  declarations: [
    AppComponent,
    IndexComponent,
    PublicLoginComponent,
    PrivateLoginComponent,
    FooterComponent,
    GuestHeaderComponent,
    LoginFormComponent,
    TeacherRegistrationComponent,
    StudentRegistrationComponent,
    RegistrationSharedFormComponent,
    AdminIndexComponent,
    StudentIndexComponent,
    TeacherIndexComponent,
    ChangePasswordComponent,
    StudentHeaderComponent,
    UpdateStudentInfoComponent,
    AdminHeaderComponent,
    AdminAllStudentsComponent,
    AdminAllTeachersComponent,
    AdminPendingTeachersComponent,
    AdminUpdateTeacherInfoComponent,
    AdminAddSubjectsComponent,
    TeacherHeaderComponent,
    TeacherUpdateTeacherInfoComponent,
    StudentTeachersPreviewComponent,
    StudentSeeTeacherDetailsComponent,
    BarChartTeachersPerSubjectComponent,
    PieChartTeachersGenderComponent,
    PieChartStudentsGenderComponent,
    TeacherClassesComponent,
    StudentClassesComponent,
    StudentNotificationsComponent,
    TeacherMyStudentsComponent,
    StarRatingViewComponent,
    HistogramChartClassesPerDayComponent,
    LineChartClassesPerMonthComponent,
    JitsiComponent,
    StudentScheduleClassCalendarComponent,
    DoughnutChartClassesStatusDistributionComponent,
    PolarAreaChartTeacherRatingsComponent,
    PolarAreaChartStudentRatingsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    FullCalendarModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
