package back.app.presentation.controller;

import back.app.domain.entity.DroitLiberteDTO;
import back.app.domain.service.DroitLiberteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/droits-libertes", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Droits & Libertés", description = "Gestion des droits et libertés")
public class DroitLiberteController {

    private final DroitLiberteService droitLiberteService;

    @Operation(summary = "Get all droits & libertés")
    @GetMapping("/all")
    public List<DroitLiberteDTO> getAllDroitLiberte() {
        return droitLiberteService.getAll();
    }

    @Operation(summary = "Get a droit/liberté by ID")
    @GetMapping("/{id}")
    public DroitLiberteDTO getDroitLiberteById(@PathVariable Long id) {
        return droitLiberteService.getById(id);
    }

    @Operation(summary = "Create a new droit/liberté")
    @PreAuthorize("hasAuthority('DROITS_LIBERTES_MODIFIER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public DroitLiberteDTO createDroitLiberte(@RequestBody DroitLiberteDTO dto) {
        return droitLiberteService.create(dto);
    }

    @Operation(summary = "Update a droit/liberté")
    @PreAuthorize("hasAuthority('DROITS_LIBERTES_MODIFIER')")
    @PutMapping("/{id}")
    public DroitLiberteDTO updateDroitLiberte(@PathVariable Long id, @RequestBody DroitLiberteDTO dto) {
        return droitLiberteService.update(id, dto);
    }

    @Operation(summary = "Delete a droit/liberté")
    @PreAuthorize("hasAuthority('DROITS_LIBERTES_MODIFIER')")
    @DeleteMapping("/{id}")
    public void deleteDroitLiberte(@PathVariable Long id) {
        droitLiberteService.delete(id);
    }
}