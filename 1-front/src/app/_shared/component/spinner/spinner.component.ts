
import { Component, DestroyRef, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatIcon } from '@angular/material/icon';
import {SpinnerService} from "../../../_services/spinner.service";

@Component({
  selector: 'app-spinner',
  standalone: true,
  templateUrl: './spinner.component.html',
  styleUrl: './spinner.component.scss',
  imports: [
    MatProgressSpinner,
    MatIcon,
  ],
})
export class SpinnerComponent implements OnInit {
  displaySpinner = false;

  constructor(
    private spinnerService: SpinnerService,
    private destroyRef: DestroyRef,
  ) {}

  ngOnInit() {
    this.spinnerService.spinnerState$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((state: boolean) => {
        this.displaySpinner = state;
      });
  }
}
