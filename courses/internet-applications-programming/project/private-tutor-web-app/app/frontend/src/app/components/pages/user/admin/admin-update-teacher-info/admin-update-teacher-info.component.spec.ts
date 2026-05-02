import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminUpdateTeacherInfoComponent } from './admin-update-teacher-info.component';

describe('AdminUpdateTeacherInfoComponent', () => {
  let component: AdminUpdateTeacherInfoComponent;
  let fixture: ComponentFixture<AdminUpdateTeacherInfoComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminUpdateTeacherInfoComponent]
    });
    fixture = TestBed.createComponent(AdminUpdateTeacherInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
