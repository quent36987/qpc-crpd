package back.app.utils;


import back.app.data.model.setting.ESettingCategorie;
import back.app.data.model.setting.ESettingNom;
import back.app.data.model.setting.ESettingType;
import back.app.data.model.setting.SettingsModel;
import back.app.data.repository.interfaces.SettingRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitializeSettings {

    private final SettingRepository settingsRepository;

    @Getter
    @AllArgsConstructor
    private static class DefaultSetting {
        private final ESettingCategorie categorie;
        private final ESettingNom nomEnum;
        private final String nom;
        private final String description;
        private final String valeur;
        private final ESettingType type;
    }

    private static final List<DefaultSetting> DEFAULTS = List.of(

    );

    @Transactional
    public void init() {
        log.info("🔁 Vérification/initialisation des settings...");

        for (DefaultSetting def : DEFAULTS) {
            if (settingsRepository.existsByNomEnum(def.getNomEnum())) {
                log.debug("⏭️ Paramètre déjà présent, on skip: {}", def.getNom());
                continue;
            }
            SettingsModel created = newParam(
                    def.getCategorie(),
                    def.getNomEnum(),
                    def.getNom(),
                    def.getDescription(),
                    def.getValeur(),
                    def.getType()
            );
            settingsRepository.save(created);
            log.info("➕ Paramètre ajouté: {}", def.getNom());
        }

        log.info("✅ Initialisation des settings terminée.");
    }

    private SettingsModel newParam(ESettingCategorie categorie,
                                   ESettingNom nomEnum,
                                   String nom,
                                   String description,
                                   String valeur,
                                   ESettingType type) {
        SettingsModel s = new SettingsModel();
        s.setCategorie(categorie);
        s.setNomEnum(nomEnum);
        s.setNom(nom);
        s.setDescription(description == null ? "" : description);
        s.setValeur(valeur);
        s.setType(type);
        s.setUpdatedAt(LocalDateTime.now());
        return s;
    }
}


