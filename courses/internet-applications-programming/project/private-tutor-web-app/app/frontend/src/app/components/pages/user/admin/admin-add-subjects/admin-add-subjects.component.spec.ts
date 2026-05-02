import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminAddSubjectsComponent } from './admin-add-subjects.component';

describe('AdminAddSubjectsComponent', () => {
  let component: AdminAddSubjectsComponent;
  let fixture: ComponentFixture<AdminAddSubjectsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminAddSubjectsComponent]
    });
    fixture = TestBed.createComponent(AdminAddSubjectsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
