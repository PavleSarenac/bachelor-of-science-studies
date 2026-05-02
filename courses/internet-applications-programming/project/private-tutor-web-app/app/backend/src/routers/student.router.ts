import express from 'express';
import { StudentController } from '../controllers/student.controller';

const studentRouter = express.Router()

studentRouter.route("/updateStudentInfo").post(
    (request, response) => new StudentController().updateStudentInfo(request, response)
)

studentRouter.route("/getAllStudents").get(
    (request, response) => new StudentController().getAllStudents(request, response)
)

studentRouter.route("/scheduleClass").post(
    (request, response) => new StudentController().scheduleClass(request, response)
)

studentRouter.route("/isTimeSlotTaken").post(
    (request, response) => new StudentController().isTimeSlotTaken(request, response)
)

studentRouter.route("/getAllUpcomingClasses").get(
    (request, response) => new StudentController().getAllUpcomingClasses(request, response)
)

studentRouter.route("/getAllPastClasses").get(
    (request, response) => new StudentController().getAllPastClasses(request, response)
)

studentRouter.route("/getRelevantClassesForNotifications").get(
    (request, response) => new StudentController().getRelevantClassesForNotifications(request, response)
)

studentRouter.route("/readNotification").get(
    (request, response) => new StudentController().readNotification(request, response)
)

studentRouter.route("/rateTeacher").post(
    (request, response) => new StudentController().rateTeacher(request, response)
)

studentRouter.route("/getClassesForCalendar").get(
    (request, response) => new StudentController().getClassesForCalendar(request, response)
)

export default studentRouter