package back.app.presentation.controller;
import back.app.data.model.qpc.DecisionQpcCcModel;
import back.app.domain.entity.DecisionQpcCcDTO;
import back.app.domain.entity.DecisionQpcCcRowDTO;
import back.app.domain.entity.PageDTO;
import back.app.domain.service.DecisionQpcCcService;
import back.app.presentation.request.DecisionQpcCcSearchRequest;
import back.app.utils.errors.RestError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/decisions-qpc-cc", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Décisions QPC CC", description = "Recherche des décisions QPC du Conseil constitutionnel")
public class DecisionQpcCcController {

    private final DecisionQpcCcService decisionQpcCcService;

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

    @Operation(summary = "Get a décision QPC CC by ID")
    @GetMapping("/{id}")
    public DecisionQpcCcDTO getDecisionsById(@PathVariable Long id) {
        return decisionQpcCcService.getById(id);
    }

    // ------------------------------------------------------------------------
    //                          SPEC HELPERS
    // ------------------------------------------------------------------------
    private Specification<DecisionQpcCcModel> buildSearchSpecification(DecisionQpcCcSearchRequest req) {
        Specification<DecisionQpcCcModel> spec = Specification.where(null);

        if (req.getOrigineQpcId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("origineQpc").get("id"), req.getOrigineQpcId()));
        }

        if (req.getQualiteDemandeurId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("qualiteDemandeur").get("id"), req.getQualiteDemandeurId()));
        }

        if (req.getTypeDispositionLegislativeId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("typeDispositionLegislative").get("id"), req.getTypeDispositionLegislativeId()));
        }

        if (req.getMatiereId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("matiere").get("id"), req.getMatiereId()));
        }

        if (req.getDispositifDecisionCcId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("dispositifDecisionCc").get("id"), req.getDispositifDecisionCcId()));
        }

        if (req.getTraitementEffetsPassesId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("traitementEffetsPasses").get("id"), req.getTraitementEffetsPassesId()));
        }

        if (req.getOraliteId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("oralite").get("id"), req.getOraliteId()));
        }

        if (req.getQualiteTiersInterventionId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("qualiteTiersIntervention").get("id"), req.getQualiteTiersInterventionId()));
        }

        if (req.getReserveIncompetenceConseilId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("reserveIncompetenceConseil").get("id"), req.getReserveIncompetenceConseilId()));
        }

        return spec;
    }
}