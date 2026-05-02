import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeacherRegistrationComponent } from './teacher-registration.component';

describe('TeacherRegistrationComponent', () => {
  let component: TeacherRegistrationComponent;
  let fixture: ComponentFixture<TeacherRegistrationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TeacherRegistrationComponent]
    });
    fixture = TestBed.createComponent(TeacherRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
