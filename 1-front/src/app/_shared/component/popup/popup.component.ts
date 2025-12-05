import { AfterViewChecked, Component, ElementRef, OnInit, ViewChild } from '@angular/core';

import { PopupConfig, PopupType } from './popup.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import {PopupService} from "../../../_services/popup.service";

@Component({
  selector: 'app-popup',
  templateUrl: './popup.component.html',
  standalone: true,
  styleUrls: ['./popup.component.scss'],
  imports: [
    CommonModule,
    FormsModule,
    MatIcon,
  ],
})
export class PopupComponent implements OnInit, AfterViewChecked {
  popupConfig: PopupConfig | null = null;

  popupType = PopupType;

  selectedValue: any = null;
  selectedFiles: File[] = [];
  @ViewChild('popupInput') popupInput!: ElementRef<HTMLInputElement>;

  constructor(private popupService: PopupService) {
  }

  ngOnInit(): void {
    this.popupService.popupState$.subscribe(config => {
      this.popupConfig = config;
      this.selectedFiles = [];
    });
  }

  ngAfterViewChecked(): void {
    if (this.popupConfig?.type === PopupType.Input && this.popupInput) {
      this.popupInput.nativeElement.focus();
    }
  }

  onFileSelected(event: any): void {
    if (event.target.files.length > 0) {
      for (const file of event.target.files) {
        this.selectedFiles.push(file)
      }
    }
  }

  onConfirm(): void {
    if (this.popupConfig?.type === PopupType.UPLOAD && this.selectedFiles && this.popupConfig?.fileCallback) {
      this.popupConfig.fileCallback(this.selectedFiles);
    } else if (this.popupConfig?.type === PopupType.SELECT && this.selectedValue && this.popupConfig?.callback) {
      this.popupConfig.callback(this.selectedValue);
      this.selectedValue = null;
    } else if (this.popupConfig?.callback) {
      this.popupConfig.callback(this.popupConfig.inputValue);
    }
    this.popupService.closePopup();
  }

  onCancel(): void {
    this.popupService.closePopup();
  }
}
