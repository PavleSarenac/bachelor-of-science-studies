import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentScheduleClassCalendarComponent } from './student-schedule-class-calendar.component';

describe('StudentScheduleClassCalendarComponent', () => {
  let component: StudentScheduleClassCalendarComponent;
  let fixture: ComponentFixture<StudentScheduleClassCalendarComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StudentScheduleClassCalendarComponent]
    });
    fixture = TestBed.createComponent(StudentScheduleClassCalendarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
