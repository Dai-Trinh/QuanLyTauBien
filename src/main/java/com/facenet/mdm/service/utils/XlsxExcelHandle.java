package com.facenet.mdm.service.utils;

import static org.apache.poi.ss.usermodel.CellType.STRING;

import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.service.dto.*;
import com.facenet.mdm.service.exception.CustomException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import java.util.*;
import java.util.ArrayList;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class XlsxExcelHandle {

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;


    public List<KeyDictionaryDTO> readColumnFromExcel(InputStream file, Integer entityType) throws IOException, ParseException {
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        // Lấy ra sheet đầu tiên từ workbook
        Sheet sheet = workbook.getSheetAt(0);
        List<KeyDictionaryDTO> result = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            Cell firstCell = row.getCell(0);
            //            if (firstCell == null || firstCell.getCellType() == CellType.BLANK) continue;
            for (int i = 0; i < 4; i++) {
                if (ExcelUtils.isEmpty(row.getCell(i)) && i != 3) throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "cell.must.not.empty",
                    String.valueOf(i + 1),
                    String.valueOf(row.getRowNum() + 1)
                );
            }
            //            for (int i = 1; i <= 4; i++) {
            //                Utils.validateSpecialCharacters(ExcelUtils.getStringCellValue(row.getCell(i)));
            //            }

            KeyDictionaryDTO keyDictionaryDTO = new KeyDictionaryDTO();
            // số thứ tự
            //            if (
            //                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(0))) || ExcelUtils.getStringCellValue(row.getCell(0)) == null
            //            ) {
            //                keyDictionaryDTO.setEntryIndex(null);
            //            } else if (row.getCell(0).getCellType() == CellType.NUMERIC) {
            //                keyDictionaryDTO.setEntryIndex(ExcelUtils.getIntegerCellValue(row.getCell(0)));
            //            } else {
            //                throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.datatype", ExcelUtils.getStringCellValue(row.getCell(0)));
            //            }
            // tên cột
            if (
                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(0))) ||
                ExcelUtils.getStringCellValue(row.getCell(0)) == null ||
                row.getCell(0).getCellType() != STRING
            ) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.datatype", ExcelUtils.getStringCellValue(row.getCell(0)));
            } else {
                keyDictionaryDTO.setKeyTitle(ExcelUtils.getStringCellValue(row.getCell(0)));
            }
            // loại dữ liệu
            if (row.getCell(1).getCellType() != STRING) throw new CustomException(
                HttpStatus.BAD_REQUEST,
                "invalid.datatype",
                ExcelUtils.getStringCellValue(row.getCell(1))
            );
            switch (ExcelUtils.getStringCellValue(row.getCell(1)).toLowerCase()) {
                case "integer":
                    keyDictionaryDTO.setDataType(1);
                    break;
                case "float":
                    keyDictionaryDTO.setDataType(2);
                    break;
                case "string":
                    keyDictionaryDTO.setDataType(3);
                    break;
                case "json":
                    keyDictionaryDTO.setDataType(4);
                    break;
                case "date":
                    keyDictionaryDTO.setDataType(5);
                    break;
                case "boolean":
                    keyDictionaryDTO.setDataType(6);
                    break;
                default:
                    throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.datatype", ExcelUtils.getStringCellValue(row.getCell(2)));
            }
            // bắt buộc
            if (row.getCell(2).getCellType() != STRING) throw new CustomException(
                HttpStatus.BAD_REQUEST,
                "invalid.datatype",
                ExcelUtils.getStringCellValue(row.getCell(2))
            );
            if (ExcelUtils.getStringCellValue(row.getCell(2)).trim().toLowerCase().equalsIgnoreCase("Bắt buộc".trim())) {
                keyDictionaryDTO.setIsRequired(true);
            } else if (ExcelUtils.getStringCellValue(row.getCell(2)).trim().toLowerCase().equalsIgnoreCase("Không bắt buộc".trim())) {
                keyDictionaryDTO.setIsRequired(false);
            } else {
                throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.datatype", ExcelUtils.getStringCellValue(row.getCell(2)));
            }
            // trạng thái
            if (
                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(3))) && row.getCell(3).getCellType() != STRING
            ) throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.datatype", ExcelUtils.getStringCellValue(row.getCell(3)));
            //System.err.println(ExcelUtils.getStringCellValue(row.getCell(3)).trim().toLowerCase());
            if (
                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(3))) ||
                ExcelUtils.getStringCellValue(row.getCell(3)).trim().toLowerCase().equalsIgnoreCase("Hiển thị".trim())
            ) {
                keyDictionaryDTO.setCheck(true);
            } else if (ExcelUtils.getStringCellValue(row.getCell(3)).trim().toLowerCase().equalsIgnoreCase("Không hiển thị".trim())) {
                keyDictionaryDTO.setCheck(false);
            } else {
                throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.datatype", ExcelUtils.getStringCellValue(row.getCell(4)));
            }
            //keyName
            keyDictionaryDTO.setKeyName(String.valueOf(UUID.randomUUID()));
            //entityType
//            switch (entityType) {
//                case Contants.EntityType.PRODUCTIONSTAGE:
//                    keyDictionaryDTO.setEntityType(Contants.EntityType.PRODUCTIONSTAGE);
//                    break;
//                case Contants.EntityType.VENDOR:
//                    keyDictionaryDTO.setEntityType(Contants.EntityType.VENDOR);
//                    break;
//                case Contants.EntityType.JOB:
//                    keyDictionaryDTO.setEntityType(Contants.EntityType.JOB);
//                    break;
//                case Contants.EntityType.ERROR:
//                    keyDictionaryDTO.setEntityType(Contants.EntityType.ERROR);
//                    break;
//                case Contants.EntityType.ERRORGROUP:
//                    keyDictionaryDTO.setEntityType(Contants.EntityType.ERRORGROUP);
//                    break;
//                case Contants.EntityType.MACHINE:
//                    keyDictionaryDTO.setEntityType(Contants.EntityType.MACHINE);
//                    break;
//                case Contants.EntityType.PRODUCTION_LINE:
//                    keyDictionaryDTO.setEntityType(Contants.EntityType.PRODUCTION_LINE);
//                    break;
//                case Contants.EntityType.BTP:
//                    keyDictionaryDTO.setEntityType(Contants.EntityType.BTP);
//                    break;
//                case Contants.EntityType.TP:
//                    keyDictionaryDTO.setEntityType(Contants.EntityType.TP);
//                    break;
//                case Contants.EntityType.NVL:
//                    keyDictionaryDTO.setEntityType(Contants.EntityType.NVL);
//                    break;
//                case Contants.EntityType.EMPLOYEE:
//                    keyDictionaryDTO.setEntityType(Contants.EntityType.EMPLOYEE);
//                    break;
//                case Contants.EntityType.TEAM_GROUP:
//                    keyDictionaryDTO.setEntityType(Contants.EntityType.TEAM_GROUP);
//                    break;
//                case Contants.EntityType.MERCHANDISE_GROUP:
//                    keyDictionaryDTO.setEntityType(Contants.EntityType.MERCHANDISE_GROUP);
//                    break;
//                case Contants.EntityType.CUSTOMER:
//                    keyDictionaryDTO.setEntityType(Contants.EntityType.CUSTOMER);
//                    break;
//                default:
//                    throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.datatype", entityType.toString());
//            }
            result.add(keyDictionaryDTO);
        }
        return result;
    }





}
