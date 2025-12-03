package back.app.presentation.controller;

import back.app.domain.entity.ListeDeroulanteDTO;
import back.app.domain.service.ListeDeroulanteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/listes-deroulantes", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Listes déroulantes", description = "Gestion des listes déroulantes")
public class ListeDeroulanteController {

    private final ListeDeroulanteService listeDeroulanteService;

    @Operation(summary = "Get all listes déroulantes")
    @GetMapping("/all")
    public List<ListeDeroulanteDTO> getAllListeDeroulante() {
        return listeDeroulanteService.getAllListesDeroulantes();
    }

    @Operation(summary = "Get a liste déroulante by ID")
    @GetMapping("/{id}")
    public ListeDeroulanteDTO getListeDeroulanteById(@PathVariable Long id) {
        return listeDeroulanteService.getById(id);
    }

    @Operation(summary = "Create a new liste déroulante")
    @PreAuthorize("hasAuthority('LISTES_DEROUlANTES_MODIFIER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ListeDeroulanteDTO createListeDeroulante(@RequestBody ListeDeroulanteDTO dto) {
        return listeDeroulanteService.create(dto);
    }

    @Operation(summary = "Update a liste déroulante")
    @PreAuthorize("hasAuthority('LISTES_DEROUlANTES_MODIFIER')")
    @PutMapping("/{id}")
    public ListeDeroulanteDTO updateListeDeroulante(@PathVariable Long id, @RequestBody ListeDeroulanteDTO dto) {
        return listeDeroulanteService.update(id, dto);
    }

    @Operation(summary = "Delete a liste déroulante")
    @PreAuthorize("hasAuthority('LISTES_DEROUlANTES_MODIFIER')")
    @DeleteMapping("/{id}")
    public void deleteListeDeroulante(@PathVariable Long id) {
        listeDeroulanteService.delete(id);
    }
}