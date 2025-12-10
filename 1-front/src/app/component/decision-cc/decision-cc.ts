import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  DcisionsQPCCCApi,
  DecisionQpcCcDTO,
  DecisionQpcCcRowDTO,
  DecisionQpcCcSearchRequest,
  DroitLiberteDTO,
  DroitsLibertsApi,
  ListeDeroulanteDTO,
  ListesDroulantesApi,
  PageDTODecisionQpcCcRowDTO
} from '../../_services/generated-api';
import { MultiSelectComponent, MultiSelectOption } from '../multi-select/multi-select';
import { SpinnerService } from '../../_services/spinner.service';
import { NotificationService } from '../../_services/notification.service';
import { apiWrapper } from '../../_services/api-wrapper';

@Component({
  selector: 'app-decision-cc',
  standalone: true,
  imports: [CommonModule, FormsModule, MultiSelectComponent],
  templateUrl: './decision-cc.html',
  styleUrls: ['./decision-cc.css'],
})
export class DecisionCCComponent implements OnInit {

  // === DTO de recherche, tel qu’envoyé au back ===
  searchCriteria: DecisionQpcCcSearchRequest = {} as any;

  // === Données brutes venant du back pour les listes déroulantes ===
  listeDeroulanteOptions: ListeDeroulanteDTO[] = [];
  droitsLibertesRaw: DroitLiberteDTO[] = [];

  // === Options pour les <app-multi-select> (toujours { value, label }) ===
  options = {
    origines: [] as MultiSelectOption[],
    qualitesDemandeur: [] as MultiSelectOption[],
    typesDispositionLegislative: [] as MultiSelectOption[],
    matieres: [] as MultiSelectOption[],
    dispositifs: [] as MultiSelectOption[],
    traitementsEffetsPasses: [] as MultiSelectOption[],
    qualitesTiers: [] as MultiSelectOption[],
    reservesIncompetence: [] as MultiSelectOption[],
    droitsLibertes: [] as MultiSelectOption[],
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
    private decisionQpcCcService: DcisionsQPCCCApi,
    private droitsLibertesApi: DroitsLibertsApi,
  ) {}

  // ---------------------------------------------------------------------------
  //                                  Init
  // ---------------------------------------------------------------------------
  ngOnInit(): void {
    // 1) Listes déroulantes génériques
    this.listeDeroulanteApi.getAllListeDeroulante()
      .pipe(apiWrapper(this.spinnerService, this.notifService))
      .subscribe(data => {
        this.listeDeroulanteOptions = data;
        this.buildListeDeroulanteOptions();
      });

    // 2) Droits & libertés
    this.droitsLibertesApi.getAllDroitLiberte()
      .pipe(apiWrapper(this.spinnerService, this.notifService))
      .subscribe(dls => {
        this.droitsLibertesRaw = dls;
        this.options.droitsLibertes = dls.map(dl => ({
          value: dl.id!,
          label: dl.texte!,
        }));
      });
  }

  downloadExcel() {
    this.decisionQpcCcService.exportQpcCcXls(this.searchCriteria)
      .pipe(apiWrapper(this.spinnerService, this.notifService, undefined, "Téléchargement pret"))
      .subscribe(blob => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'decisions_filtrage_qpc.xlsx';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      });
  }

  // ---------------------------------------------------------------------------
  //         Construction des options à partir de ListeDeroulanteDTO
  // ---------------------------------------------------------------------------
  private buildListeDeroulanteOptions(): void {
    const byChamp = (champ: string): ListeDeroulanteDTO[] =>
      this.listeDeroulanteOptions.filter(o => o.champ === champ && o.actif);

    // Origine QPC
    this.options.origines = byChamp('decision_qpc_cc.origine_qpc').map(o => ({
      value: o.id!,
      label: o.valeur!,
    }));

    // Qualité demandeur
    this.options.qualitesDemandeur = byChamp('decision_qpc_cc.qualite_demandeur').map(o => ({
      value: o.id!,
      label: o.valeur!,
    }));

    // Type de disposition législative contestée
    this.options.typesDispositionLegislative = byChamp('decision_qpc_cc.type_disposition_legislative').map(o => ({
      value: o.id!,
      label: o.valeur!,
    }));

    // Matière
    this.options.matieres = byChamp('decision_qpc_cc.matiere').map(o => ({
      value: o.id!,
      label: o.valeur!,
    }));

    // Dispositif décision CC
    this.options.dispositifs = byChamp('decision_qpc_cc.dispositif_decision_cc').map(o => ({
      value: o.id!,
      label: o.valeur!,
    }));

    // Traitement des effets passés
    this.options.traitementsEffetsPasses = byChamp('decision_qpc_cc.traitement_effets_passes').map(o => ({
      value: o.id!,
      label: o.valeur!,
    }));

    // Qualité tiers intervenants
    this.options.qualitesTiers = byChamp('decision_qpc_cc.qualite_tiers_intervention').map(o => ({
      value: o.id!,
      label: o.valeur!,
    }));

    // Réserve d’incompétence du Conseil
    this.options.reservesIncompetence = byChamp('decision_qpc_cc.reserve_incompetence_conseil').map(o => ({
      value: o.id!,
      label: o.valeur!,
    }));

    // Oralité est maintenant un booléen dans le modèle => pas de liste ici
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
      .pipe(apiWrapper(this.spinnerService, this.notifService, undefined, "Recherche terminée"))
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
    return Math.ceil(result.totalElements! / this.pageSize);
  }

  resetForm(): void {
    this.searchCriteria = {} as any;
    this.showResults = false;
    this.searchResult = null;
    this.currentPage = 1;
    this.selectedDecision = null;
    this.formCollapsed = false;
  }

  viewDetails(decision: DecisionQpcCcRowDTO): void {
    this.decisionQpcCcService.getDecisionsById(decision.id!)
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
