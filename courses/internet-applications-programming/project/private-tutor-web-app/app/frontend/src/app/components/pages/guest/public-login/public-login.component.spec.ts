import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PublicLoginComponent } from './public-login.component';

describe('PublicLoginComponent', () => {
  let component: PublicLoginComponent;
  let fixture: ComponentFixture<PublicLoginComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PublicLoginComponent]
    });
    fixture = TestBed.createComponent(PublicLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
