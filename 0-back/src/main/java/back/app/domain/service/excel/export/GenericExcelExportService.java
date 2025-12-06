package back.app.domain.service.excel.export;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GenericExcelExportService {

    public <T> byte[] export(ExcelExportConfig<T> cfg, List<T> data) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet(cfg.getSheetName());
            // On fige les 2 premières lignes (groupes + headers)
            sheet.createFreezePane(0, 2);

            // Styles
            CellStyle groupStyle = createGroupStyle(wb);
            Map<Short, CellStyle> groupStylesByColor = new HashMap<>();
            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle textStyle = createTextStyle(wb);
            CellStyle dateStyle = createDateStyle(wb);
            CellStyle dateTimeStyle = createDateTimeStyle(wb);

            // Lignes d’en-tête
            Row groupRow = sheet.createRow(0);
            Row headerRow = sheet.createRow(1);

            // Création des cellules d’entête + gestion des groupes
            List<ExcelColumn<T>> columns = cfg.getColumns();
            String currentGroup = null;
            short currentGroupColor = 0;
            int groupStartCol = -1;

            for (int c = 0; c < columns.size(); c++) {
                ExcelColumn<T> col = columns.get(c);

                // Header (ligne 2)
                Cell headerCell = headerRow.createCell(c);
                headerCell.setCellValue(col.getHeader());
                headerCell.setCellStyle(headerStyle);

                // Groupes (ligne 1)
                String groupName = col.getGroupName();
                if (groupName != null && !groupName.isBlank()) {
                    if (!groupName.equals(currentGroup)) {
                        // On clôt le groupe précédent
                        if (currentGroup != null) {
                            sheet.addMergedRegion(new CellRangeAddress(
                                    0, 0,
                                    groupStartCol, c - 1
                            ));
                        }
                        currentGroup = groupName;
                        currentGroupColor = col.getGroupColorIndex();
                        groupStartCol = c;

                        Cell groupCell = groupRow.createCell(c);
                        groupCell.setCellValue(groupName);
                        groupCell.setCellStyle(
                                groupStylesByColor.computeIfAbsent(
                                        currentGroupColor,
                                        color -> cloneGroupStyleWithColor(wb, groupStyle, color)
                                )
                        );
                    } else {
                        // Même groupe => on crée une cellule vide avec le même style
                        Cell groupCell = groupRow.createCell(c);
                        groupCell.setCellStyle(
                                groupStylesByColor.computeIfAbsent(
                                        currentGroupColor,
                                        color -> cloneGroupStyleWithColor(wb, groupStyle, color)
                                )
                        );
                    }
                } else {
                    // Pas de groupe pour cette colonne => cellule vide
                    groupRow.createCell(c).setCellStyle(groupStyle);
                }

                // Largeur de colonne
                sheet.setColumnWidth(c, col.getWidth() * 256);
            }

            // Clôture du dernier groupe si nécessaire
            if (currentGroup != null) {
                sheet.addMergedRegion(new CellRangeAddress(
                        0, 0,
                        groupStartCol, columns.size() - 1
                ));
            }

            // Données
            int rowIndex = 2;
            for (T item : data) {
                Row row = sheet.createRow(rowIndex++);
                for (int c = 0; c < columns.size(); c++) {
                    ExcelColumn<T> col = columns.get(c);
                    Cell cell = row.createCell(c);
                    Object value = col.getValueExtractor().apply(item);
                    writeValue(cell, value, textStyle, dateStyle, dateTimeStyle);
                }
            }

            // Data validation (listes déroulantes)
            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
            for (int c = 0; c < columns.size(); c++) {
                ExcelColumn<T> col = columns.get(c);
                String[] allowed = col.getAllowedValues();
                if (allowed != null && allowed.length > 0) {
                    DataValidationConstraint dvConstraint =
                            dvHelper.createExplicitListConstraint(allowed);

                    CellRangeAddressList addrList = new CellRangeAddressList(
                            2,
                            cfg.getMaxRowsForValidation(),
                            c,
                            c
                    );

                    DataValidation validation = dvHelper.createValidation(dvConstraint, addrList);
                    validation.setSuppressDropDownArrow(false);
                    validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                    validation.createErrorBox(
                            "Valeur invalide",
                            "Merci de choisir une valeur dans la liste déroulante."
                    );
                    validation.setShowErrorBox(true);

                    sheet.addValidationData(validation);
                }
            }

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l’export Excel", e);
        }
    }

    // ----------------------------------------------------------------------
    // Styles
    // ----------------------------------------------------------------------

    private CellStyle createGroupStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        return style;
    }

    private CellStyle cloneGroupStyleWithColor(Workbook wb, CellStyle base, short colorIndex) {
        CellStyle clone = wb.createCellStyle();
        clone.cloneStyleFrom(base);
        clone.setFillForegroundColor(colorIndex);
        return clone;
    }

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTextStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.TOP);
        style.setWrapText(true);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDateStyle(Workbook wb) {
        CellStyle style = createTextStyle(wb);
        CreationHelper helper = wb.getCreationHelper();
        style.setDataFormat(helper.createDataFormat().getFormat("dd/MM/yyyy"));
        return style;
    }

    private CellStyle createDateTimeStyle(Workbook wb) {
        CellStyle style = createTextStyle(wb);
        CreationHelper helper = wb.getCreationHelper();
        style.setDataFormat(helper.createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
        return style;
    }

    // ----------------------------------------------------------------------
    // Helpers écriture des valeurs
    // ----------------------------------------------------------------------

    private void writeValue(Cell cell,
                            Object value,
                            CellStyle textStyle,
                            CellStyle dateStyle,
                            CellStyle dateTimeStyle) {
        if (value == null) {
            cell.setCellStyle(textStyle);
            cell.setBlank();
            return;
        }

        if (value instanceof Number number) {
            cell.setCellStyle(textStyle);
            cell.setCellValue(number.doubleValue());
        } else if (value instanceof LocalDate ld) {
            cell.setCellStyle(dateStyle);
            cell.setCellValue(java.sql.Date.valueOf(ld));
        } else if (value instanceof LocalDateTime ldt) {
            cell.setCellStyle(dateTimeStyle);
            cell.setCellValue(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()));
        } else if (value instanceof Enum<?> e) {
            cell.setCellStyle(textStyle);
            cell.setCellValue(e.name());
        } else {
            cell.setCellStyle(textStyle);
            cell.setCellValue(value.toString());
        }
    }
}
