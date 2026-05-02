import { Component } from '@angular/core';
import { Chart } from "chart.js/auto"
import { User } from 'src/app/models/user.model';
import { TeacherService } from 'src/app/services/teacher/teacher.service';

@Component({
  selector: 'app-pie-chart-teachers-gender',
  templateUrl: './pie-chart-teachers-gender.component.html',
  styleUrls: ['./pie-chart-teachers-gender.component.css']
})
export class PieChartTeachersGenderComponent {
  chart: any

  numberOfMaleTeachers: number = 0
  numberOfFemaleTeachers: number = 0

  constructor(private teacherService: TeacherService) { }

  ngOnInit(): void {
    this.teacherService.getAllActiveTeachers().subscribe(
      (teachers: User[]) => {
        teachers.forEach(
          (teacher: User) => {
            if (teacher.gender == "M") {
              this.numberOfMaleTeachers++
            } else {
              this.numberOfFemaleTeachers++
            }
          }
        )
        this.createPieChart()
      }
    )
  }

  createPieChart() {
    this.chart = new Chart(
      "pie-chart-teachers-gender",
      {
        type: "pie",
        data: {
          labels: ["Male Teachers", "Female Teachers"],
          datasets: [
            {
              data: [this.numberOfMaleTeachers, this.numberOfFemaleTeachers],
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
