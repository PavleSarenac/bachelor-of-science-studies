import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PieChartStudentsGenderComponent } from './pie-chart-students-gender.component';

describe('PieChartStudentsGenderComponent', () => {
  let component: PieChartStudentsGenderComponent;
  let fixture: ComponentFixture<PieChartStudentsGenderComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PieChartStudentsGenderComponent]
    });
    fixture = TestBed.createComponent(PieChartStudentsGenderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
