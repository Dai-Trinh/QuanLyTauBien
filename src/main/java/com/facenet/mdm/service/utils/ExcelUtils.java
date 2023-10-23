package com.facenet.mdm.service.utils;

import com.facenet.mdm.service.exception.CustomException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
//import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.http.HttpStatus;

public class ExcelUtils {

    private static final Logger log = LogManager.getLogger(ExcelUtils.class);

    public static Double getNumberCellValue(Cell cell) {
        if (isEmpty(cell)) return null;
        switch (cell.getCellType()) {
            case STRING:
                {
                    try {
                        return Double.parseDouble(cell.getStringCellValue());
                    } catch (Exception e) {
                        log.info(e.getMessage());
                        return null;
                    }
                }
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:
                        return Double.parseDouble(cell.getStringCellValue());
                    case NUMERIC:
                        return (double) Math.round(cell.getNumericCellValue());
                    default:
                        return Double.parseDouble(cell.getCellFormula());
                }
            default:
                return cell.getNumericCellValue();
        }
    }

    public static String getStringCellValue(Cell cell) {
        if (isEmpty(cell)) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return String.valueOf(Double.valueOf(cell.getNumericCellValue()));
        }
    }

    public static String getStringCellValueV2(Cell cell) {
        if (isEmpty(cell)) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return String.valueOf(new BigDecimal(cell.getNumericCellValue()));
        }
    }

    public static Integer getIntegerCellValue(Cell cell) {
        if (isEmpty(cell)) return null;
        switch (cell.getCellType()) {
            case STRING:
                {
                    try {
                        return Integer.parseInt(cell.getStringCellValue());
                    } catch (Exception e) {
                        log.info(e.getMessage());
                        return null;
                    }
                }
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:
                        return Integer.parseInt(cell.getStringCellValue());
                    case NUMERIC:
                        return (int) Math.round(cell.getNumericCellValue());
                    default:
                        return Integer.parseInt(cell.getCellFormula());
                }
            default:
                return Double.valueOf(cell.getNumericCellValue()).intValue();
        }
    }

    public static void validateRow(Row row, int start, int end) {
        for (int i = start; i <= end; i++) {
            if (isEmpty(row.getCell(i))) throw new CustomException(
                HttpStatus.BAD_REQUEST,
                "cell.must.not.empty",
                String.valueOf(i + 1),
                String.valueOf(row.getRowNum() + 1)
            );
        }
    }

    //    public static void validateRowCsv(CSVRecord csvRecord, int start, int end) {
    //        for (int i = start; i <= end; i++) {
    //            if (csvRecord.get(i).trim().isEmpty())
    //                throw new CustomException(HttpStatus.BAD_REQUEST, "cell.must.not.empty", String.valueOf(i + 1), String.valueOf(csvRecord.getRecordNumber() + 1));
    //        }
    //    }

    public static LocalDateTime getDateTimeCell(Cell cell) {
        if (isEmpty(cell)) return null;
        LocalDateTime dateTime;
        try {
            dateTime = cell.getLocalDateTimeCellValue();
        } catch (Exception e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "unparseable.date", getStringCellValue(cell));
        }
        return dateTime;
    }

    public static LocalDate getDateCell(Cell cell) {
        LocalDateTime date = getDateTimeCell(cell);
        return date == null ? null : date.toLocalDate();
    }

    public static boolean isEmpty(Cell cell) {
        return cell == null || cell.getCellType() == CellType.BLANK;
    }
}
