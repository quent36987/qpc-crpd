import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {
  DcisionsQPCCCApi,
  DecisionFiltrageQpcSearchRequest, DecisionQpcCcDTO, DecisionQpcCcRowDTO,
  DecisionQpcCcSearchRequest, DroitLiberteDTO, DroitsLibertsApi,
  ListeDeroulanteDTO, ListesDroulantesApi, PageDTODecisionQpcCcRowDTO
} from "../../_services/generated-api";
import {MultiSelectComponent, MultiSelectOption} from "../multi-select/multi-select";
import {SpinnerService} from "../../_services/spinner.service";
import {NotificationService} from "../../_services/notification.service";
import {apiWrapper} from "../../_services/api-wrapper";

@Component({
  selector: 'app-decision-cc',
  standalone: true,
  imports: [CommonModule, FormsModule, MultiSelectComponent],
  templateUrl: './decision-cc.html',
  styleUrls: ['./decision-cc.css'],
})
export class DecisionCCComponent implements OnInit {

  // === DTO de recherche, c’est lui qui part au back ===
  searchCriteria: DecisionQpcCcSearchRequest = {} as any;

  // === Données brutes venant du back pour les listes déroulantes ===
  listeDeroulanteOptions: ListeDeroulanteDTO[] = [];
   droitsLibertesRaw: DroitLiberteDTO[] = [];

  // === Options pour les <app-multi-select> (toujours { value, label }) ===
  options = {
    origines: [] as MultiSelectOption[],
    qualitesDemandeur: [] as MultiSelectOption[],
    matieres: [] as MultiSelectOption[],
    dispositifs: [] as MultiSelectOption[],
    traitementsEffetsPasses: [] as MultiSelectOption[],
    qualitesTiers: [] as MultiSelectOption[],
    reservesIncompetence: [] as MultiSelectOption[],
    droitsLibertes: [] as MultiSelectOption[],
    techniquesControle: [] as MultiSelectOption[],
    motifsInconstitutionnalite: [] as MultiSelectOption[],
  };

  // === Résultats / pagination / détails ===
  searchResult: PageDTODecisionQpcCcRowDTO | null = null;
  showResults = false;
  isLoading = false;
  currentPage = 1;
  pageSize = 10;
  selectedDecision: DecisionQpcCcDTO | null = null;
  formCollapsed = false;

  constructor(
    private spinnerService: SpinnerService,
    private notifService: NotificationService,
    private listeDeroulanteApi: ListesDroulantesApi,
    private decisionQpcCcService: DcisionsQPCCCApi, // adapte le nom si différent
    private droitsLibertesApi: DroitsLibertsApi,
  ) {}

  ngOnInit(): void {
    // 1) Charger toutes les listes déroulantes génériques
    this.listeDeroulanteApi.getAllListeDeroulante()
      .pipe(apiWrapper(this.spinnerService, this.notifService))
      .subscribe(data => {
        this.listeDeroulanteOptions = data;
        this.buildListeDeroulanteOptions();
      });

    this.droitsLibertesApi.getAllDroitLiberte()
      .pipe(apiWrapper(this.spinnerService, this.notifService))
      .subscribe(dls => {
        this.droitsLibertesRaw = dls;
        this.options.droitsLibertes = dls.map(dl => ({
          value: dl.id,
          label: dl.texte,
        }));
      });


    // 3) Techniques de contrôle / motifs d’inconstitutionnalité :
    // si c’est aussi en liste déroulante, tu pourras les construire
    // à partir de listeDeroulanteOptions, sinon tu peux les coder en dur.
    // Exemple simple (à adapter) :
    this.options.techniquesControle = [
      { value: 'proportionnalite', label: 'Proportionnalité' },
      { value: 'controle_concret', label: 'Contrôle concret' },
      { value: 'controle_abstrait', label: 'Contrôle abstrait' },
    ];

    this.options.motifsInconstitutionnalite = [
      { value: 'violation_droit_defense', label: 'Violation des droits de la défense' },
      { value: 'violation_egalite', label: 'Violation du principe d’égalité' },
      // ...
    ];
  }

  // ---------------------------------------------------------------------------
  //         Construction des options à partir de ListeDeroulanteDTO
  // ---------------------------------------------------------------------------
  private buildListeDeroulanteOptions(): void {
    const byChamp = (champ: string): ListeDeroulanteDTO[] =>
      this.listeDeroulanteOptions.filter(o => o.champ === champ && o.actif);

    // Origine QPC
    this.options.origines = byChamp('ORIGINE_QPC').map(o => ({
      value: o.id,
      label: o.valeur,
    }));

    // Qualité demandeur
    this.options.qualitesDemandeur = byChamp('QUALITE_DEMANDEUR_QPC_CC').map(o => ({
      value: o.id,
      label: o.valeur,
    }));

    // Matière
    this.options.matieres = byChamp('MATIERE_QPC_CC').map(o => ({
      value: o.id,
      label: o.valeur,
    }));

    // Dispositif décision CC
    this.options.dispositifs = byChamp('DISPOSITIF_DECISION_CC').map(o => ({
      value: o.id,
      label: o.valeur,
    }));

    // Traitement des effets passés
    this.options.traitementsEffetsPasses = byChamp('TRAITEMENT_EFFETS_PASSES').map(o => ({
      value: o.id,
      label: o.valeur,
    }));

    // Qualité tiers intervenants
    this.options.qualitesTiers = byChamp('QUALITE_TIERS_INTERVENTION').map(o => ({
      value: o.id,
      label: o.valeur,
    }));

    // Réserves d’incompétence
    this.options.reservesIncompetence = byChamp('RESERVE_INCOMPETENCE_CONSEIL').map(o => ({
      value: o.id,
      label: o.valeur,
    }));

    // Si techniques/motifs viennent aussi de liste déroulante, tu peux également
    // les construire ici plutôt que de les coder en dur dans ngOnInit.
  }

  // ---------------------------------------------------------------------------
  //                              Actions
  // ---------------------------------------------------------------------------
  onSearch(): void {
    this.isLoading = true;
    this.currentPage = 1;
    this.search(this.searchCriteria);
  }

  private search(payload: DecisionQpcCcSearchRequest): void {
    this.decisionQpcCcService
      .searchDecisions(this.currentPage - 1, this.pageSize, [], payload)
      .pipe(apiWrapper(this.spinnerService, this.notifService))
      .subscribe((res: PageDTODecisionQpcCcRowDTO) => {
        this.searchResult = res;
        this.showResults = true;
        this.isLoading = false;
        this.formCollapsed = true;
      });
  }

  goToPage(page: number): void {
    if (page < 1 || page > this.totalPages()) return;

    this.currentPage = page;
    this.isLoading = true;
    this.search(this.searchCriteria);
  }

  totalPages(): number {
    const result = this.searchResult;
    if (!result) return 0;
    return Math.ceil(result.totalElements / this.pageSize);
  }

  resetForm(): void {
    this.searchCriteria = {} as any;
    this.showResults = false;
    this.searchResult = null;
    this.currentPage = 1;
    this.selectedDecision = null;
  }

  viewDetails(decision: DecisionQpcCcRowDTO): void {
    this.decisionQpcCcService.getDecisionsById(decision.id)
      .pipe(apiWrapper(this.spinnerService, this.notifService))
      .subscribe(data => {
        this.selectedDecision = data;
      });
  }

  closeDetails(): void {
    this.selectedDecision = null;
  }

  toggleForm(): void {
    this.formCollapsed = !this.formCollapsed;
  }
}
