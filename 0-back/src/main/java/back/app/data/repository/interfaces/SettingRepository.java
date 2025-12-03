package back.app.data.repository.interfaces;


import back.app.data.model.setting.ESettingNom;
import back.app.data.model.setting.SettingsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<SettingsModel, Long> {
    Optional<SettingsModel> findByNomEnum(ESettingNom nom);
    List<SettingsModel> findByNomIn(List<String> noms);


    boolean existsByNomEnum(ESettingNom nom);

}
