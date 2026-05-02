import { Component } from '@angular/core';
import { AdminService } from 'src/app/services/admin/admin.service';
import { Chart } from "chart.js/auto"
import { Class } from 'src/app/models/class.model';

@Component({
  selector: 'app-polar-area-chart-student-ratings',
  templateUrl: './polar-area-chart-student-ratings.component.html',
  styleUrls: ['./polar-area-chart-student-ratings.component.css']
})
export class PolarAreaChartStudentRatingsComponent {
  chart: any
  numberOfOneStarRatings: number = 0
  numberOfTwoStarRatings: number = 0
  numberOfThreeStarRatings: number = 0
  numberOfFourStarRatings: number = 0
  numberOfFiveStarRatings: number = 0

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.adminService.getAllClassesWithRatedStudent().subscribe(
      (classes: Class[]) => {
        classes.forEach(
          (currentClass: Class) => {
            if (currentClass.teacherToStudentGrade == 1) this.numberOfOneStarRatings++
            if (currentClass.teacherToStudentGrade == 2) this.numberOfTwoStarRatings++
            if (currentClass.teacherToStudentGrade == 3) this.numberOfThreeStarRatings++
            if (currentClass.teacherToStudentGrade == 4) this.numberOfFourStarRatings++
            if (currentClass.teacherToStudentGrade == 5) this.numberOfFiveStarRatings++
          }
        )
        this.createPolarAreaChart()
      }
    )
  }

  createPolarAreaChart() {
    this.chart = new Chart(
      "polar-area-chart-student-ratings",
      {
        type: "polarArea",
        data: {
          labels: [
            "1-star student rating", "2-star student rating", "3-star student rating", "4-star student rating",
            "5-star student rating"
          ],
          datasets: [
            {
              data: [
                this.numberOfOneStarRatings, this.numberOfTwoStarRatings, this.numberOfThreeStarRatings, this.numberOfFourStarRatings, this.numberOfFiveStarRatings
              ],
              backgroundColor: ["red", "orange", "yellow", "blue", "limegreen"]
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
