import {Type} from '@angular/core';

export enum PopupType {
  Confirmation = 'Confirmation',
  Input = 'Input',
  DATE = 'DATE',
  CHOICE = 'CHOICE',
  UPLOAD = 'UPLOAD',
  SINGLE_UPLOAD = 'SINGLE_UPLOAD',
  IMAGE_VIEWER = 'IMAGE_VIEWER',
  COMPONENT = 'COMPONENT',
  SELECT = 'SELECT',
}

export interface PopupOption {
  label: string;
  value: number;
}

export interface PopupConfig {
  type: PopupType;
  title: string;
  description?: string;
  inputValue?: string;
  dateValue?: Date;
  confirmButton?: string;
  options?: PopupOption[];
  choiceButtons?: {
    text: string;
    callback: () => void;
  }[];
  imageUrl?: string;
  callback?: (input?: any) => void;
  fileCallback?: (files: File[]) => void;
  closeCallback?: () => void;

  /** Pour l’affichage d’un composant Angular */
  component?: Type<any>;
  componentInputs?: { [key: string]: any };
}
