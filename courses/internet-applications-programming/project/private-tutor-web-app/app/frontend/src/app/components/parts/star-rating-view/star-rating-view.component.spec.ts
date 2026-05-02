import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StarRatingViewComponent } from './star-rating-view.component';

describe('StarRatingViewComponent', () => {
  let component: StarRatingViewComponent;
  let fixture: ComponentFixture<StarRatingViewComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StarRatingViewComponent]
    });
    fixture = TestBed.createComponent(StarRatingViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
