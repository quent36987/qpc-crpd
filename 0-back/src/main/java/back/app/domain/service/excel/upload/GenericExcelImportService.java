package back.app.domain.service.excel.upload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenericExcelImportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

            log.info("Import Excel terminé : {} lignes importées depuis la feuille '{}'", result.size(), sheet.getSheetName());

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

    // ----------------------------------------------------------------------
    // Helpers de parsing
    // ----------------------------------------------------------------------

    public static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        // deux cas possibles: "2024-01-01T00:00" depuis POI ou "01/01/2024" direct
        s = s.trim();
        try {
            if (s.contains("T")) {
                return LocalDate.parse(s.substring(0, 10)); // "yyyy-MM-dd..."
            }
            return LocalDate.parse(s, DATE_FMT);
        } catch (Exception e) {
            return null;
        }
    }

    public static String str(String raw) {
        return raw == null ? null : raw.trim();
    }

    public static Integer parseInteger(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Boolean parseBoolean(String s) {
        if (s == null || s.isBlank()) return null;
        String v = s.trim().toLowerCase(Locale.ROOT);
        return switch (v) {
            case "1", "true", "vrai", "oui", "o", "x" -> Boolean.TRUE;
            case "0", "false", "faux", "non", "n" -> Boolean.FALSE;
            default -> null;
        };
    }
}

