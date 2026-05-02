import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPendingTeachersComponent } from './admin-pending-teachers.component';

describe('AdminPendingTeachersComponent', () => {
  let component: AdminPendingTeachersComponent;
  let fixture: ComponentFixture<AdminPendingTeachersComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminPendingTeachersComponent]
    });
    fixture = TestBed.createComponent(AdminPendingTeachersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
