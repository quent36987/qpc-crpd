package back.app.domain.service.excel.upload;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GenericExcelImportService {

    public <T> List<T> importFromXls(InputStream in, ExcelImportConfig<T> config) {
        try (Workbook wb = WorkbookFactory.create(in)) {

            Sheet sheet = resolveSheet(wb, config.getSheetName());
            if (sheet == null) {
                throw new IllegalArgumentException("Feuille Excel introuvable: " + config.getSheetName());
            }

            Row headerRow = sheet.getRow(config.getHeaderRowIndex());
            if (headerRow == null) {
                throw new IllegalStateException("Ligne de header introuvable (index " + config.getHeaderRowIndex() + ")");
            }

            // headerText -> columnIndex
            Map<String, Integer> headerIndex = new HashMap<>();
            for (int c = 0; c < headerRow.getLastCellNum(); c++) {
                Cell cell = headerRow.getCell(c);
                String header = getCellString(cell);
                if (header != null && !header.isBlank()) {
                    headerIndex.put(header.trim(), c);
                }
            }

            // header -> ExcelImportColumn
            Map<String, ExcelImportColumn<T>> columnByHeader = new HashMap<>();
            for (ExcelImportColumn<T> col : config.getColumns()) {
                columnByHeader.put(col.getHeader(), col);
            }

            List<T> result = new ArrayList<>();

            int lastRowNum = sheet.getLastRowNum();
            for (int r = config.getDataStartRowIndex(); r <= lastRowNum; r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }

                // test ligne vide (facultatif : on peut sortir si plusieurs vides d'affilée)
                if (isRowEmpty(row)) {
                    continue;
                }

                T target = config.getTargetSupplier().get();
                Map<String, String> rawValues = new HashMap<>();

                // On parcourt tous les headers connus
                for (Map.Entry<String, Integer> entry : headerIndex.entrySet()) {
                    String header = entry.getKey();
                    int colIndex = entry.getValue();
                    Cell cell = row.getCell(colIndex);
                    String raw = getCellString(cell);
                    raw = raw != null ? raw.trim() : null;
                    rawValues.put(header, raw);

                    ExcelImportColumn<T> colCfg = columnByHeader.get(header);
                    if (colCfg == null) {
                        // colonne non mappée : on ignore, mais elle reste dispo dans rawValues pour le postProcessor
                        continue;
                    }

                    Object parsed = colCfg.getParser().apply(raw);
                    colCfg.getRawSetter().accept(target, parsed);
                }

                // Post-traitement éventuel (ex : droits/libertés)
                if (config.getPostProcessor() != null) {
                    config.getPostProcessor().accept(target, rawValues);
                }

                result.add(target);
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'import Excel", e);
        }
    }

    // ----------------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------------

    private Sheet resolveSheet(Workbook wb, String name) {
        if (name == null || name.isBlank()) {
            return wb.getNumberOfSheets() > 0 ? wb.getSheetAt(0) : null;
        }
        Sheet s = wb.getSheet(name);
        if (s != null) return s;
        // tolérance : on tente aussi sans sensible à la casse
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            if (wb.getSheetAt(i).getSheetName().equalsIgnoreCase(name)) {
                return wb.getSheetAt(i);
            }
        }
        return null;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String v = getCellString(cell);
                if (v != null && !v.isBlank()) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getCellString(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    // on laisse la date à parser côté service si besoin
                    yield cell.getLocalDateTimeCellValue().toString();
                } else {
                    double d = cell.getNumericCellValue();
                    // éviter les ".0" moches pour les ints
                    if (d == (long) d) {
                        yield Long.toString((long) d);
                    } else {
                        yield Double.toString(d);
                    }
                }
            }
            case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    yield Double.toString(cell.getNumericCellValue());
                }
            }
            default -> null;
        };
    }
}

