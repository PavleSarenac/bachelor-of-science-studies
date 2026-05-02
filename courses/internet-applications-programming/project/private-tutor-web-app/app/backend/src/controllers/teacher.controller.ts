import express from "express"
import { DefaultController } from "./default.controller"
import multer from "multer"
import * as fileSystem from "fs"
import { UserModel } from "../models/user.model"
import DataModel from "../models/data.model"
import ClassModel from "../models/class.model"

const NUMBER_OF_BYTES_IN_ONE_KILOBYTE = 1024
const NUMBER_OF_KILOBYTES_IN_ONE_MEGABYTE = 1024

const NUMBER_OF_MINUTES_IN_ONE_HOUR = 60
const NUMBER_OF_SECONDS_IN_ONE_MINUTE = 60
const NUMBER_OF_MILLISECONDS_IN_ONE_SECOND = 1000

const NUMBER_OF_MILLISECONDS_IN_ONE_HOUR =
    NUMBER_OF_MINUTES_IN_ONE_HOUR * NUMBER_OF_SECONDS_IN_ONE_MINUTE * NUMBER_OF_MILLISECONDS_IN_ONE_SECOND
const NUMBER_OF_MILLISECONDS_IN_ONE_DAY = NUMBER_OF_MILLISECONDS_IN_ONE_HOUR * 24

export class TeacherController {
    uploadCv = (request: express.Request, response: express.Response) => {
        let currentDateTimeString: string = DefaultController.currentRegisterAttemptDateTimeString
        let extension: string = ""
        let filename: string = ""
        let filepath: string = ""
        const cvStorage = multer.diskStorage({
            destination: (request, file, callback) => {
                callback(null, "./files/cvs")
            },
            filename: (request, file, callback) => {
                extension = file.originalname.substring(file.originalname.indexOf("."))
                filename = currentDateTimeString + extension
                filepath = "./files/cvs/" + filename
                callback(null, filename)
            }
        });
        const uploadMulter = multer({ storage: cvStorage })
        uploadMulter.single("cv")(request, response, (error) => {
            if (error) {
                console.log(error)
                response.status(500).json({ content: "CV upload failed." })
            } else {
                const cvSizeInBytes = fileSystem.statSync(filepath).size
                const cvSizeInMegaBytes = cvSizeInBytes / (NUMBER_OF_BYTES_IN_ONE_KILOBYTE * NUMBER_OF_KILOBYTES_IN_ONE_MEGABYTE)
                const maxCvSizeInMegaBytes = 3
                if (cvSizeInMegaBytes > maxCvSizeInMegaBytes) {
                    fileSystem.unlinkSync(filepath)
                    fileSystem.unlinkSync(`${DefaultController.currentProfilePictureFilePath}`)
                    response.json({
                        content: `ERROR|CV is larger than 3 MB - it is ${cvSizeInMegaBytes} MB.`
                    })
                } else {
                    response.json({ content: `FILEPATH|${filepath}` })
                }
            }
        })
    }

    getAllActiveTeachers = (request: express.Request, response: express.Response) => {
        UserModel.find({ userType: "teacher", isAccountActive: true, isAccountPending: false, isAccountBanned: false })
            .then((teachers) => response.json(teachers))
            .catch((error) => console.log(error))
    }

    getAllPendingTeachers = (request: express.Request, response: express.Response) => {
        UserModel.find({ userType: "teacher", isAccountActive: false, isAccountPending: true, isAccountBanned: false })
            .then((teachers) => response.json(teachers))
            .catch((error) => console.log(error))
    }

    updateTeacherInfo = (request: express.Request, response: express.Response) => {
        let teacher = request.body
        UserModel.updateOne(
            {
                username: teacher.username
            },
            {
                name: teacher.name,
                surname: teacher.surname,
                address: teacher.address,
                phone: teacher.phone,
                email: teacher.email,
                profilePicturePath: teacher.profilePicturePath,
                teacherSubjects: teacher.teacherSubjects,
                teacherPreferredStudentsAge: teacher.teacherPreferredStudentsAge
            }
        ).then(
            () => {
                DataModel.findOne({}).then(
                    (data: any) => {
                        teacher.teacherSubjects.forEach(
                            (subject: any) => {
                                if (!data.subjects.includes(subject)) {
                                    data.subjects.push(subject)
                                }
                            }
                        )
                        DataModel.updateOne(
                            {},
                            {
                                subjects: data.subjects
                            }
                        ).then(() => response.json({ content: "ok" }))
                    }
                ).catch((error) => console.log(error))
            }
        ).catch((error) => console.log(error))
    }

    getTeachersTeachingSpecificStudentAge = (request: express.Request, response: express.Response) => {
        UserModel.find(
            {
                userType: "teacher",
                isAccountActive: true,
                isAccountPending: false,
                isAccountBanned: false,
                teacherPreferredStudentsAge: { $elemMatch: { $eq: request.query.studentAge } }
            }
        ).then(
            (teachers: any[]) => {
                ClassModel.find(
                    {
                        isClassDone: true,
                        studentToTeacherGrade: { $ne: 0 }
                    }
                ).then(
                    (classes: any[]) => {
                        let responseTeachers: any[] = []
                        teachers.forEach(
                            (teacher: any) => {
                                let numberOfRatings = 0
                                let sumOfRatings = 0
                                let averageRating = 0

                                classes.forEach(
                                    (currentClass: any) => {
                                        if (currentClass.teacherUsername == teacher.username) {
                                            numberOfRatings++
                                            sumOfRatings += currentClass.studentToTeacherGrade
                                        }
                                    }
                                )

                                if (numberOfRatings >= 3) {
                                    averageRating = sumOfRatings / numberOfRatings
                                }

                                responseTeachers.push(
                                    {
                                        username: teacher.username,
                                        password: teacher.password,
                                        userType: teacher.userType,
                                        securityQuestion: teacher.securityQuestion,
                                        securityAnswer: teacher.securityAnswer,
                                        name: teacher.name,
                                        surname: teacher.surname,
                                        gender: teacher.gender,
                                        address: teacher.address,
                                        phone: teacher.phone,
                                        email: teacher.email,
                                        profilePicturePath: teacher.profilePicturePath,
                                        schoolType: teacher.schoolType,
                                        currentGrade: teacher.currentGrade,
                                        teacherSubjects: teacher.teacherSubjects,
                                        teacherPreferredStudentsAge: teacher.teacherPreferredStudentsAge,
                                        teacherWhereDidYouHearAboutUs: teacher.teacherWhereDidYouHearAboutUs,
                                        cvPath: teacher.cvPath,
                                        isAccountActive: teacher.isAccountActive,
                                        isAccountPending: teacher.isAccountPending,
                                        isAccountBanned: teacher.isAccountBanned,
                                        workingDays: teacher.workingDays,
                                        workingHours: teacher.workingHours,

                                        teacherAverageGrade: averageRating
                                    }
                                )
                            }
                        )
                        response.json(responseTeachers)
                    }
                ).catch((error) => console.log(error))
            }
        ).catch((error) => console.log(error))
    }

    updateWorktime = (request: express.Request, response: express.Response) => {
        let teacher = request.body
        UserModel.updateOne(
            {
                username: teacher.username
            },
            {
                workingDays: teacher.workingDays,
                workingHours: teacher.workingHours
            }
        ).then(
            () => response.json({ content: "ok" })
        ).catch((error) => console.log(error))
    }

    getAllPendingClassRequests = (request: express.Request, response: express.Response) => {
        let teacherUsername = request.query.teacherUsername
        ClassModel.find(
            {
                teacherUsername: teacherUsername,
                isClassAccepted: false,
                isClassRejected: false,
                isClassCancelled: false,
                isClassDone: false,
                didClassRequestExpire: false
            }
        ).sort(
            {
                startDate: "ascending",
                startTime: "ascending"
            }
        ).then(
            (pendingClasses: any[]) => {
                UserModel.find(
                    {
                        userType: "student"
                    }
                ).then(
                    (students: any[]) => {
                        ClassModel.find(
                            {
                                isClassDone: true,
                                teacherToStudentGrade: { $ne: 0 }
                            }
                        ).then(
                            (doneClasses: any[]) => {
                                let responseClasses: any[] = []
                                pendingClasses.forEach(
                                    (classRequest: any) => {
                                        let student = students.find((student) => student.username == classRequest.studentUsername)

                                        let numberOfRatings = 0
                                        let sumOfRatings = 0
                                        let averageRating = 0

                                        doneClasses.forEach(
                                            (currentClass: any) => {
                                                if (currentClass.studentUsername == student.username) {
                                                    numberOfRatings++
                                                    sumOfRatings += currentClass.teacherToStudentGrade
                                                }
                                            }
                                        )

                                        if (numberOfRatings >= 3) {
                                            averageRating = sumOfRatings / numberOfRatings
                                        }

                                        responseClasses.push(
                                            {
                                                id: classRequest.id,
                                                studentUsername: classRequest.studentUsername,
                                                teacherUsername: classRequest.teacherUsername,
                                                subject: classRequest.subject,
                                                startDate: classRequest.startDate,
                                                endDate: classRequest.endDate,
                                                startTime: classRequest.startTime,
                                                endTime: classRequest.endTime,
                                                description: classRequest.description,
                                                isClassAccepted: classRequest.isClassAccepted,
                                                isClassRejected: classRequest.isClassRejected,
                                                isClassCancelled: classRequest.isClassCancelled,
                                                decisionDate: classRequest.decisionDate,
                                                decisionTime: classRequest.decisionTime,
                                                isNotificationRead: classRequest.isNotificationRead,
                                                isClassDone: classRequest.isClassDone,
                                                didClassRequestExpire: classRequest.didClassRequestExpire,
                                                rejectionReason: classRequest.rejectionReason,
                                                cancellationReason: classRequest.cancellationReason,
                                                studentToTeacherComment: classRequest.studentToTeacherComment,
                                                studentToTeacherGrade: classRequest.studentToTeacherGrade,
                                                teacherToStudentComment: classRequest.teacherToStudentComment,
                                                teacherToStudentGrade: classRequest.teacherToStudentGrade,

                                                studentName: student.name,
                                                studentSurname: student.surname,
                                                studentAverageGrade: averageRating
                                            }
                                        )
                                    }
                                )
                                response.json(responseClasses)
                            }
                        ).catch((error) => console.log(error))
                    }
                ).catch((error) => console.log(error))
            }
        ).catch((error) => console.log(error))
    }

    deleteExpiredClassRequests = (request: express.Request, response: express.Response) => {
        let currentDateTimeInMillis = Date.now()
        let currentDateTime = this.convertMillisToDateTimeStringWithoutSeconds(currentDateTimeInMillis + NUMBER_OF_MILLISECONDS_IN_ONE_HOUR)
        let currentDate = currentDateTime.substring(0, currentDateTime.indexOf(" "))
        let currentTime = currentDateTime.substring(currentDateTime.indexOf(" ") + 1)
        ClassModel.find({}).then(
            (classes: any[]) => {
                classes.forEach(
                    (classRequest: any) => {
                        if (!classRequest.isClassAccepted && !classRequest.isClassRejected
                            && !classRequest.isClassCancelled && !classRequest.isClassDone
                            && currentDate >= classRequest.startDate && currentTime >= classRequest.startTime) {
                            classRequest.didClassRequestExpire = true
                        }
                    }
                )
                ClassModel.deleteMany({}).then(
                    () => ClassModel.insertMany(classes).then(
                        () => response.json({ content: "ok" })
                    ).catch((error) => console.log(error))
                ).catch((error) => console.log(error))
            }
        ).catch((error) => console.log(error))
    }

    acceptClassRequest = (request: express.Request, response: express.Response) => {
        let classRequest = request.body
        let currentDateTimeString = this.convertMillisToDateTimeStringWithMilliseconds(Date.now() + NUMBER_OF_MILLISECONDS_IN_ONE_HOUR)
        let currentDate = currentDateTimeString.substring(0, currentDateTimeString.indexOf(" "))
        let currentTime = currentDateTimeString.substring(currentDateTimeString.indexOf(" ") + 1)
        ClassModel.updateOne(
            {
                id: classRequest.id
            },
            {
                isClassAccepted: true,
                decisionDate: currentDate,
                decisionTime: currentTime,
                isNotificationRead: false
            }
        ).then(
            () => response.json({ content: "ok" })
        ).catch((error) => console.log(error))
    }

    rejectClassRequest = (request: express.Request, response: express.Response) => {
        let classRequest = request.body
        let currentDateTimeString = this.convertMillisToDateTimeStringWithMilliseconds(Date.now() + NUMBER_OF_MILLISECONDS_IN_ONE_HOUR)
        let currentDate = currentDateTimeString.substring(0, currentDateTimeString.indexOf(" "))
        let currentTime = currentDateTimeString.substring(currentDateTimeString.indexOf(" ") + 1)
        ClassModel.updateOne(
            {
                id: classRequest.id
            },
            {
                isClassRejected: true,
                rejectionReason: classRequest.rejectionReason,
                decisionDate: currentDate,
                decisionTime: currentTime,
                isNotificationRead: false
            }
        ).then(
            () => response.json({ content: "ok" })
        ).catch((error) => console.log(error))
    }

    cancelClass = (request: express.Request, response: express.Response) => {
        let upcomingClass = request.body
        let currentDateTimeString = this.convertMillisToDateTimeStringWithMilliseconds(Date.now() + NUMBER_OF_MILLISECONDS_IN_ONE_HOUR)
        let currentDate = currentDateTimeString.substring(0, currentDateTimeString.indexOf(" "))
        let currentTime = currentDateTimeString.substring(currentDateTimeString.indexOf(" ") + 1)
        ClassModel.updateOne(
            {
                id: upcomingClass.id
            },
            {
                isClassAccepted: false,
                isClassCancelled: true,
                cancellationReason: upcomingClass.cancellationReason,
                decisionDate: currentDate,
                decisionTime: currentTime,
                isNotificationRead: false
            }
        ).then(
            () => response.json({ content: "ok" })
        ).catch((error) => console.log(error))
    }

    getAllAcceptedClassesForNextThreeDays = (request: express.Request, response: express.Response) => {
        let teacherUsername = request.query.teacherUsername

        let currentDateTimeInMillis = Date.now() + NUMBER_OF_MILLISECONDS_IN_ONE_HOUR
        let finalDateTimeInMillis = currentDateTimeInMillis + 3 * NUMBER_OF_MILLISECONDS_IN_ONE_DAY

        let currentDateTimeString = this.convertMillisToDateTimeStringWithoutSeconds(currentDateTimeInMillis)
        let finalDateTimeString = this.convertMillisToDateTimeStringWithoutSeconds(finalDateTimeInMillis)

        let currentDateString = currentDateTimeString.substring(0, currentDateTimeString.indexOf(" "))
        let currentTimeString = currentDateTimeString.substring(currentDateTimeString.indexOf(" ") + 1)
        let finalDateString = finalDateTimeString.substring(0, finalDateTimeString.indexOf(" "))

        ClassModel.updateMany(
            {
                isClassAccepted: true,
                isClassRejected: false,
                isClassCancelled: false,
                isClassDone: false,
                $or: [
                    {
                        endDate: { $lt: currentDateString }
                    },
                    {
                        $and: [
                            {
                                endDate: { $eq: currentDateString },
                            },
                            {
                                endTime: { $lt: currentTimeString }
                            }
                        ]
                    }
                ]
            },
            {
                isClassAccepted: false,
                isClassDone: true
            }
        ).then(
            () => {
                ClassModel.find(
                    {
                        teacherUsername: teacherUsername,
                        isClassAccepted: true,
                        isClassRejected: false,
                        isClassCancelled: false,
                        isClassDone: false,
                        $and: [
                            {
                                startDate: { $gte: currentDateString }
                            },
                            {
                                startDate: { $lte: finalDateString }
                            }
                        ]
                    }
                ).sort(
                    {
                        startDate: "ascending",
                        startTime: "ascending"
                    }
                ).then(
                    (classes: any[]) => {
                        UserModel.find(
                            {
                                userType: "student"
                            }
                        ).then(
                            (students: any[]) => {
                                let responseClasses: any[] = []
                                classes.forEach(
                                    (classRequest: any) => {
                                        let student = students.find((student) => student.username == classRequest.studentUsername)
                                        responseClasses.push(
                                            {
                                                id: classRequest.id,
                                                studentUsername: classRequest.studentUsername,
                                                teacherUsername: classRequest.teacherUsername,
                                                subject: classRequest.subject,
                                                startDate: classRequest.startDate,
                                                endDate: classRequest.endDate,
                                                startTime: classRequest.startTime,
                                                endTime: classRequest.endTime,
                                                description: classRequest.description,
                                                isClassAccepted: classRequest.isClassAccepted,
                                                isClassRejected: classRequest.isClassRejected,
                                                isClassCancelled: classRequest.isClassCancelled,
                                                decisionDate: classRequest.decisionDate,
                                                decisionTime: classRequest.decisionTime,
                                                isNotificationRead: classRequest.isNotificationRead,
                                                isClassDone: classRequest.isClassDone,
                                                didClassRequestExpire: classRequest.didClassRequestExpire,
                                                rejectionReason: classRequest.rejectionReason,
                                                cancellationReason: classRequest.cancellationReason,
                                                studentToTeacherComment: classRequest.studentToTeacherComment,
                                                studentToTeacherGrade: classRequest.studentToTeacherGrade,
                                                teacherToStudentComment: classRequest.teacherToStudentComment,
                                                teacherToStudentGrade: classRequest.teacherToStudentGrade,

                                                studentName: student.name,
                                                studentSurname: student.surname
                                            }
                                        )
                                    }
                                )
                                response.json(responseClasses)
                            }
                        ).catch((error) => console.log(error))
                    }
                ).catch((error) => console.log(error))
            }
        ).catch((error) => console.log(error))
    }

    getAllDoneClasses = (request: express.Request, response: express.Response) => {
        let teacherUsername = request.query.teacherUsername

        let currentDateTimeInMillis = Date.now() + NUMBER_OF_MILLISECONDS_IN_ONE_HOUR
        let currentDateTimeString = this.convertMillisToDateTimeStringWithoutSeconds(currentDateTimeInMillis)
        let currentDateString = currentDateTimeString.substring(0, currentDateTimeString.indexOf(" "))
        let currentTimeString = currentDateTimeString.substring(currentDateTimeString.indexOf(" ") + 1)

        ClassModel.updateMany(
            {
                isClassAccepted: true,
                isClassRejected: false,
                isClassCancelled: false,
                isClassDone: false,
                $or: [
                    {
                        endDate: { $lt: currentDateString }
                    },
                    {
                        $and: [
                            {
                                endDate: { $eq: currentDateString },
                            },
                            {
                                endTime: { $lt: currentTimeString }
                            }
                        ]
                    }
                ]
            },
            {
                isClassAccepted: false,
                isClassDone: true
            }
        ).then(
            () => {
                ClassModel.find(
                    {
                        teacherUsername: teacherUsername,
                        isClassDone: true
                    }
                ).sort(
                    {
                        subject: "ascending",
                        endDate: "descending",
                        endTime: "descending"
                    }
                ).then(
                    (classes: any[]) => {
                        UserModel.find(
                            {
                                userType: "student"
                            }
                        ).then(
                            (students: any[]) => {
                                let responseClasses: any[] = []
                                classes.forEach(
                                    (classRequest: any) => {
                                        let student = students.find((student) => student.username == classRequest.studentUsername)
                                        responseClasses.push(
                                            {
                                                id: classRequest.id,
                                                studentUsername: classRequest.studentUsername,
                                                teacherUsername: classRequest.teacherUsername,
                                                subject: classRequest.subject,
                                                startDate: classRequest.startDate,
                                                endDate: classRequest.endDate,
                                                startTime: classRequest.startTime,
                                                endTime: classRequest.endTime,
                                                description: classRequest.description,
                                                isClassAccepted: classRequest.isClassAccepted,
                                                isClassRejected: classRequest.isClassRejected,
                                                isClassCancelled: classRequest.isClassCancelled,
                                                decisionDate: classRequest.decisionDate,
                                                decisionTime: classRequest.decisionTime,
                                                isNotificationRead: classRequest.isNotificationRead,
                                                isClassDone: classRequest.isClassDone,
                                                didClassRequestExpire: classRequest.didClassRequestExpire,
                                                rejectionReason: classRequest.rejectionReason,
                                                cancellationReason: classRequest.cancellationReason,
                                                studentToTeacherComment: classRequest.studentToTeacherComment,
                                                studentToTeacherGrade: classRequest.studentToTeacherGrade,
                                                teacherToStudentComment: classRequest.teacherToStudentComment,
                                                teacherToStudentGrade: classRequest.teacherToStudentGrade,

                                                studentName: student.name,
                                                studentSurname: student.surname
                                            }
                                        )
                                    }
                                )
                                response.json(responseClasses)
                            }
                        ).catch((error) => console.log(error))
                    }
                ).catch((error) => console.log(error))
            }
        ).catch((error) => console.log(error))
    }

    rateStudent = (request: express.Request, response: express.Response) => {
        let classObject: any = request.body
        ClassModel.updateOne(
            {
                id: classObject.id
            },
            {
                teacherToStudentComment: classObject.teacherToStudentComment,
                teacherToStudentGrade: classObject.teacherToStudentGrade
            }
        ).then(
            () => response.json({ content: "ok" })
        ).catch((error) => console.log(error))
    }

    convertMillisToDateTimeStringWithoutSeconds(dateTimeInMillis: number): string {
        let currentDateTime = new Date(dateTimeInMillis).toISOString()
        let currentDate = currentDateTime.substring(0, currentDateTime.indexOf("T"))
        let currentTime = currentDateTime.substring(currentDateTime.indexOf("T") + 1, currentDateTime.indexOf(".") - 3)
        currentDateTime = currentDate + " " + currentTime
        return currentDateTime
    }

    convertMillisToDateTimeStringWithMilliseconds(dateTimeInMillis: number): string {
        let currentDateTime = new Date(dateTimeInMillis).toISOString()
        let currentDate = currentDateTime.substring(0, currentDateTime.indexOf("T"))
        let currentTime = currentDateTime.substring(currentDateTime.indexOf("T") + 1, currentDateTime.indexOf("Z"))
        currentDateTime = currentDate + " " + currentTime
        return currentDateTime
    }
}