import {Component, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {DecisionCCService} from '../services/decision-cc.service';
import {DecisionConseilConstitutionnel, SearchResultCC} from '../models/decision-cc.model';
import {MultiSelectComponent} from "../multi-select/multi-select";

@Component({
  selector: 'app-decision-cc',
  standalone: true,
  imports: [CommonModule, FormsModule, MultiSelectComponent],
  templateUrl: './decision-cc.html',
  styleUrls: ['./decision-cc.scss'],
})
export class DecisionCCComponent implements OnInit {
  service = new DecisionCCService();
  options: any = {};

  searchCriteria: any = {};
  selectedOrigines: string[] = [];
  selectedQualitesDemandeur: string[] = [];
  selectedDroitsLibertes: string[] = [];
  selectedQualitesTiers: string[] = [];
  selectedMatieres: string[] = [];
  selectedDispositifs: string[] = [];
  selectedTraitements: string[] = [];
  selectedReservesIncompetence: string[] = [];
  selectedTechniques: string[] = [];
  selectedMotifs: string[] = [];

  searchResult = signal<SearchResultCC | null>(null);
  showResults = signal(false);
  isLoading = signal(false);
  currentPage = signal(1);
  pageSize = 10;
  selectedDecision = signal<DecisionConseilConstitutionnel | null>(null);
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
    this.selectedOrigines = [];
    this.selectedQualitesDemandeur = [];
    this.selectedDroitsLibertes = [];
    this.selectedQualitesTiers = [];
    this.selectedMatieres = [];
    this.selectedDispositifs = [];
    this.selectedTraitements = [];
    this.selectedReservesIncompetence = [];
    this.selectedTechniques = [];
    this.selectedMotifs = [];
    this.showResults.set(false);
    this.searchResult.set(null);
  }

  viewDetails(decision: DecisionConseilConstitutionnel) {
    this.selectedDecision.set(decision);
  }

  closeDetails() {
    this.selectedDecision.set(null);
  }

  toggleForm() {
    this.formCollapsed.update(val => !val);
  }
}
