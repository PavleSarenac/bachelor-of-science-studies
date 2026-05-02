import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const isStudentGuard: CanActivateFn = (route, state) => {
  const router = inject(Router)
  let user: any = localStorage.getItem("loggedInUser")
  if (user != null) {
    user = JSON.parse(user)
    if (user.userType == "student") {
      return true
    }
    router.navigate([user.userType + "-index"])
    return false
  }
  router.navigate([""])
  return false
};
