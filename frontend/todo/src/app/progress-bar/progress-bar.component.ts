import { Component, Input, ChangeDetectorRef, OnInit, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-progress-bar',
  template: `
    <div *ngIf="visible" class="progress-bar">
      <div class="progress-bar-inner" [style.width.%]="progress"></div>
    </div>
  `,
  styles: [
    `
      .progress-bar {
        width: 100%;
        height: 5px;
        background-color: #ddd;
        overflow: hidden;
        margin-bottom: 0;
      }

      .progress-bar-inner {
        height: 100%;
        background-color: #4caf50;
        transition: width 5s ease-out;
      }
    `,
  ],
})
export class ProgressBarComponent implements OnInit, OnDestroy {
  @Input() progress: number = 100;
  @Input() visible: boolean = false;

  private intervalId: any;

  constructor(private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.startProgressBar();
  }

  ngOnDestroy() {
    clearInterval(this.intervalId);
  }

  private startProgressBar() {
    this.intervalId = setInterval(() => {
      if (this.progress < 100) {
        this.progress += 1;
        this.cdr.detectChanges(); // Trigger change detection manually
      } else {
        clearInterval(this.intervalId);
      }
    }, 5);
  }
}