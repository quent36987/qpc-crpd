package back.app.domain.service.excel.export;

import lombok.Builder;
import lombok.Data;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.function.Function;

@Data
@Builder
public class ExcelColumn<T> {

    /**
     * Label affiché sur la 2ème ligne (ligne des colonnes).
     */
    private String header;

    /**
     * Nom du groupe (catégorie) affiché sur la 1ère ligne.
     * Si null => pas de groupe.
     */
    private String groupName;

    /**
     * Couleur de fond pour la cellule de groupe.
     * (utiliser IndexedColors.XYZ.getIndex())
     */
    @Builder.Default
    private short groupColorIndex = IndexedColors.GREY_25_PERCENT.getIndex();

    /**
     * Largeur de colonne en "caractères" (sera multiplié par 256 par POI).
     */
    @Builder.Default
    private int width = 20;

    /**
     * Fonction qui extrait la valeur à afficher à partir du DTO.
     */
    private Function<T, Object> valueExtractor;

    /**
     * Liste de valeurs autorisées (pour créer une liste déroulante Excel).
     * Si null ou vide => pas de data validation.
     */
    private String[] allowedValues;
}
