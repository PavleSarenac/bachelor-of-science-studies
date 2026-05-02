import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LineChartClassesPerMonthComponent } from './line-chart-classes-per-month.component';

describe('LineChartClassesPerMonthComponent', () => {
  let component: LineChartClassesPerMonthComponent;
  let fixture: ComponentFixture<LineChartClassesPerMonthComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LineChartClassesPerMonthComponent]
    });
    fixture = TestBed.createComponent(LineChartClassesPerMonthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
