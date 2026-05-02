import express from "express"
import { UserModel } from "../models/user.model"
import ClassModel from "../models/class.model"

const NUMBER_OF_MINUTES_IN_ONE_HOUR = 60
const NUMBER_OF_SECONDS_IN_ONE_MINUTE = 60
const NUMBER_OF_MILLISECONDS_IN_ONE_SECOND = 1000

const NUMBER_OF_MILLISECONDS_IN_ONE_HOUR =
    NUMBER_OF_MINUTES_IN_ONE_HOUR * NUMBER_OF_SECONDS_IN_ONE_MINUTE * NUMBER_OF_MILLISECONDS_IN_ONE_SECOND

export class StudentController {
    updateStudentInfo = (request: express.Request, response: express.Response) => {
        let student: any = request.body
        UserModel.updateOne(
            {
                username: student.username
            },
            {
                name: student.name,
                surname: student.surname,
                address: student.address,
                email: student.email,
                phone: student.phone,
                schoolType: student.schoolType,
                currentGrade: student.currentGrade,
                profilePicturePath: student.profilePicturePath
            }
        ).then(
            () => response.json({ content: "ok" })
        ).catch((error) => console.log(error))
    }

    getAllStudents = (request: express.Request, response: express.Response) => {
        UserModel.find({ userType: "student" })
            .then((students) => response.json(students))
            .catch((error) => console.log(error))
    }

    scheduleClass = (request: express.Request, response: express.Response) => {
        let newClass = request.body
        let newId = 1
        ClassModel.find({}).sort({ id: "descending" }).limit(1).then(
            (classes: any[]) => {
                if (classes.length > 0) {
                    newId = classes[0].id + 1
                }
                newClass.id = newId
                new ClassModel(newClass).save().then(
                    () => response.json({ content: "ok" })
                ).catch((error) => console.log(error))
            }
        ).catch((error) => console.log(error))
    }

    getClassesForCalendar = (request: express.Request, response: express.Response) => {
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
                        isClassRejected: false,
                        isClassCancelled: false,
                        isClassDone: false,
                        didClassRequestExpire: false
                    }
                ).then(
                    (classes: any[]) => {
                        response.json(classes)
                    }
                ).catch((error) => console.log(error))
            }
        ).catch((error) => console.log(error))
    }

    isTimeSlotTaken = (request: express.Request, response: express.Response) => {
        let classRequest = request.body
        let newStartDate = classRequest.startDate
        let newEndDate = classRequest.endDate
        let newStartTime = classRequest.startTime
        let newEndTime = classRequest.endTime

        if (newEndTime == "00:00") {
            newEndTime = "24:00"
            newEndDate = newStartDate
        }

        ClassModel.find(
            {
                $or: [
                    { studentUsername: classRequest.studentUsername },
                    { teacherUsername: classRequest.teacherUsername }
                ],
                isClassRejected: false,
                isClassCancelled: false,
                isClassDone: false,
                didClassRequestExpire: false
            }
        ).then(
            (classes: any[]) => {
                let responseClasses: any[] = []
                classes.forEach(
                    (classObject: any) => {
                        let existingStartDate = classObject.startDate
                        let existingEndDate = classObject.endDate
                        let existingStartTime = classObject.startTime
                        let existingEndTime = classObject.endTime

                        if (existingEndTime == "00:00") {
                            existingEndDate = existingStartDate
                            existingEndTime = "24:00"
                        }

                        if (existingStartDate == newStartDate) {
                            if (existingStartDate == existingEndDate) {
                                if (newStartDate == newEndDate) {
                                    let condition1: boolean = newEndTime > existingStartTime && newEndTime < existingEndTime
                                    let condition2: boolean = newStartTime > existingStartTime && newStartTime < existingEndTime
                                    let condition3: boolean = newStartTime == existingStartTime && newEndTime == existingEndTime
                                    if (condition1 || condition2 || condition3) responseClasses.push(classObject)
                                } else {
                                    if (newStartTime >= existingStartTime && newStartTime < existingEndTime)
                                        responseClasses.push(classObject)
                                }
                            } else {
                                if (newStartDate == newEndDate) {
                                    if (existingStartTime >= newStartTime && existingStartTime < newEndTime)
                                        responseClasses.push(classObject)
                                } else {
                                    responseClasses.push(classObject)
                                }
                            }
                        } else if (existingEndDate == newStartDate) {
                            if (newStartTime < existingEndTime) responseClasses.push(classObject)
                        } else if (existingStartDate == newEndDate) {
                            if (existingStartTime < newEndTime) responseClasses.push(classObject)
                        }
                    }
                )
                response.json(responseClasses)
            }
        ).catch((error) => console.log(error))
    }

    getAllUpcomingClasses = (request: express.Request, response: express.Response) => {
        let studentUsername = request.query.studentUsername

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
                        studentUsername: studentUsername,
                        isClassAccepted: true,
                        isClassRejected: false,
                        isClassCancelled: false,
                        isClassDone: false
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
                                userType: "teacher"
                            }
                        ).then(
                            (teachers: any[]) => {
                                let responseClasses: any[] = []
                                classes.forEach(
                                    (classRequest: any) => {
                                        let teacher = teachers.find((teacher) => teacher.username == classRequest.teacherUsername)
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

                                                teacherName: teacher.name,
                                                teacherSurname: teacher.surname
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
        )
    }

    getAllPastClasses = (request: express.Request, response: express.Response) => {
        let studentUsername = request.query.studentUsername

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
                        studentUsername: studentUsername,
                        isClassAccepted: false,
                        isClassRejected: false,
                        isClassCancelled: false,
                        isClassDone: true
                    }
                ).sort(
                    {
                        startDate: "descending",
                        startTime: "descending"
                    }
                ).then(
                    (classes: any[]) => {
                        UserModel.find(
                            {
                                userType: "teacher"
                            }
                        ).then(
                            (teachers: any[]) => {
                                let responseClasses: any[] = []
                                classes.forEach(
                                    (classRequest: any) => {
                                        let teacher = teachers.find((teacher) => teacher.username == classRequest.teacherUsername)
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

                                                teacherName: teacher.name,
                                                teacherSurname: teacher.surname
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
        )
    }

    getRelevantClassesForNotifications = (request: express.Request, response: express.Response) => {
        let studentUsername = request.query.studentUsername
        ClassModel.find(
            {
                studentUsername: studentUsername,
                isNotificationRead: false,
                $or: [
                    {
                        isClassAccepted: true
                    },
                    {
                        isClassRejected: true
                    },
                    {
                        isClassCancelled: true
                    },
                    {
                        isClassDone: true
                    }
                ]
            }
        ).sort(
            {
                decisionDate: "descending",
                decisionTime: "descending",

            }
        ).then(
            (unreadNotificationClasses: any[]) => {
                ClassModel.find(
                    {
                        studentUsername: studentUsername,
                        isNotificationRead: true,
                        $or: [
                            {
                                isClassAccepted: true
                            },
                            {
                                isClassRejected: true
                            },
                            {
                                isClassCancelled: true
                            },
                            {
                                isClassDone: true
                            }
                        ]
                    }
                ).sort(
                    {
                        decisionDate: "descending",
                        decisionTime: "descending"
                    }
                ).then(
                    (readNotificationClasses: any[]) => {
                        let allClasses: any[] = unreadNotificationClasses.concat(readNotificationClasses)
                        UserModel.find(
                            {
                                userType: "teacher"
                            }
                        ).then(
                            (teachers: any[]) => {
                                let responseClasses: any[] = []
                                allClasses.forEach(
                                    (classRequest: any) => {
                                        let teacher = teachers.find((teacher) => teacher.username == classRequest.teacherUsername)
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

                                                teacherName: teacher.name,
                                                teacherSurname: teacher.surname
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
        )
    }

    readNotification = (request: express.Request, response: express.Response) => {
        let classId = request.query.classId
        ClassModel.updateOne(
            {
                id: classId
            },
            {
                isNotificationRead: true
            }
        ).then(
            () => response.json({ content: "ok" })
        ).catch((error) => console.log(error))
    }

    rateTeacher = (request: express.Request, response: express.Response) => {
        let classObject: any = request.body
        ClassModel.updateOne(
            {
                id: classObject.id
            },
            {
                studentToTeacherComment: classObject.studentToTeacherComment,
                studentToTeacherGrade: classObject.studentToTeacherGrade
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
}