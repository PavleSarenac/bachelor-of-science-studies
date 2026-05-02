import express from 'express';
import { DefaultController } from '../controllers/default.controller';

const defaultRouter = express.Router()

defaultRouter.route("/login").post(
    (request, response) => new DefaultController().login(request, response)
)

defaultRouter.route("/uploadProfilePicture").post(
    (request, response) => new DefaultController().uploadProfilePicture(request, response)
)

defaultRouter.route("/register").post(
    (request, response) => new DefaultController().register(request, response)
)

defaultRouter.route("/getNumberOfTeachers").get(
    (request, response) => new DefaultController().getNumberOfTeachers(request, response)
)

defaultRouter.route("/getNumberOfStudents").get(
    (request, response) => new DefaultController().getNumberOfStudents(request, response)
)

defaultRouter.route("/getNumberOfDoneClassesLastWeek").get(
    (request, response) => new DefaultController().getNumberOfDoneClassesLastWeek(request, response)
)

defaultRouter.route("/getNumberOfDoneClassesLastMonth").get(
    (request, response) => new DefaultController().getNumberOfDoneClassesLastMonth(request, response)
)

defaultRouter.route("/checkOldPassword").post(
    (request, response) => new DefaultController().checkOldPassword(request, response)
)

defaultRouter.route("/changePassword").post(
    (request, response) => new DefaultController().changePassword(request, response)
)

defaultRouter.route("/getUser").get(
    (request, response) => new DefaultController().getUser(request, response)
)

defaultRouter.route("/getData").get(
    (request, response) => new DefaultController().getData(request, response)
)

defaultRouter.route("/deleteProfilePicture").post(
    (request, response) => new DefaultController().deleteProfilePicture(request, response)
)

defaultRouter.route("/getProfilePicture").post(
    (request, response) => new DefaultController().getProfilePicture(request, response)
)

defaultRouter.route("/checkIfUserWithEmailExists").get(
    (request, response) => new DefaultController().checkIfUserWithEmailExists(request, response)
)

export default defaultRouter;