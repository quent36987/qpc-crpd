package back.app.domain.service.excel.upload;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Data
@Builder
public class ExcelImportConfig<T> {


    /**
     * Nom de l'onglet. null => premier onglet.
     */
    private String sheetName;

    /**
     * Index (0-based) de la ligne qui contient les headers (ligne 2 Excel).
     * Avec ton fichier : ligne 1 = groupes, ligne 2 = headers, ligne 3 = data.
     * => headerRowIndex = 1, dataStartRowIndex = 2
     */
    @Builder.Default
    private int headerRowIndex = 1;

    /**
     * Index (0-based) de la première ligne de données.
     */
    @Builder.Default
    private int dataStartRowIndex = 2;

    /**
     * Fabrique d'instance (new T()) pour chaque ligne.
     */
    private Supplier<T> targetSupplier;

    /**
     * Colonnes mappées par header.
     */
    private List<ExcelImportColumn<T>> columns;

    /**
     * Hook de post-traitement par ligne (optionnel),
     * on reçoit l'entité T + toutes les valeurs brutes (header -> string).
     * Utile pour les droits/libertés, etc.
     */
    private BiConsumer<T, Map<String, String>> postProcessor;
}

