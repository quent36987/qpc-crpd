package back.app.domain.service.excel.export;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExcelExportConfig<T> {

    private String sheetName;
    private List<ExcelColumn<T>> columns;

    /**
     * Nb max de lignes sur lesquelles appliquer la data validation.
     */
    @Builder.Default
    private int maxRowsForValidation = 10000;
}
