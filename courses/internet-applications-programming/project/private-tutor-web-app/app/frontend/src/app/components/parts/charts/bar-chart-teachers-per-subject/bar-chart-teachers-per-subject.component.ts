import { Component, OnInit } from '@angular/core'
import { Chart } from "chart.js/auto"
import { Data } from 'src/app/models/data.model';
import { User } from 'src/app/models/user.model';
import { DefaultService } from 'src/app/services/default/default.service';
import { TeacherService } from 'src/app/services/teacher/teacher.service';

@Component({
  selector: 'app-bar-chart-teachers-per-subject',
  templateUrl: './bar-chart-teachers-per-subject.component.html',
  styleUrls: ['./bar-chart-teachers-per-subject.component.css']
})
export class BarChartTeachersPerSubjectComponent implements OnInit {
  chart: any
  yAxisValues1: string[] = []
  yAxisValues2: string[] = []
  yAxisValues3: string[] = []

  data: Data = new Data()
  allTeachers: User[] = []

  constructor(
    private defaultService: DefaultService,
    private teacherService: TeacherService
  ) { }

  ngOnInit(): void {
    this.defaultService.getData().subscribe(
      (data: Data[]) => {
        this.data = data[0]
        this.teacherService.getAllActiveTeachers().subscribe(
          (teachers: User[]) => {
            this.allTeachers = teachers
            this.data.subjects.forEach(
              (subject: string) => {
                let y1 = 0
                let y2 = 0
                let y3 = 0
                this.allTeachers.forEach(
                  (teacher: User) => {
                    teacher.teacherSubjects.forEach(
                      (teacherSubject: string) => {
                        if (teacherSubject == subject) {
                          if (teacher.teacherPreferredStudentsAge.includes(this.data.studentAges[0])) {
                            y1++
                          }
                          if (teacher.teacherPreferredStudentsAge.includes(this.data.studentAges[1])) {
                            y2++
                          }
                          if (teacher.teacherPreferredStudentsAge.includes(this.data.studentAges[2])) {
                            y3++
                          }
                        }
                      }
                    )
                  }
                )
                this.yAxisValues1.push(String(y1))
                this.yAxisValues2.push(String(y2))
                this.yAxisValues3.push(String(y3))
              }
            )
            this.createBarChart()
          }
        )
      }
    )
  }

  createBarChart() {
    this.chart = new Chart(
      "bar-chart-teachers-per-subject",
      {
        type: "bar",
        data: {
          labels: this.data.subjects,
          datasets: [
            {
              label: "Primary School (Grades: 1-4) Teachers",
              data: this.yAxisValues1,
              backgroundColor: "blue"
            },
            {
              label: "Primary School (Grades: 5-8) Teachers",
              data: this.yAxisValues2,
              backgroundColor: "limegreen"
            },
            {
              label: "Secondary School Teachers",
              data: this.yAxisValues3,
              backgroundColor: "red"
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          aspectRatio: 1
        }
      }
    )
  }
}
