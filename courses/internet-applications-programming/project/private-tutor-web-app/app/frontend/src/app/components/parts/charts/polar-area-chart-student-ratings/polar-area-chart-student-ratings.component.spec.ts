import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PolarAreaChartStudentRatingsComponent } from './polar-area-chart-student-ratings.component';

describe('PolarAreaChartStudentRatingsComponent', () => {
  let component: PolarAreaChartStudentRatingsComponent;
  let fixture: ComponentFixture<PolarAreaChartStudentRatingsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PolarAreaChartStudentRatingsComponent]
    });
    fixture = TestBed.createComponent(PolarAreaChartStudentRatingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
