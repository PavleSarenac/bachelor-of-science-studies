import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentTeachersPreviewComponent } from './student-teachers-preview.component';

describe('StudentTeachersPreviewComponent', () => {
  let component: StudentTeachersPreviewComponent;
  let fixture: ComponentFixture<StudentTeachersPreviewComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StudentTeachersPreviewComponent]
    });
    fixture = TestBed.createComponent(StudentTeachersPreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
