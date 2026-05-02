import express from 'express';
import { TeacherController } from '../controllers/teacher.controller';

const teacherRouter = express.Router()

teacherRouter.route("/uploadCv").post(
    (request, response) => new TeacherController().uploadCv(request, response)
)

teacherRouter.route("/getAllActiveTeachers").get(
    (request, response) => new TeacherController().getAllActiveTeachers(request, response)
)

teacherRouter.route("/getAllPendingTeachers").get(
    (request, response) => new TeacherController().getAllPendingTeachers(request, response)
)

teacherRouter.route("/updateTeacherInfo").post(
    (request, response) => new TeacherController().updateTeacherInfo(request, response)
)

teacherRouter.route("/getTeachersTeachingSpecificStudentAge").get(
    (request, response) => new TeacherController().getTeachersTeachingSpecificStudentAge(request, response)
)

teacherRouter.route("/updateWorktime").post(
    (request, response) => new TeacherController().updateWorktime(request, response)
)

teacherRouter.route("/getAllPendingClassRequests").get(
    (request, response) => new TeacherController().getAllPendingClassRequests(request, response)
)

teacherRouter.route("/deleteExpiredClassRequests").get(
    (request, response) => new TeacherController().deleteExpiredClassRequests(request, response)
)

teacherRouter.route("/acceptClassRequest").post(
    (request, response) => new TeacherController().acceptClassRequest(request, response)
)

teacherRouter.route("/rejectClassRequest").post(
    (request, response) => new TeacherController().rejectClassRequest(request, response)
)

teacherRouter.route("/cancelClass").post(
    (request, response) => new TeacherController().cancelClass(request, response)
)

teacherRouter.route("/getAllAcceptedClassesForNextThreeDays").get(
    (request, response) => new TeacherController().getAllAcceptedClassesForNextThreeDays(request, response)
)

teacherRouter.route("/getAllDoneClasses").get(
    (request, response) => new TeacherController().getAllDoneClasses(request, response)
)

teacherRouter.route("/rateStudent").post(
    (request, response) => new TeacherController().rateStudent(request, response)
)

export default teacherRouter