import {Component, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {SpinnerService} from "../../_services/spinner.service";
import {NotificationService} from "../../_services/notification.service";
import {
  DcisionsDeFiltrageQPCApi,
  DecisionFiltrageQpcDTO,
  DecisionFiltrageQpcRowDTO,
  DecisionFiltrageQpcSearchRequest,
  ListeDeroulanteDTO,
  ListesDroulantesApi,
  PageDTODecisionFiltrageQpcRowDTO
} from "../../_services/generated-api";
import {apiWrapper} from "../../_services/api-wrapper";
import {MultiSelectComponent, MultiSelectOption} from "../multi-select/multi-select";

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule, MultiSelectComponent],
  templateUrl: './search.html',
  styleUrls: ['./search.css'],
})
export class SearchComponent implements OnInit {

  // === DTO de recherche, c’est lui qui sera envoyé au back ===
  searchCriteria: DecisionFiltrageQpcSearchRequest = {} as any;

  // === Données brutes venant du back pour les listes déroulantes ===
  listeDeroulanteOptions: ListeDeroulanteDTO[] = [];

  // === Options pour le template (utilisées par les <app-multi-select>) ===
  options = {
    juridictions: [] as MultiSelectOption[],
    niveauxFiltrage: [] as MultiSelectOption[],
    formationsJugement: [] as MultiSelectOption[],
    chambresSection: [] as MultiSelectOption[],
    numerosChambresReunies: [] as MultiSelectOption[],
    applicationTheorieOptions: [] as MultiSelectOption[],
    origines: [] as MultiSelectOption[],
    codes: [] as MultiSelectOption[],
    matieres: [] as MultiSelectOption[],
    droitsLibertes: [] as MultiSelectOption[],
  };

  // === Valeurs sélectionnées dans chaque multi-select ===
  selectedJuridictions: DecisionFiltrageQpcSearchRequest.JuridictionsEnum[] = [];
  selectedNiveauxFiltrage: DecisionFiltrageQpcSearchRequest.NiveauxFiltrageEnum[] = [];

  selectedFormationsJugement: string[] = [];
  selectedChambresSection: ListeDeroulanteDTO[] = [];
  selectedNumerosChambresReunies: ListeDeroulanteDTO[] = [];
  selectedApplicationTheorie: ListeDeroulanteDTO[] = [];
  selectedOrigines: string[] = [];
  selectedCodes: string[] = [];
  selectedMatieres: ListeDeroulanteDTO[] = [];
  selectedDroitsLibertes: { id: number; label: string }[] = [];

  // === le reste comme avant ===
  searchResult: PageDTODecisionFiltrageQpcRowDTO | null = null;
  showResults = false;
  isLoading = false;
  currentPage = 1;
  pageSize = 10;
  selectedDecision: DecisionFiltrageQpcDTO | null = null;
  formCollapsed = false;

  constructor(
    private spinnerService: SpinnerService,
    private notifService: NotificationService,
    private listeDeroulanteApi: ListesDroulantesApi,
    private decisionFiltrageService: DcisionsDeFiltrageQPCApi,
  ) {
  }


  ngOnInit() {

    // enums → value = enum, label = text lisible
    this.options.juridictions = [
      {
        value: DecisionFiltrageQpcSearchRequest.JuridictionsEnum.ConseilEtat,
        label: 'Conseil d\'État',
      },
      {
        value: DecisionFiltrageQpcSearchRequest.JuridictionsEnum.CourDeCassation,
        label: 'Cour de cassation',
      },
    ];

    this.options.niveauxFiltrage = [
      { value: DecisionFiltrageQpcSearchRequest.NiveauxFiltrageEnum.None, label: 'Aucun' },
      { value: DecisionFiltrageQpcSearchRequest.NiveauxFiltrageEnum.PremierEtDernier, label: 'Premier et dernier' },
      { value: DecisionFiltrageQpcSearchRequest.NiveauxFiltrageEnum.DeuxiemeFiltrage, label: 'Deuxième filtrage' },
    ];

    this.listeDeroulanteApi.getAllListeDeroulante()
      .pipe(apiWrapper(this.spinnerService, this.notifService))
      .subscribe(data => {
        this.listeDeroulanteOptions = data;
        this.buildListeDeroulanteOptions();
      });

    // si tu as un service pour droitsLibertes, tu peux aussi les charger ici
  }

  private buildListeDeroulanteOptions() {
    const byChamp = (champ: string) =>
      this.listeDeroulanteOptions.filter(o => o.champ === champ && o.actif);

    this.options.chambresSection = byChamp('CHAMBRE_SOUS_SECTION').map(o => ({
      value: o.id,
      label: o.valeur,
    }));

    this.options.numerosChambresReunies = byChamp('NUMERO_CHAMBRES_REUNIES').map(o => ({
      value: o.id,
      label: o.valeur,
    }));

    this.options.matieres = byChamp('MATIERE').map(o => ({
      value: o.id,
      label: o.valeur,
    }));

    this.options.applicationTheorieOptions = byChamp('APPLICATION_THEORIE_CHANGEMENT_CIRCONSTANCES').map(o => ({
      value: o.id,
      label: o.valeur,
    }));

    // si ces champs viennent aussi de liste déroulante :
    // this.options.formationsJugement = byChamp('FORMATION_JUGEMENT').map(o => ({ value: o.valeur, label: o.valeur }));
    // this.options.origines = byChamp('ORIGINE_JURIDICTIONNELLE').map(o => ({ value: o.valeur, label: o.valeur }));
    // this.options.codes = byChamp('CODE').map(o => ({ value: o.valeur, label: o.valeur }));
  }

  onSearch() {
    this.isLoading = true;
    this.currentPage = 1;
    this.search(this.searchCriteria);
  }

  private search(payload: DecisionFiltrageQpcSearchRequest) {
    this.decisionFiltrageService
      .searchDecisionFiltrage(this.currentPage, this.pageSize, [], payload)
      .pipe(apiWrapper(this.spinnerService, this.notifService))
      .subscribe(res => {
        this.searchResult = res;
        this.showResults = true;
        this.isLoading = false;
        this.formCollapsed = true;
      });
  }

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages()) return;

    this.currentPage = page;
    this.isLoading = true;

    const payload: DecisionFiltrageQpcSearchRequest = {
      ...this.searchCriteria,
      juridictions: this.selectedJuridictions,
      niveauxFiltrage: this.selectedNiveauxFiltrage,
      formationsJugement: this.selectedFormationsJugement,
      chambresSousSectionIds: this.selectedChambresSection.map(s => s.id),
      numerosChambresReuniesIds: this.selectedNumerosChambresReunies.map(s => s.id),
      applicationTheorieChangementCirconstancesIds: this.selectedApplicationTheorie.map(s => s.id),
      originesJuridictionnellesQpc: this.selectedOrigines,
      matieresIds: this.selectedMatieres.map(s => s.id),
      droitsLibertesIds: this.selectedDroitsLibertes.map(d => d.id),
      codes: this.selectedCodes,
    };

    this.search(payload);
  }

  totalPages(): number {
    const result = this.searchResult;
    if (!result) return 0;
    return Math.ceil(result.totalElements / this.pageSize);
  }

  resetForm() {
    this.searchCriteria = {} as any;
    this.showResults = false;
    this.searchResult = null;
  }

  viewDetails(decision: DecisionFiltrageQpcRowDTO) {
    this.decisionFiltrageService.getDecisionFiltrageById(decision.id)
      .pipe(apiWrapper(this.spinnerService, this.notifService))
      .subscribe(data => {
        this.selectedDecision = data;
      });
  }

  closeDetails() {
    this.selectedDecision = null;
  }

  toggleForm() {
    this.formCollapsed = !this.formCollapsed;
  }
}
