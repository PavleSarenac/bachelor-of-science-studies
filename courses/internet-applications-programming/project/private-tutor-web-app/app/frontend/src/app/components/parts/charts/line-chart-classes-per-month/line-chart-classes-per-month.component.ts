import { Component, OnInit } from '@angular/core';
import { Chart } from "chart.js/auto"
import { AdminService } from 'src/app/services/admin/admin.service';

@Component({
  selector: 'app-line-chart-classes-per-month',
  templateUrl: './line-chart-classes-per-month.component.html',
  styleUrls: ['./line-chart-classes-per-month.component.css']
})
export class LineChartClassesPerMonthComponent implements OnInit {
  chart: any
  filledDatasets: any[] = []

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.adminService.getMostWantedTeachers().subscribe(
      (teacherObjects: any[]) => {
        teacherObjects.forEach(
          (teacher: any) => {
            this.filledDatasets.push(
              {
                label: teacher.teacher,
                data: teacher.monthsCountArray,
                fill: false,
                borderColor: this.getRandomColor(),
                tension: 0.1
              }
            )
          }
        )
        this.createLineChart()
      }
    )
  }

  getRandomColor = () => `rgb(${Math.random() * 255}, ${Math.random() * 255}, ${Math.random() * 255})`;

  createLineChart() {
    this.chart = new Chart(
      "line-chart-classes-per-month",
      {
        type: "line",
        data: {
          labels: ["January", "February", "March", "April", "June", "July",
            "August", "September", "October", "November", "December"],
          datasets: this.filledDatasets
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          aspectRatio: 1,
          scales: {
            y: {
              beginAtZero: true,
              title: {
                display: true,
                text: "Number of classes (2023)"
              }
            }
          }
        }
      }
    )
  }
}
