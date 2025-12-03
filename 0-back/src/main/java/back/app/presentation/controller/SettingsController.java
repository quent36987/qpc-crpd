package back.app.presentation.controller;

import back.app.domain.entity.SettingsDTO;
import back.app.domain.service.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/settings", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Settings", description = "Application settings operations")
public class SettingsController extends BaseController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    // ===========================
    //            GET
    // ===========================

    @Operation(summary = "Get all settings")
    @GetMapping
    public List<SettingsDTO> getAll() {
        return settingsService.getAll();
    }

    // ===========================
    //        POST / PUT
    // ===========================

    @Operation(summary = "Update a setting by name (only value is used)")
    @PreAuthorize("hasAuthority('SETTING_MODIFIER')")
    @PutMapping
    public SettingsDTO updateOne(@Valid @RequestBody SettingsDTO setting) {
        // setting.nom is mandatory at service level; only setting.valeur is updated
        return settingsService.updateOne(setting, getUserId());
    }
}
