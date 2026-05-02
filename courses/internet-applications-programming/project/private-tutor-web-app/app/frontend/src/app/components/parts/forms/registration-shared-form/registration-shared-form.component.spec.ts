import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistrationSharedFormComponent } from './registration-shared-form.component';

describe('RegistrationSharedFormComponent', () => {
  let component: RegistrationSharedFormComponent;
  let fixture: ComponentFixture<RegistrationSharedFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RegistrationSharedFormComponent]
    });
    fixture = TestBed.createComponent(RegistrationSharedFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
