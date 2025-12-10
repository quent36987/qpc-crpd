package back.app.presentation.controller;

import back.app.data.model.qpc.DecisionFiltrageQpcModel;
import back.app.domain.entity.DecisionFiltrageQpcDTO;
import back.app.domain.entity.DecisionFiltrageQpcRowDTO;
import back.app.domain.entity.PageDTO;
import back.app.domain.service.DecisionFiltrageQpcService;
import back.app.domain.service.excel.export.DecisionFiltrageQpcExportService;
import back.app.domain.service.excel.upload.DecisionFiltrageQpcImportService;
import back.app.presentation.request.DecisionFiltrageQpcSearchRequest;
import back.app.utils.errors.RestError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping(path = "/api/decisions-filtrage-qpc", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Décisions de filtrage QPC", description = "Recherche des décisions de filtrage CE / Cour de cassation")
public class DecisionFiltrageQpcController {

    private final DecisionFiltrageQpcService decisionFiltrageQpcService;
    private final DecisionFiltrageQpcExportService decisionFiltrageQpcExportService;
    private final DecisionFiltrageQpcImportService importService;


    @Operation(summary = "Search paginated décisions de filtrage QPC")
    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PageDTO<DecisionFiltrageQpcRowDTO> searchDecisionFiltrage(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
            @RequestParam(defaultValue = "id.desc", name = "sort") String[] sort,
            @RequestBody(required = false) DecisionFiltrageQpcSearchRequest searchRequest
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

        Specification<DecisionFiltrageQpcModel> spec = Specification.where(null);

        if (searchRequest != null) {
            spec = spec.and(buildSearchSpecification(searchRequest));
        }

        return decisionFiltrageQpcService.getPaginatedDecisionsFiltrage(spec, pageable);
    }

    @Operation(
            summary = "Export XLSX d'une requête de décisions de filtrage QPC",
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
    public ResponseEntity<byte[]> exportXlsQpcFiltrage(@RequestBody(required = false) DecisionFiltrageQpcSearchRequest searchRequest) {

        Specification<DecisionFiltrageQpcModel> spec = Specification.where(null);

        if (searchRequest != null) {
            spec = spec.and(buildSearchSpecification(searchRequest));
        }

        var bytes = decisionFiltrageQpcExportService.getXlsDecisionsFiltrage(spec);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=%s.xlsx".formatted("decisions_filtrage_qpc"))
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    @Operation(summary = "Get a décision de filtrage QPC by ID")
    @GetMapping("/{id}")
    public DecisionFiltrageQpcDTO getDecisionFiltrageById(@PathVariable Long id) {
        return decisionFiltrageQpcService.getById(id);
    }

    @Operation(summary = "Import XLSX des décisions de filtrage QPC")
    @PreAuthorize("hasAuthority('IMPORT_XLS')")
    @PostMapping(value = "/import-xls", consumes = "multipart/form-data")
    public ResponseEntity<Void> importDecisionFiltrageXls(@RequestPart("file") MultipartFile file) throws Exception {
        try (var in = file.getInputStream()) {
            importService.importFromXls(in);
        }
        return ResponseEntity.ok().build();
    }

    // ------------------------------------------------------------------------
    //                          SPEC HELPERS
    // ------------------------------------------------------------------------
    private Specification<DecisionFiltrageQpcModel> buildSearchSpecification(DecisionFiltrageQpcSearchRequest req) {
        Specification<DecisionFiltrageQpcModel> spec = Specification.where(null);

        if (req.getNumeroDecision() != null && !req.getNumeroDecision().isBlank()) {
            String value = "%" + req.getNumeroDecision().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("numeroDecision")), value));
        }

        if (req.getReference() != null && !req.getReference().isBlank()) {
            String value = "%" + req.getReference().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("reference")), value));
        }

        if (req.getJuridictions() != null && !req.getJuridictions().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("juridiction").in(req.getJuridictions()));
        }

        if (req.getNiveauxFiltrage() != null && !req.getNiveauxFiltrage().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("niveauFiltrage").in(req.getNiveauxFiltrage()));
        }

        if (req.getDateFiltrageFrom() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("dateFiltrage"), req.getDateFiltrageFrom()));
        }

        if (req.getDateFiltrageTo() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("dateFiltrage"), req.getDateFiltrageTo()));
        }

        if (req.getFormationsJugement() != null && !req.getFormationsJugement().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("formationJugement").in(req.getFormationsJugement()));
        }

        if (req.getChambresSousSectionIds() != null && !req.getChambresSousSectionIds().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("chambreSousSection").get("id").in(req.getChambresSousSectionIds()));
        }

        if (req.getNumerosChambresReuniesIds() != null && !req.getNumerosChambresReuniesIds().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("numeroChambresReunies").get("id").in(req.getNumerosChambresReuniesIds()));
        }

        if (req.getApplicationTheorieChangementCirconstancesIds() != null
                && !req.getApplicationTheorieChangementCirconstancesIds().isEmpty()) {

            spec = spec.and((root, query, cb) ->
                    root.get("applicationTheorieChangementCirconstances")
                            .get("id")
                            .in(req.getApplicationTheorieChangementCirconstancesIds()));
        }

        if (req.getOriginesJuridictionnellesQpc() != null
                && !req.getOriginesJuridictionnellesQpc().isEmpty()) {

            spec = spec.and((root, query, cb) ->
                    root.get("origineJuridictionnelleQpc").in(req.getOriginesJuridictionnellesQpc()));
        }

        if (req.getMatieresIds() != null && !req.getMatieresIds().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("matiere").get("id").in(req.getMatieresIds()));
        }

        // droits/libertés via leur id (many-to-many)
        if (req.getDroitsLibertesIds() != null && !req.getDroitsLibertesIds().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);
                var join = root.join("droitsLibertes", JoinType.INNER);
                return join.get("id").in(req.getDroitsLibertesIds());
            });
        }

        return spec;
    }

}