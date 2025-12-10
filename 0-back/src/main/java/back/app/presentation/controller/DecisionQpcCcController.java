package back.app.presentation.controller;

import back.app.data.model.qpc.DecisionQpcCcModel;
import back.app.domain.entity.DecisionQpcCcDTO;
import back.app.domain.entity.DecisionQpcCcRowDTO;
import back.app.domain.entity.PageDTO;
import back.app.domain.service.DecisionQpcCcService;
import back.app.domain.service.excel.export.DecisionQpcCcExportService;
import back.app.domain.service.excel.upload.DecisionQpcCcImportService;
import back.app.presentation.request.DecisionQpcCcSearchRequest;
import back.app.utils.errors.RestError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/decisions-qpc-cc", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Décisions QPC CC", description = "Recherche des décisions QPC du Conseil constitutionnel")
public class DecisionQpcCcController {

    private final DecisionQpcCcService decisionQpcCcService;
    private final DecisionQpcCcExportService decisionQpcCcServiceExport;
    private final DecisionQpcCcImportService decisionQpcCcImportService;


    @Operation(summary = "Search paginated décisions QPC CC")
    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PageDTO<DecisionQpcCcRowDTO> searchDecisions(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
            @RequestParam(defaultValue = "id.desc", name = "sort") String[] sort,
            @RequestBody(required = false) DecisionQpcCcSearchRequest searchRequest
    ) {

        Sort sorting = Sort.by(Arrays.stream(sort)
                .map(s -> {
                    try {
                        String[] parts = s.split("\\.");
                        return parts[1].equalsIgnoreCase("desc")
                                ? Sort.Order.desc(parts[0])
                                : Sort.Order.asc(parts[0]);
                    } catch (Exception e) {
                        throw RestError.INVALID_FIELD.get("sort");
                    }
                })
                .toList());

        Pageable pageable = PageRequest.of(page, size, sorting);

        Specification<DecisionQpcCcModel> spec = Specification.where(null);

        if (searchRequest != null) {
            spec = spec.and(buildSearchSpecification(searchRequest));
        }

        return decisionQpcCcService.getPaginatedDecisionsQpcCc(spec, pageable);
    }

    @Operation(
            summary = "Export XLSX d'une requête de décisions QPC CC",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Fichier XLSX",
                    content = @Content(
                            mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
    )
    @PostMapping(value = "/export-xls", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportQpcCcXls(
            @RequestBody(required = false) DecisionQpcCcSearchRequest searchRequest
    ) {
        Specification<DecisionQpcCcModel> spec = Specification.where(null);

        if (searchRequest != null) {
            spec = spec.and(buildSearchSpecification(searchRequest));
        }

        byte[] bytes = decisionQpcCcServiceExport.getXlsDecisionsQpcCc(spec);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=%s.xlsx".formatted("decisions_qpc_cc"))
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    @Operation(summary = "Get a décision QPC CC by ID")
    @GetMapping("/{id}")
    public DecisionQpcCcDTO getDecisionsById(@PathVariable Long id) {
        return decisionQpcCcService.getById(id);
    }


    @Operation(summary = "Import XLSX des décisions QPC CC")
    @PreAuthorize("hasAuthority('IMPORT_XLS')")
    @PostMapping(value = "/import-xls", consumes = "multipart/form-data")
    public ResponseEntity<Void> importQpcCcXls(@RequestPart("file") MultipartFile file) throws Exception {
        try (var in = file.getInputStream()) {
            decisionQpcCcImportService.importFromXls(in);
        }
        return ResponseEntity.ok().build();
    }

    // ------------------------------------------------------------------------
    //                          SPEC HELPERS
    // ------------------------------------------------------------------------

    private Specification<DecisionQpcCcModel> buildSearchSpecification(DecisionQpcCcSearchRequest req) {
        Specification<DecisionQpcCcModel> spec = Specification.where(null);

        // --------------------
        // TEXTES SIMPLES (LIKE, case-insensitive)
        // --------------------
        if (req.getNumero() != null && !req.getNumero().isBlank()) {
            String value = "%" + req.getNumero().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("numero")), value));
        }

        if (req.getReferenceDecisionConseil() != null && !req.getReferenceDecisionConseil().isBlank()) {
            String value = "%" + req.getReferenceDecisionConseil().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("referenceDecisionConseil")), value));
        }

        if (req.getReferenceDecisionTransmission() != null && !req.getReferenceDecisionTransmission().isBlank()) {
            String value = "%" + req.getReferenceDecisionTransmission().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("referenceDecisionTransmission")), value));
        }

        if (req.getIdentiteDemandeur() != null && !req.getIdentiteDemandeur().isBlank()) {
            String value = "%" + req.getIdentiteDemandeur().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("identiteDemandeur")), value));
        }

        if (req.getDispositionsLegislativesContestees() != null
                && !req.getDispositionsLegislativesContestees().isBlank()) {
            String value = "%" + req.getDispositionsLegislativesContestees().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("dispositionsLegislativesContestees")), value));
        }

        if (req.getNomMembreDeporteOuRecuse() != null && !req.getNomMembreDeporteOuRecuse().isBlank()) {
            String value = "%" + req.getNomMembreDeporteOuRecuse().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nomMembreDeporteOuRecuse")), value));
        }

        if (req.getRemarque() != null && !req.getRemarque().isBlank()) {
            String value = "%" + req.getRemarque().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("autresRemarques")), value));
        }

        if (req.getTechniquesControle() != null && !req.getTechniquesControle().isBlank()) {
            String value = "%" + req.getTechniquesControle().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("techniquesControle")), value));
        }

        if (req.getMotifsInconstitutionnalite() != null && !req.getMotifsInconstitutionnalite().isBlank()) {
            String value = "%" + req.getMotifsInconstitutionnalite().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("motifInconstitutionnalite")), value));
        }


        // --------------------
        // DATES
        // --------------------
        if (req.getDateDecisionFrom() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("dateDecision"), req.getDateDecisionFrom()));
        }

        if (req.getDateDecisionTo() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("dateDecision"), req.getDateDecisionTo()));
        }

        if (req.getDateAbrogationDiffereeFrom() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("dateAbrogationDifferee"), req.getDateAbrogationDiffereeFrom()));
        }

        if (req.getDateAbrogationDiffereeTo() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("dateAbrogationDifferee"), req.getDateAbrogationDiffereeTo()));
        }

        // --------------------
        // LISTES DÉROULANTES (ids de ListeDeroulanteModel)
        // --------------------
        if (req.getOriginesQpcIds() != null && !req.getOriginesQpcIds().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("origineQpc").get("id").in(req.getOriginesQpcIds()));
        }

        if (req.getQualitesDemandeurIds() != null && !req.getQualitesDemandeurIds().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("qualiteDemandeur").get("id").in(req.getQualitesDemandeurIds()));
        }

        if (req.getTypesDispositionLegislativeIds() != null && !req.getTypesDispositionLegislativeIds().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("typeDispositionLegislative").get("id").in(req.getTypesDispositionLegislativeIds()));
        }

        if (req.getMatieresIds() != null && !req.getMatieresIds().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("matiere").get("id").in(req.getMatieresIds()));
        }

        if (req.getDispositifsDecisionCcIds() != null && !req.getDispositifsDecisionCcIds().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("dispositifDecisionCc").get("id").in(req.getDispositifsDecisionCcIds()));
        }

        if (req.getTraitementsEffetsPassesIds() != null && !req.getTraitementsEffetsPassesIds().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("traitementEffetsPasses").get("id").in(req.getTraitementsEffetsPassesIds()));
        }

        if (req.getQualitesTiersInterventionIds() != null && !req.getQualitesTiersInterventionIds().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("qualiteTiersIntervention").get("id").in(req.getQualitesTiersInterventionIds()));
        }

        if (req.getReservesIncompetenceConseilIds() != null && !req.getReservesIncompetenceConseilIds().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("reserveIncompetenceConseil").get("id").in(req.getReservesIncompetenceConseilIds()));
        }

        // --------------------
        // DROITS & LIBERTÉS (many-to-many)
        // --------------------
        if (req.getDroitsLibertesIds() != null && !req.getDroitsLibertesIds().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);
                Join<Object, Object> join = root.join("droitsLibertes", JoinType.INNER);
                return join.get("id").in(req.getDroitsLibertesIds());
            });
        }

        // --------------------
        // DÉLAI AVANT ABROGATION (bornes)
        // --------------------
        if (req.getDelaiAvantAbrogationMin() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("delaiAvantAbrogationMois"), req.getDelaiAvantAbrogationMin()));
        }

        if (req.getDelaiAvantAbrogationMax() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("delaiAvantAbrogationMois"), req.getDelaiAvantAbrogationMax()));
        }

        // --------------------
        // NOMBRE D'INTERVENTIONS ADMISES (bornes)
        // --------------------
        if (req.getNombreInterventionsMin() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("nombreInterventionsAdmises"), req.getNombreInterventionsMin()));
        }

        if (req.getNombreInterventionsMax() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("nombreInterventionsAdmises"), req.getNombreInterventionsMax()));
        }

        // --------------------
        // NOMBRE DE MEMBRES SIÉGEANT (bornes)
        // --------------------
        if (req.getNombreMembresSiegesMin() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("nombreMembresSieges"), req.getNombreMembresSiegesMin()));
        }

        if (req.getNombreMembresSiegesMax() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("nombreMembresSieges"), req.getNombreMembresSiegesMax()));
        }

        // --------------------
        // NOMBRE DE DROITS & LIBERTÉS INVOQUÉS (bornes)
        // --------------------
        if (req.getNombreDroitsLibertesMin() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("nombreDroitsLibertesInvoques"),
                            req.getNombreDroitsLibertesMin()));
        }

        if (req.getNombreDroitsLibertesMax() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("nombreDroitsLibertesInvoques"),
                            req.getNombreDroitsLibertesMax()));
        }

        // --------------------
        // RÉPARTITION PAR TYPES DE PARTIES (bornes)
        // --------------------
        if (req.getNombrePersonnesPhysiquesMin() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("nombrePersonnesPhysiques"),
                            req.getNombrePersonnesPhysiquesMin()));
        }

        if (req.getNombrePersonnesPhysiquesMax() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("nombrePersonnesPhysiques"),
                            req.getNombrePersonnesPhysiquesMax()));
        }

        if (req.getNombreAssociationsMin() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("nombreAssociations"),
                            req.getNombreAssociationsMin()));
        }

        if (req.getNombreAssociationsMax() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("nombreAssociations"),
                            req.getNombreAssociationsMax()));
        }

        if (req.getNombreEntreprisesMin() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("nombreEntreprises"),
                            req.getNombreEntreprisesMin()));
        }

        if (req.getNombreEntreprisesMax() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("nombreEntreprises"),
                            req.getNombreEntreprisesMax()));
        }

        if (req.getNombreSyndicatsApOpMin() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("nombreSyndicatsApOp"),
                            req.getNombreSyndicatsApOpMin()));
        }

        if (req.getNombreSyndicatsApOpMax() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("nombreSyndicatsApOp"),
                            req.getNombreSyndicatsApOpMax()));
        }

        if (req.getNombreCollectivitesTerritorialesMin() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("nombreCollectivitesTerritoriales"),
                            req.getNombreCollectivitesTerritorialesMin()));
        }

        if (req.getNombreCollectivitesTerritorialesMax() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("nombreCollectivitesTerritoriales"),
                            req.getNombreCollectivitesTerritorialesMax()));
        }

        // --------------------
        // DEMANDE DE RÉCUSATION (Integer en base, booléen en search)
        // --------------------
        if (req.getDemandeRecusation() != null) {
            if (req.getDemandeRecusation()) {
                spec = spec.and((root, query, cb) ->
                        cb.greaterThan(root.get("demandeRecusation"), 0));
            } else {
                spec = spec.and((root, query, cb) ->
                        cb.or(
                                cb.isNull(root.get("demandeRecusation")),
                                cb.equal(root.get("demandeRecusation"), 0)
                        ));
            }
        }

        // --------------------
        // DÉPORT (Integer en base, booléen en search)
        // --------------------
        if (req.getDeport() != null) {
            if (req.getDeport()) {
                spec = spec.and((root, query, cb) ->
                        cb.greaterThan(root.get("deport"), 0));
            } else {
                spec = spec.and((root, query, cb) ->
                        cb.or(
                                cb.isNull(root.get("deport")),
                                cb.equal(root.get("deport"), 0)
                        ));
            }
        }

        // --------------------
        // BOOLÉENS DIRECTS SUR LE MODÈLE
        // --------------------
        if (req.getApplicationTheorieChangementCirconstances() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("applicationTheorieChangementCirconstances"),
                            req.getApplicationTheorieChangementCirconstances()));
        }

        if (req.getReserveOpportunite() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("reserveOpportunite"), req.getReserveOpportunite()));
        }

        if (req.getInterpretationJurisprudence() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("priseEnCompteInterpretationJurisprudentielle"),
                            req.getInterpretationJurisprudence()));
        }

        if (req.getCaractereNotableDecision() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("caractereNotableDecision"),
                            req.getCaractereNotableDecision()));
        }

        // Oralité est maintenant un booléen directement sur le modèle
        if (req.getOralite() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("oralite"), req.getOralite()));
        }

        return spec;
    }

}
