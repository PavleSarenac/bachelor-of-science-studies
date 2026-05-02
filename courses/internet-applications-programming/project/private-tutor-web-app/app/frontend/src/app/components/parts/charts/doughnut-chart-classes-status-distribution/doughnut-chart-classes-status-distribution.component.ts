import { Component, OnInit } from '@angular/core';
import { Chart } from "chart.js/auto"
import { Class } from 'src/app/models/class.model';
import { AdminService } from 'src/app/services/admin/admin.service';

@Component({
  selector: 'app-doughnut-chart-classes-status-distribution',
  templateUrl: './doughnut-chart-classes-status-distribution.component.html',
  styleUrls: ['./doughnut-chart-classes-status-distribution.component.css']
})
export class DoughnutChartClassesStatusDistributionComponent implements OnInit {
  chart: any
  upcomingClassesNumber: number = 0
  doneClassesNumber: number = 0
  rejectedClassesNumber: number = 0
  cancelledClassesNumber: number = 0
  totalNumberOfClasses: number = 0

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.adminService.getAllClasses().subscribe(
      (classes: Class[]) => {
        classes.forEach(
          (currentClass: Class) => {
            this.totalNumberOfClasses++
            if (currentClass.isClassAccepted) this.upcomingClassesNumber++
            if (currentClass.isClassDone) this.doneClassesNumber++
            if (currentClass.isClassRejected) this.rejectedClassesNumber++
            if (currentClass.isClassCancelled) this.cancelledClassesNumber++
          }
        )
        this.createDoughnutChart()
      }
    )
  }

  createDoughnutChart() {
    this.chart = new Chart(
      "doughnut-chart-classes-status-distribution",
      {
        type: "doughnut",
        data: {
          labels: ["Upcoming classes", "Done classes", "Rejected classes", "Cancelled classes"],
          datasets: [
            {
              data: [
                this.upcomingClassesNumber, this.doneClassesNumber, this.rejectedClassesNumber, this.cancelledClassesNumber
              ],
              backgroundColor: ["blue", "limegreen", "yellow", "red"]
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
