import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeacherMyStudentsComponent } from './teacher-my-students.component';

describe('TeacherMyStudentsComponent', () => {
  let component: TeacherMyStudentsComponent;
  let fixture: ComponentFixture<TeacherMyStudentsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TeacherMyStudentsComponent]
    });
    fixture = TestBed.createComponent(TeacherMyStudentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
