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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.Path;
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
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

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

    @Operation(summary = "Export XLSX des décisions QPC CC")
    @GetMapping(value = "/export-xls", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
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
        // DÉLAI AVANT ABROGATION (expression type: >6, <=12, =0, <>3, ...)
        // --------------------
        if (req.getDelaiAvantAbrogationExpression() != null &&
                !req.getDelaiAvantAbrogationExpression().isBlank()) {
            spec = spec.and(buildDelaiAvantAbrogationSpec(req.getDelaiAvantAbrogationExpression()));
        }

        // --------------------
        // NOMBRE D'INTERVENTIONS ADMISES
        // --------------------
        if (req.getNombreInterventionsExact() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("nombreInterventionsAdmises"), req.getNombreInterventionsExact()));
        }

        if (req.getNombreInterventionsMin() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("nombreInterventionsAdmises"), req.getNombreInterventionsMin()));
        }

        if (req.getNombreInterventionsMax() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("nombreInterventionsAdmises"), req.getNombreInterventionsMax()));
        }

        // --------------------
        // NOMBRE DE MEMBRES SIÉGEANT
        // --------------------
        if (req.getNombreMembresSieges() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("nombreMembresSieges"), req.getNombreMembresSieges()));
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

        // Oralité : ton modèle a un ListeDeroulanteModel "oralite"
        // et le formulaire Oui/Non → ici, on interprète :
        //   true  => il y a une valeur d'oralité
        //   false => pas d'oralité
        if (req.getOralite() != null) {
            if (req.getOralite()) {
                spec = spec.and((root, query, cb) ->
                        cb.isNotNull(root.get("oralite")));
            } else {
                spec = spec.and((root, query, cb) ->
                        cb.isNull(root.get("oralite")));
            }
        }

        // --------------------
        // TECHNIQUES DE CONTRÔLE (search texte sur LOB)
        // --------------------
        if (req.getTechniquesControle() != null && !req.getTechniquesControle().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                var field = cb.lower(root.get("techniquesControle"));
                java.util.List<Predicate> predicates = new java.util.ArrayList<>();
                for (String t : req.getTechniquesControle()) {
                    if (t != null && !t.isBlank()) {
                        predicates.add(cb.like(field, "%" + t.toLowerCase() + "%"));
                    }
                }
                if (predicates.isEmpty()) {
                    return cb.conjunction();
                }
                return cb.or(predicates.toArray(new Predicate[0]));
            });
        }

        // --------------------
        // MOTIFS D'INCONSTITUTIONNALITÉ (search texte sur LOB)
        // --------------------
        if (req.getMotifsInconstitutionnalite() != null && !req.getMotifsInconstitutionnalite().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                var field = cb.lower(root.get("motifInconstitutionnalite"));
                java.util.List<Predicate> predicates = new java.util.ArrayList<>();
                for (String m : req.getMotifsInconstitutionnalite()) {
                    if (m != null && !m.isBlank()) {
                        predicates.add(cb.like(field, "%" + m.toLowerCase() + "%"));
                    }
                }
                if (predicates.isEmpty()) {
                    return cb.conjunction();
                }
                return cb.or(predicates.toArray(new Predicate[0]));
            });
        }

        return spec;
    }

    /**
     * Parse une expression de délai d'abrogation du type
     * ">6", "<=3", "=12", "<>0", "<10" ...
     * et construit la Specification correspondante sur le champ
     * "delaiAvantAbrogationMois".
     */
    private Specification<DecisionQpcCcModel> buildDelaiAvantAbrogationSpec(String expression) {
        return (root, query, cb) -> {
            if (expression == null) {
                return cb.conjunction();
            }

            String exp = expression.trim().replace(" ", "");
            if (exp.isEmpty()) {
                return cb.conjunction();
            }

            String op = "=";
            String valuePart = exp;

            // opérateurs à 2 caractères
            if (exp.startsWith("<=") || exp.startsWith(">=") || exp.startsWith("<>")) {
                op = exp.substring(0, 2);
                valuePart = exp.substring(2);
            } else if (exp.startsWith("<") || exp.startsWith(">") || exp.startsWith("=")) {
                op = exp.substring(0, 1);
                valuePart = exp.substring(1);
            }

            Integer value;
            try {
                value = Integer.valueOf(valuePart);
            } catch (Exception e) {
                throw RestError.INVALID_FIELD.get("delaiAvantAbrogationExpression");
            }

            // === CORRECTION ICI ===
            Path<Integer> path = root.get("delaiAvantAbrogationMois");

            return switch (op) {
                case "<" -> cb.lt(path, value);
                case "<=" -> cb.le(path, value);
                case ">" -> cb.gt(path, value);
                case ">=" -> cb.ge(path, value);
                case "<>" -> cb.notEqual(path, value);
                case "=" -> cb.equal(path, value);
                default -> cb.conjunction();
            };
        };
    }

}