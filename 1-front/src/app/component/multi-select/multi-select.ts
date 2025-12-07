import {
  Component,
  Input,
  Output,
  EventEmitter,
  signal,
  HostListener,
  ViewChild,
  ElementRef,
  OnInit,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

export interface MultiSelectOption {
  value: string | number;
  label: string;
}

@Component({
  selector: 'app-multi-select',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './multi-select.html',
  styleUrls: ['./multi-select.css'],
})
export class MultiSelectComponent implements OnInit, OnChanges {

  // ⚠️ maintenant : options = { value, label }
  @Input() options: MultiSelectOption[] = [];
  @Input() placeholder: string = 'Sélectionner...';

  /**
   * selectedItems = liste des values (id ou enum)
   * → c’est CE TABLEAU que tu envoies direct au back.
   */
  @Input() selectedItems: (string | number)[] = [];
  @Output() selectedItemsChange = new EventEmitter<(string | number)[]>();

  @ViewChild('container') container!: ElementRef;

  isOpen = signal(false);
  searchText = '';
  filteredOptions = signal<MultiSelectOption[]>([]);
  selected = signal<(string | number)[]>([]);

  ngOnInit() {
    this.selected.set(this.selectedItems ?? []);
    this.updateFilteredOptions();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['selectedItems']) {
      this.selected.set(this.selectedItems ?? []);
    }
    if (changes['options']) {
      this.updateFilteredOptions();
    }
  }

  toggleDropdown() {
    this.isOpen.update(val => !val);
    this.updateFilteredOptions();
  }

  closeDropdown() {
    this.isOpen.set(false);
    this.searchText = '';
    this.updateFilteredOptions();
  }

  toggleOption(option: MultiSelectOption) {
    const current = this.selected();
    const index = current.indexOf(option.value);
    let next: (string | number)[];

    if (index === -1) {
      next = [...current, option.value];
    } else {
      next = current.filter((_, i) => i !== index);
    }

    this.selected.set(next);
    this.selectedItemsChange.emit(next);
  }

  removeItem(event: Event, value: string | number) {
    event.stopPropagation();
    const current = this.selected();
    const next = current.filter(v => v !== value);
    this.selected.set(next);
    this.selectedItemsChange.emit(next);
  }

  isSelected(value: string | number): boolean {
    return this.selected().includes(value);
  }

  filterOptions() {
    const text = this.searchText.toLowerCase();
    this.filteredOptions.set(
      this.options.filter(opt => opt.label.toLowerCase().includes(text))
    );
  }

  updateFilteredOptions() {
    const text = this.searchText.toLowerCase();
    this.filteredOptions.set(
      this.options.filter(opt => opt.label.toLowerCase().includes(text))
    );
  }

  getSelectedOptions(): MultiSelectOption[] {
    const values = this.selected();
    return this.options.filter(o => values.includes(o.value));
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    const target = event.target as HTMLElement;
    if (this.container && !this.container.nativeElement.contains(target)) {
      this.closeDropdown();
    }
  }
}
