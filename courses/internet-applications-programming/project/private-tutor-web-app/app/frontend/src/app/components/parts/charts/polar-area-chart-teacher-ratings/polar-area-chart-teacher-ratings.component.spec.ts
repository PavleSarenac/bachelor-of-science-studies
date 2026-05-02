import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PolarAreaChartTeacherRatingsComponent } from './polar-area-chart-teacher-ratings.component';

describe('PolarAreaChartTeacherRatingsComponent', () => {
  let component: PolarAreaChartTeacherRatingsComponent;
  let fixture: ComponentFixture<PolarAreaChartTeacherRatingsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PolarAreaChartTeacherRatingsComponent]
    });
    fixture = TestBed.createComponent(PolarAreaChartTeacherRatingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
