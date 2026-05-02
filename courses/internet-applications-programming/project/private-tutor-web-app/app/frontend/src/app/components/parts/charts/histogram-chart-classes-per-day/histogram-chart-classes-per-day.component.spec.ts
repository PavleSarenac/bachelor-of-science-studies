import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistogramChartClassesPerDayComponent } from './histogram-chart-classes-per-day.component';

describe('HistogramChartClassesPerDayComponent', () => {
  let component: HistogramChartClassesPerDayComponent;
  let fixture: ComponentFixture<HistogramChartClassesPerDayComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HistogramChartClassesPerDayComponent]
    });
    fixture = TestBed.createComponent(HistogramChartClassesPerDayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
