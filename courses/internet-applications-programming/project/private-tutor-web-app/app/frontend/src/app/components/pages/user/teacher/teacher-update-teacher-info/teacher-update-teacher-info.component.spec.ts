import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeacherUpdateTeacherInfoComponent } from './teacher-update-teacher-info.component';

describe('TeacherUpdateTeacherInfoComponent', () => {
  let component: TeacherUpdateTeacherInfoComponent;
  let fixture: ComponentFixture<TeacherUpdateTeacherInfoComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TeacherUpdateTeacherInfoComponent]
    });
    fixture = TestBed.createComponent(TeacherUpdateTeacherInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
