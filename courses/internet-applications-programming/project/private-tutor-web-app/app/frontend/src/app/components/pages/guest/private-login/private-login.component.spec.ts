import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrivateLoginComponent } from './private-login.component';

describe('PrivateLoginComponent', () => {
  let component: PrivateLoginComponent;
  let fixture: ComponentFixture<PrivateLoginComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PrivateLoginComponent]
    });
    fixture = TestBed.createComponent(PrivateLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
