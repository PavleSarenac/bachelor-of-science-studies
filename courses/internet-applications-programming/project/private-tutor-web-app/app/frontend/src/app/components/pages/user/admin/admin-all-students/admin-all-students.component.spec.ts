import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminAllStudentsComponent } from './admin-all-students.component';

describe('AdminAllStudentsComponent', () => {
  let component: AdminAllStudentsComponent;
  let fixture: ComponentFixture<AdminAllStudentsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminAllStudentsComponent]
    });
    fixture = TestBed.createComponent(AdminAllStudentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
