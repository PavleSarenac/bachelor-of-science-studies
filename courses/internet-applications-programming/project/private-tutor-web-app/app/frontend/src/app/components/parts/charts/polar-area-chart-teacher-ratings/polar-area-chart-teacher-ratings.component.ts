import { Component } from '@angular/core';
import { AdminService } from 'src/app/services/admin/admin.service';
import { Chart } from "chart.js/auto"
import { Class } from 'src/app/models/class.model';

@Component({
  selector: 'app-polar-area-chart-teacher-ratings',
  templateUrl: './polar-area-chart-teacher-ratings.component.html',
  styleUrls: ['./polar-area-chart-teacher-ratings.component.css']
})
export class PolarAreaChartTeacherRatingsComponent {
  chart: any
  numberOfOneStarRatings: number = 0
  numberOfTwoStarRatings: number = 0
  numberOfThreeStarRatings: number = 0
  numberOfFourStarRatings: number = 0
  numberOfFiveStarRatings: number = 0

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.adminService.getAllClassesWithRatedTeacher().subscribe(
      (classes: Class[]) => {
        classes.forEach(
          (currentClass: Class) => {
            if (currentClass.studentToTeacherGrade == 1) this.numberOfOneStarRatings++
            if (currentClass.studentToTeacherGrade == 2) this.numberOfTwoStarRatings++
            if (currentClass.studentToTeacherGrade == 3) this.numberOfThreeStarRatings++
            if (currentClass.studentToTeacherGrade == 4) this.numberOfFourStarRatings++
            if (currentClass.studentToTeacherGrade == 5) this.numberOfFiveStarRatings++
          }
        )
        this.createPolarAreaChart()
      }
    )
  }

  createPolarAreaChart() {
    this.chart = new Chart(
      "polar-area-chart-teacher-ratings",
      {
        type: "polarArea",
        data: {
          labels: [
            "1-star teacher rating", "2-star teacher rating", "3-star teacher rating", "4-star teacher rating",
            "5-star teacher rating"
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
