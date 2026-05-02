import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentSeeTeacherDetailsComponent } from './student-see-teacher-details.component';

describe('StudentSeeTeacherDetailsComponent', () => {
  let component: StudentSeeTeacherDetailsComponent;
  let fixture: ComponentFixture<StudentSeeTeacherDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StudentSeeTeacherDetailsComponent]
    });
    fixture = TestBed.createComponent(StudentSeeTeacherDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
