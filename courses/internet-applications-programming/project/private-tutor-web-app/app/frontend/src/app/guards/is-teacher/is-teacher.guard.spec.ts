import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { isTeacherGuard } from './is-teacher.guard';

describe('isTeacherGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => isTeacherGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
