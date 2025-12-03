package back.app.domain.service;

import back.app.data.model.setting.ESettingNom;
import back.app.data.model.setting.SettingsModel;
import back.app.data.model.user.UserModel;
import back.app.data.repository.interfaces.SettingRepository;
import back.app.data.repository.interfaces.UserRepository;
import back.app.domain.entity.SettingsDTO;
import back.app.domain.mapper.SettingsMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class SettingsService {

    private final SettingRepository settingsRepository;
    private final UserRepository userRepository;
    private final SettingsMapper settingsMapper;

    @Transactional(readOnly = true)
    public List<SettingsDTO> getAll() {
        return settingsMapper.toDTOList(settingsRepository.findAll());
    }

    // --- UPDATE ONE ---
    public SettingsDTO updateOne(SettingsDTO dto, Long userId) {
        if (dto == null || dto.getNomEnum() == null) {
            throw new IllegalArgumentException("Le champ 'nom' est requis pour mettre à jour un setting.");
        }

        SettingsModel entity = settingsRepository.findByNomEnum(dto.getNomEnum())
                .orElseThrow(() -> new NoSuchElementException("Aucun setting trouvé avec nom=" + dto.getNom()));

        if (dto.getValeur() != null && !Objects.equals(entity.getValeur(), dto.getValeur())) {
            entity.setValeur(dto.getValeur());
            setAudit(entity, userId);
            entity = settingsRepository.save(entity);
            log.info("Setting '{}' mis à jour par userId={}", entity.getNomEnum(), userId);
        }

        return settingsMapper.toDTO(entity);
    }

    // --- Helpers ---
    private void setAudit(SettingsModel entity, Long userId) {
        entity.setUpdatedAt(LocalDateTime.now());
        if (userId != null) {
            UserModel user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable id=" + userId));
            entity.setUpdatedBy(user);
        } else {
            entity.setUpdatedBy(null);
        }
    }

    @Transactional(readOnly = true)
    public <T> T getSettingValue(ESettingNom nom, Class<T> type){
        SettingsModel setting = settingsRepository.findByNomEnum(nom)
                .orElseThrow(() -> new NoSuchElementException("Aucun setting trouvé avec nom=" + nom));

        String valeur = setting.getValeur();

        if (type == String.class) {
            return type.cast(valeur);
        }
        if (type == Integer.class) {
            return type.cast(Integer.valueOf(valeur));
        }
        if (type == Boolean.class) {
            return type.cast(Boolean.valueOf(valeur));
        }
        if (type == Double.class) {
            return type.cast(Double.valueOf(valeur));
        }
        if (type == LocalDateTime.class) {
            return type.cast(LocalDateTime.parse(valeur));
        }
        if (type == LocalDate.class) {
            return type.cast(LocalDate.parse(valeur));
        }

        throw new IllegalArgumentException("Type non supporté: " + type);
    }
}
