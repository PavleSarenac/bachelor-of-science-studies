import { Component, OnInit } from '@angular/core';
import { Chart } from "chart.js/auto"
import { AdminService } from 'src/app/services/admin/admin.service';

@Component({
  selector: 'app-histogram-chart-classes-per-day',
  templateUrl: './histogram-chart-classes-per-day.component.html',
  styleUrls: ['./histogram-chart-classes-per-day.component.css']
})
export class HistogramChartClassesPerDayComponent implements OnInit {
  chart: any
  yAxisValues: number[] = []

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.adminService.getAverageClassesPerDay().subscribe(
      (daysObject: any) => {
        this.setYAxisValues(daysObject)
        this.createHistogramChart()
      }
    )
  }

  setYAxisValues(daysObject: any) {
    let totalNumberOfClasses = 0

    totalNumberOfClasses += daysObject.Monday
    totalNumberOfClasses += daysObject.Tuesday
    totalNumberOfClasses += daysObject.Wednesday
    totalNumberOfClasses += daysObject.Thursday
    totalNumberOfClasses += daysObject.Friday
    totalNumberOfClasses += daysObject.Saturday
    totalNumberOfClasses += daysObject.Sunday

    this.yAxisValues[0] = daysObject.Monday / totalNumberOfClasses
    this.yAxisValues[1] = daysObject.Tuesday / totalNumberOfClasses
    this.yAxisValues[2] = daysObject.Wednesday / totalNumberOfClasses
    this.yAxisValues[3] = daysObject.Thursday / totalNumberOfClasses
    this.yAxisValues[4] = daysObject.Friday / totalNumberOfClasses
    this.yAxisValues[5] = daysObject.Saturday / totalNumberOfClasses
    this.yAxisValues[6] = daysObject.Sunday / totalNumberOfClasses
  }

  createHistogramChart() {
    this.chart = new Chart(
      "histogram-chart-classes-per-day",
      {
        type: "bar",
        data: {
          labels: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"],
          datasets: [
            {
              label: "Distribution of classes among days of the week (2023)",
              data: this.yAxisValues,
              backgroundColor: "blue"
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
