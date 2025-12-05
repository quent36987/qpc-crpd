import {Component, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {DecisionFiltrageService} from '../services/decision-filtrage.service';
import {DecisionFiltrageQpc, SearchResult} from '../models/decision-filtrage.model';
import {MultiSelectComponent} from './multi-select.component';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule, MultiSelectComponent],
  templateUrl: './search.html',
  styleUrls: ['./search.scss'],
})
export class SearchComponent implements OnInit {
  service = new DecisionFiltrageService();
  options: any = {};

  searchCriteria: any = {};
  selectedJuridictions: string[] = [];
  selectedNiveauxFiltrage: string[] = [];
  selectedFormationsJugement: string[] = [];
  selectedChambresSection: string[] = [];
  selectedNumerosChambresReunies: string[] = [];
  selectedOrigines: string[] = [];
  selectedCodes: string[] = [];
  selectedMatieres: string[] = [];
  selectedDroitsLibertes: string[] = [];
  selectedApplicationTheorie: string[] = [];

  searchResult = signal<SearchResult | null>(null);
  showResults = signal(false);
  isLoading = signal(false);
  currentPage = signal(1);
  pageSize = 10;
  selectedDecision = signal<DecisionFiltrageQpc | null>(null);
  formCollapsed = signal(false);

  ngOnInit() {
    this.options = this.service.getOptions();
  }

  onSearch() {
    this.isLoading.set(true);
    this.currentPage.set(1);

    this.service.search(this.searchCriteria, this.currentPage(), this.pageSize)
      .subscribe(result => {
        this.searchResult.set(result);
        this.showResults.set(true);
        this.isLoading.set(false);
        this.formCollapsed.set(true);
      });
  }

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages()) return;

    this.currentPage.set(page);
    this.isLoading.set(true);

    this.service.search(this.searchCriteria, page, this.pageSize)
      .subscribe(result => {
        this.searchResult.set(result);
        this.isLoading.set(false);
        window.scrollTo({top: 0, behavior: 'smooth'});
      });
  }

  totalPages(): number {
    const result = this.searchResult();
    if (!result) return 0;
    return Math.ceil(result.total / this.pageSize);
  }

  resetForm() {
    this.searchCriteria = {};
    this.selectedJuridictions = [];
    this.selectedNiveauxFiltrage = [];
    this.selectedFormationsJugement = [];
    this.selectedChambresSection = [];
    this.selectedNumerosChambresReunies = [];
    this.selectedOrigines = [];
    this.selectedCodes = [];
    this.selectedMatieres = [];
    this.selectedDroitsLibertes = [];
    this.selectedApplicationTheorie = [];
    this.showResults.set(false);
    this.searchResult.set(null);
  }

  viewDetails(decision: DecisionFiltrageQpc) {
    this.selectedDecision.set(decision);
  }

  closeDetails() {
    this.selectedDecision.set(null);
  }

  toggleForm() {
    this.formCollapsed.update(val => !val);
  }
}
