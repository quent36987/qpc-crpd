import {Component, Input, Output, EventEmitter, signal, HostListener, ViewChild, ElementRef} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-multi-select',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './multi-select.html',
  styleUrls: ['./multi-select.scss'],
})
export class MultiSelectComponent {
  @Input() options: string[] = [];
  @Input() placeholder: string = 'Sélectionner...';
  @Input() selectedItems: string[] = [];
  @Output() selectedItemsChange = new EventEmitter<string[]>();

  @ViewChild('container') container!: ElementRef;

  isOpen = signal(false);
  searchText = '';
  filteredOptions = signal<string[]>([]);
  selected = signal<string[]>([]);

  ngOnInit() {
    this.selected.set(this.selectedItems);
    this.updateFilteredOptions();
  }

  ngOnChanges() {
    this.selected.set(this.selectedItems);
    this.updateFilteredOptions();
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

  toggleOption(option: string) {
    const selected = this.selected();
    const index = selected.indexOf(option);

    if (index === -1) {
      this.selected.set([...selected, option]);
    } else {
      this.selected.set(selected.filter((_, i) => i !== index));
    }

    this.selectedItemsChange.emit(this.selected());
  }

  removeItem(event: Event, item: string) {
    event.stopPropagation();
    const selected = this.selected();
    this.selected.set(selected.filter(s => s !== item));
    this.selectedItemsChange.emit(this.selected());
  }

  isSelected(option: string): boolean {
    return this.selected().includes(option);
  }

  filterOptions() {
    const text = this.searchText.toLowerCase();
    this.filteredOptions.set(
      this.options.filter(opt => opt.toLowerCase().includes(text))
    );
  }

  updateFilteredOptions() {
    this.filteredOptions.set(
      this.options.filter(opt => opt.toLowerCase().includes(this.searchText.toLowerCase()))
    );
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    const target = event.target as HTMLElement;
    if (this.container && !this.container.nativeElement.contains(target)) {
      this.closeDropdown();
    }
  }
}
