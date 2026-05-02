import express from "express"
import * as fileSystem from "fs"
import { UserModel } from "../models/user.model"
import DataModel from "../models/data.model"
import ClassModel from "../models/class.model"

const NUMBER_OF_MINUTES_IN_ONE_HOUR = 60
const NUMBER_OF_SECONDS_IN_ONE_MINUTE = 60
const NUMBER_OF_MILLISECONDS_IN_ONE_SECOND = 1000

const NUMBER_OF_MILLISECONDS_IN_ONE_HOUR =
    NUMBER_OF_MINUTES_IN_ONE_HOUR * NUMBER_OF_SECONDS_IN_ONE_MINUTE * NUMBER_OF_MILLISECONDS_IN_ONE_SECOND

export class AdminController {
    downloadPdf = (request: express.Request, response: express.Response) => {
        const teacher = request.body
        response.setHeader("Content-Type", "application/pdf")
        response.setHeader("Content-Disposition", `attachment; filename=${teacher.username}_CV.pdf`)
        const fileStream = fileSystem.createReadStream(teacher.cvPath)
        fileStream.pipe(response)
    }

    approveTeacherRegistration = (request: express.Request, response: express.Response) => {
        let teacher = request.body
        UserModel.updateOne(
            { username: teacher.username },
            {
                isAccountActive: true,
                isAccountPending: false,
                isAccountBanned: false
            }
        ).then(
            () => {
                DataModel.find({}).then(
                    (data: any[]) => {
                        teacher.teacherSubjects.forEach(
                            (subject: any) => {
                                if (!data[0].subjects.includes(subject)) {
                                    data[0].subjects.push(subject)
                                }
                            }
                        )
                        DataModel.deleteMany({}).then(
                            () => {
                                DataModel.insertMany(data).then(
                                    () => response.json({ content: "ok" })
                                ).catch((error) => console.log(error))
                            }
                        ).catch((error) => console.log(error))
                    }
                ).catch((error) => console.log(error))
            }
        ).catch((error) => console.log(error))
    }

    banTeacherAccount = (request: express.Request, response: express.Response) => {
        let teacherUsername = request.query.teacherUsername
        UserModel.updateOne(
            { username: teacherUsername },
            {
                isAccountActive: false,
                isAccountPending: false,
                isAccountBanned: true
            }
        ).then(
            () => response.json({ content: "ok" })
        ).catch((error) => console.log(error))
    }

    updateSubjects = (request: express.Request, response: express.Response) => {
        let newSubjectsArray = request.body
        DataModel.updateOne(
            {},
            {
                subjects: newSubjectsArray
            }
        ).then(
            () => response.json({ content: "ok" })
        ).catch((error) => console.log(error))
    }

    getAllClassesWithRatedStudent = (request: express.Request, response: express.Response) => {
        ClassModel.find(
            {
                isClassDone: true,
                teacherToStudentGrade: { $ne: 0 }
            }
        ).then(
            (classes) => response.json(classes)
        ).catch((error) => console.log(error))
    }

    getAllClassesWithRatedTeacher = (request: express.Request, response: express.Response) => {
        ClassModel.find(
            {
                isClassDone: true,
                studentToTeacherGrade: { $ne: 0 }
            }
        ).then(
            (classes) => response.json(classes)
        ).catch((error) => console.log(error))
    }

    getAllClasses = (request: express.Request, response: express.Response) => {
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
                ClassModel.find({ didClassRequestExpire: false }).then(
                    (classes: any[]) => response.json(classes)
                ).catch((error) => console.log(error))
            }
        ).catch((error) => console.log(error))
    }

    getAverageClassesPerDay = (request: express.Request, response: express.Response) => {
        let daysCounters: number[] = [0, 0, 0, 0, 0, 0, 0]
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
                        isClassDone: true,
                        $and: [
                            {
                                startDate: { $lte: "2023-12-31" }
                            },
                            {
                                startDate: { $gte: "2023-01-01" }
                            }
                        ]
                    }
                ).then(
                    (classes: any[]) => {
                        classes.forEach(
                            (currentClass: any) => {
                                let day = new Date(currentClass.startDate).getDay()
                                if (day == 0) day = 6  // sunday
                                else day--  // monday-friday
                                daysCounters[day]++
                            }
                        )
                        response.json({
                            Monday: daysCounters[0],
                            Tuesday: daysCounters[1],
                            Wednesday: daysCounters[2],
                            Thursday: daysCounters[3],
                            Friday: daysCounters[4],
                            Saturday: daysCounters[5],
                            Sunday: daysCounters[6],
                        })
                    }
                ).catch((error) => console.log(error))
            }
        ).catch((error) => console.log(error))
    }

    getMostWantedTeachers = (request: express.Request, response: express.Response) => {
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
            async () => {
                const topTenTeachers: any = await ClassModel.aggregate([
                    {
                        $match: {
                            startDate: { $gte: '2023-01-01', $lte: '2023-12-31' },
                            isClassDone: true
                        }
                    },
                    {
                        $group: {
                            _id: '$teacherUsername',
                            classesCount: { $sum: 1 }
                        }
                    },
                    {
                        $sort: {
                            classesCount: -1
                        }
                    },
                    {
                        $limit: 10
                    },
                    {
                        $project: {
                            _id: 0,
                            teacherUsername: '$_id',
                            classesCount: 1
                        }
                    }
                ]);
                ClassModel.find(
                    {
                        isClassDone: true,
                        $and: [
                            {
                                startDate: { $lte: "2023-12-31" }
                            },
                            {
                                startDate: { $gte: "2023-01-01" }
                            }
                        ]
                    }
                ).then(
                    (classes: any[]) => {
                        UserModel.find({ userType: "teacher" }).then(
                            (teachers: any[]) => {
                                let responseArray: any[] = []

                                topTenTeachers.forEach(
                                    (teacherObject: any) => {
                                        let monthsCountArray = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
                                        classes.forEach(
                                            (currentClass: any) => {
                                                if (currentClass.teacherUsername == teacherObject.teacherUsername) {
                                                    monthsCountArray[new Date(currentClass.startDate).getMonth()]++
                                                }
                                            }
                                        )

                                        teachers.forEach(
                                            (teacher: any) => {
                                                if (teacher.username == teacherObject.teacherUsername) {
                                                    responseArray.push(
                                                        {
                                                            teacher: teacher.name + " " + teacher.surname,
                                                            monthsCountArray: monthsCountArray
                                                        }
                                                    )
                                                }
                                            }
                                        )
                                    }
                                )
                                response.json(responseArray)
                            }
                        ).catch((error) => console.log(error))
                    }
                ).catch((error) => console.log(error))
            }
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