export class User {
  username: string = ""
  password: string = ""
  userType: string = ""
  securityQuestion: string = ""
  securityAnswer: string = ""
  name: string = ""
  surname: string = ""
  gender: string = ""
  address: string = ""
  phone: string = ""
  email: string = ""
  profilePicturePath: string = ""
  schoolType: string = ""
  currentGrade: string = ""
  teacherSubjects: string[] = []
  teacherPreferredStudentsAge: string[] = []
  teacherWhereDidYouHearAboutUs: string = ""
  cvPath: string = ""
  isAccountActive: boolean = true
  isAccountPending: boolean = false
  isAccountBanned: boolean = false
  workingDays: string[] = []
  workingHours: string = ""

  teacherAverageGrade: number = 0
}
