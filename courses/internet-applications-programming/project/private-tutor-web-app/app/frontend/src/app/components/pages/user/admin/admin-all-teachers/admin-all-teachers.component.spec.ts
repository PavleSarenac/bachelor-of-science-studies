import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminAllTeachersComponent } from './admin-all-teachers.component';

describe('AdminAllTeachersComponent', () => {
  let component: AdminAllTeachersComponent;
  let fixture: ComponentFixture<AdminAllTeachersComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminAllTeachersComponent]
    });
    fixture = TestBed.createComponent(AdminAllTeachersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
