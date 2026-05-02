import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BarChartTeachersPerSubjectComponent } from './bar-chart-teachers-per-subject.component';

describe('BarChartTeachersPerSubjectComponent', () => {
  let component: BarChartTeachersPerSubjectComponent;
  let fixture: ComponentFixture<BarChartTeachersPerSubjectComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BarChartTeachersPerSubjectComponent]
    });
    fixture = TestBed.createComponent(BarChartTeachersPerSubjectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
