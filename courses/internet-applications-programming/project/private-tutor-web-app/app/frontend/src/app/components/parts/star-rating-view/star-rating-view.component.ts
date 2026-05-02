import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-star-rating-view',
  templateUrl: './star-rating-view.component.html',
  styleUrls: ['./star-rating-view.component.css']
})
export class StarRatingViewComponent {
  @Input() rating: number = 0
}
