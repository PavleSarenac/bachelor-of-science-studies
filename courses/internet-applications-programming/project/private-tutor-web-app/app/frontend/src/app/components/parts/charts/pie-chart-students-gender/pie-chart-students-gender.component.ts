import { Component, OnInit } from '@angular/core'
import { Chart } from "chart.js/auto"
import { User } from 'src/app/models/user.model'
import { StudentService } from 'src/app/services/student/student.service'

@Component({
  selector: 'app-pie-chart-students-gender',
  templateUrl: './pie-chart-students-gender.component.html',
  styleUrls: ['./pie-chart-students-gender.component.css']
})
export class PieChartStudentsGenderComponent implements OnInit {
  chart: any

  numberOfMaleStudents: number = 0
  numberOfFemaleStudents: number = 0

  constructor(private studentService: StudentService) { }

  ngOnInit(): void {
    this.studentService.getAllStudents().subscribe(
      (students: User[]) => {
        students.forEach(
          (student: User) => {
            if (student.gender == "M") {
              this.numberOfMaleStudents++
            } else {
              this.numberOfFemaleStudents++
            }
          }
        )
        this.createPieChart()
      }
    )
  }

  createPieChart() {
    this.chart = new Chart(
      "pie-chart-students-gender",
      {
        type: "pie",
        data: {
          labels: ["Male Students", "Female Students"],
          datasets: [
            {
              data: [this.numberOfMaleStudents, this.numberOfFemaleStudents],
              backgroundColor: ["blue", "limegreen"],
              borderColor: "black",
              borderWidth: 1
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
