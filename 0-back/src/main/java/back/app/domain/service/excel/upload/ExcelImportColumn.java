package back.app.domain.service.excel.upload;

import lombok.Builder;
import lombok.Data;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Data
@Builder
public class ExcelImportColumn<T> {

    /**
     * Valeur exacte du header (ligne 2).
     */
    private String header;

    /**
     * Fonction de parsing : String Excel -> objet typé.
     */
    private Function<String, ?> parser;

    /**
     * Setter brut : (target, valueParsed).
     * On fera un helper pour cacher le cast sauvage.
     */
    private BiConsumer<T, Object> rawSetter;

    // Helper statique pour créer des colonnes typées proprement
    public static <T, V> ExcelImportColumn<T> of(
            String header,
            Function<String, V> parser,
            BiConsumer<T, V> setter
    ) {
        return ExcelImportColumn.<T>builder()
                .header(header)
                .parser(parser)
                .rawSetter((t, v) -> setter.accept(t, (V) v))
                .build();
    }
}

