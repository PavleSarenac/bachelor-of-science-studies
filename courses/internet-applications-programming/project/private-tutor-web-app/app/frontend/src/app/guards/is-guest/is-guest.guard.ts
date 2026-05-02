import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const isGuestGuard: CanActivateFn = (route, state) => {
  const router = inject(Router)
  let user: any = localStorage.getItem("loggedInUser")
  if (user != null) {
    user = JSON.parse(user)
    router.navigate([user.userType + "-index"])
    return false
  }
  return true
};
