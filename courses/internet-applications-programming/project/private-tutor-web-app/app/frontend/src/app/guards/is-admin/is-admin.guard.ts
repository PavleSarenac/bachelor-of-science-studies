import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const isAdminGuard: CanActivateFn = (route, state) => {
  const router = inject(Router)
  let user: any = localStorage.getItem("loggedInUser")
  if (user != null) {
    user = JSON.parse(user)
    if (user.userType == "admin") {
      return true
    }
    router.navigate([user.userType + "-index"])
    return false
  }
  router.navigate([""])
  return false
};
