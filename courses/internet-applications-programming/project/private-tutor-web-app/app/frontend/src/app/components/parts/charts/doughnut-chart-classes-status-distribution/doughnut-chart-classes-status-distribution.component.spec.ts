import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DoughnutChartClassesStatusDistributionComponent } from './doughnut-chart-classes-status-distribution.component';

describe('DoughnutChartClassesStatusDistributionComponent', () => {
  let component: DoughnutChartClassesStatusDistributionComponent;
  let fixture: ComponentFixture<DoughnutChartClassesStatusDistributionComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DoughnutChartClassesStatusDistributionComponent]
    });
    fixture = TestBed.createComponent(DoughnutChartClassesStatusDistributionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
