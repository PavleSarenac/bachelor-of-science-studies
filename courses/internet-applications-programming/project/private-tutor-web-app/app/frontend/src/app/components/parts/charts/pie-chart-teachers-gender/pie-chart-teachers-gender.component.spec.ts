import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PieChartTeachersGenderComponent } from './pie-chart-teachers-gender.component';

describe('PieChartTeachersGenderComponent', () => {
  let component: PieChartTeachersGenderComponent;
  let fixture: ComponentFixture<PieChartTeachersGenderComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PieChartTeachersGenderComponent]
    });
    fixture = TestBed.createComponent(PieChartTeachersGenderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
