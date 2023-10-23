package com.facenet.mdm.service.utils;

import static org.apache.poi.ss.usermodel.CellType.STRING;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.*;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.service.dto.*;
import com.facenet.mdm.service.dto.excel.MachineExcel;
import com.facenet.mdm.service.dto.excel.ProductionLineExcel;
import com.facenet.mdm.service.dto.excel.StageJobExcel;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.model.MqqPriceExcelModel;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class XlsxExcelHandle {

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @Autowired
    MqqPriceRepository mqqPriceRepository;

    @Autowired
    LeadTimeRepository leadTimeRepository;

    Logger logger = LoggerFactory.getLogger(XlsxExcelHandle.class);
    private final MachineRepository machineRepository;
    private final MachineTypeRepository machineTypeRepository;
    private final ProductionLineTypeRepository productionLineTypeRepository;

    public XlsxExcelHandle(
        MachineRepository machineRepository,
        MachineTypeRepository machineTypeRepository,
        ProductionLineTypeRepository productionLineTypeRepository
    ) {
        this.machineRepository = machineRepository;
        this.machineTypeRepository = machineTypeRepository;
        this.productionLineTypeRepository = productionLineTypeRepository;
    }

    public StageJobExcel readStageAndJobFromExcel(InputStream file) throws IOException, ParseException {
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        // Lấy ra sheet đầu tiên từ workbook
        Sheet sheet = workbook.getSheetAt(0);
        StageJobExcel stageJobExcel = new StageJobExcel();
        List<ProductionStageDTO> resultStage = new ArrayList<>();
        List<JobDTO> resultJob = new ArrayList<>();

        ProductionStageDTO productionStageDTOEqual = new ProductionStageDTO();
        //danh sách cột của công đoạn(đã sắp xếp)
        List<ColumnPropertyEntity> stageColumns = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.PRODUCTIONSTAGE
        );
        // danh sách cột của job
        List<ColumnPropertyEntity> jobColumns = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(Contants.EntityType.JOB);
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            //validate
            //            for (int i = 0; i <= stageColumns.size() + jobColumns.size(); i++) {
            //                if (ExcelUtils.getStringCellValue(row.getCell(i)) == null || ExcelUtils.isEmpty(row.getCell(i))) continue;
            //                Utils.validateSpecialCharacters(ExcelUtils.getStringCellValue(row.getCell(i)));
            //            }

            Integer breakIndex = 0;
            ProductionStageDTO productionStageDTO = new ProductionStageDTO();
            for (int i = 0; i < stageColumns.size(); i++) {
                //validate requirements columns
                if (
                    stageColumns.get(i).getIsRequired() == true &&
                    (
                        StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                        ExcelUtils.getStringCellValue(row.getCell(i)) == null
                    )
                ) {
                    if (!stageColumns.get(i).getKeyName().equalsIgnoreCase("status")) {
                        throw new CustomException(
                            HttpStatus.BAD_REQUEST,
                            "cell.must.not.empty",
                            String.valueOf(i + 1),
                            String.valueOf(row.getRowNum() + 1)
                        );
                    }
                }
                breakIndex = i;
                switch (stageColumns.get(i).getKeyName()) {
                    case "productionStageCode":
                        productionStageDTO.setProductionStageCode(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "productionStageName":
                        productionStageDTO.setProductionStageName(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "status":
                        {
                            //                        System.err.println(row.getRowNum()+ 1 + "---"+ i+1  +"---" +ExcelUtils.getStringCellValue(row.getCell(i)));
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null ||
                                ExcelUtils.getStringCellValue(row.getCell(i)).trim().toLowerCase().equalsIgnoreCase("Hoạt động".trim())
                            ) {
                                productionStageDTO.setStatus(1);
                            } else if (
                                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) &&
                                ExcelUtils.getStringCellValue(row.getCell(i)) != null &&
                                ExcelUtils
                                    .getStringCellValue(row.getCell(i))
                                    .trim()
                                    .toLowerCase()
                                    .equalsIgnoreCase("Ngừng hoạt động".trim())
                            ) {
                                productionStageDTO.setStatus(0);
                            } else throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                            break;
                        }
                    default:
                        {
                            if (!StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i)))) {
                                //set thuộc tính vào map
                                //                            System.err.println(row.getRowNum()+ 1 + "---"+ i+1  +"---" +ExcelUtils.getStringCellValue(row.getCell(i)));
                                productionStageDTO.setStageMap(
                                    stageColumns.get(i).getKeyName(),
                                    ExcelUtils.getStringCellValue(row.getCell(i))
                                );
                                //                            System.err.println(keyStageDTOList.get(i).getKeyName()+"----" + ExcelUtils.getStringCellValue(row.getCell(i)));
                            }
                            break;
                        }
                }
            }
            //chuyển sang lấy thông tin của job
            breakIndex++;
            System.err.println(breakIndex);
            if (row.getCell(breakIndex) != null && !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(breakIndex)))) {
                JobDTO jobDTO = new JobDTO();
                jobDTO.setProductionStageCode(productionStageDTO.getProductionStageCode());
                for (int i = breakIndex; i < jobColumns.size() + breakIndex; i++) {
                    //validate requirements columns
                    if (
                        jobColumns.get(i - breakIndex).getIsRequired() == true &&
                        (
                            StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                            ExcelUtils.getStringCellValue(row.getCell(i)) == null
                        )
                    ) {
                        if (
                            !jobColumns.get(i - breakIndex).getKeyName().equalsIgnoreCase("status") &&
                            !jobColumns.get(i - breakIndex).getKeyName().equalsIgnoreCase("level")
                        ) {
                            throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "cell.must.not.empty",
                                String.valueOf(i + 1),
                                String.valueOf(row.getRowNum() + 1)
                            );
                        }
                    }
                    //                    System.err.println(jobColumns.get(i-breakIndex).getKeyTitle());
                    switch (jobColumns.get(i - breakIndex).getKeyName()) {
                        case "jobCode":
                            jobDTO.setJobCode(ExcelUtils.getStringCellValue(row.getCell(i)));
                            break;
                        case "jobName":
                            jobDTO.setJobName(ExcelUtils.getStringCellValue(row.getCell(i)));
                            break;
                        case "level":
                            if (!StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i)))) {
                                jobDTO.setProductionStageCode(ExcelUtils.getStringCellValue(row.getCell(i)));
                            } else {
                                jobDTO.setProductionStageCode(productionStageDTO.getProductionStageCode());
                            }
                            break;
                        case "status":
                            {
                                //                            System.err.println(row.getRowNum()+ 1 + "---"+ i+1  +"---" +ExcelUtils.getStringCellValue(row.getCell(i)));
                                if (
                                    StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                    ExcelUtils.getStringCellValue(row.getCell(i)) == null ||
                                    ExcelUtils.getStringCellValue(row.getCell(i)).trim().toLowerCase().equalsIgnoreCase("Hoạt động".trim())
                                ) {
                                    jobDTO.setStatus(1);
                                } else if (
                                    !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) &&
                                    ExcelUtils.getStringCellValue(row.getCell(i)) != null &&
                                    ExcelUtils
                                        .getStringCellValue(row.getCell(i))
                                        .trim()
                                        .toLowerCase()
                                        .equalsIgnoreCase("Ngừng hoạt động".trim())
                                ) {
                                    jobDTO.setStatus(0);
                                } else throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                                break;
                            }
                        default:
                            {
                                if (!StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i)))) {
                                    //                                System.err.println(row.getRowNum()+ 1 + "---"+ i+1  +"---" +ExcelUtils.getStringCellValue(row.getCell(i)));
                                    jobDTO.setJobMap(
                                        jobColumns.get(i - breakIndex).getKeyName(),
                                        ExcelUtils.getStringCellValue(row.getCell(i))
                                    );
                                    //                                System.err.println(jobColumns.get(i - breakIndex).getKeyName()+"---"+ExcelUtils.getStringCellValue(row.getCell(i)));
                                }
                            }
                    }
                }
                resultJob.add(jobDTO);
            }

            if (!productionStageDTO.getProductionStageCode().equals(productionStageDTOEqual.getProductionStageCode())) {
                resultStage.add(productionStageDTO);
                productionStageDTOEqual = productionStageDTO;
            }
        }
        stageJobExcel.setProductionStageDTOList(resultStage);
        stageJobExcel.setJobDTOList(resultJob);
        return stageJobExcel;
    }

    public List<VendorDTO> readVendorInfo(InputStream file) throws IOException, ParseException {
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        // Lấy ra sheet đầu tiên từ workbook
        Sheet sheet = workbook.getSheetAt(0);
        List<ColumnPropertyEntity> columnPropertyEntityList = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.VENDOR
        );
        List<VendorDTO> result = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            VendorDTO vendorDTO = new VendorDTO();
            //validate
            //            for (int i = 0; i <= columnPropertyEntityList.size(); i++) {
            //                if (ExcelUtils.getStringCellValue(row.getCell(i)) == null || ExcelUtils.isEmpty(row.getCell(i))) continue;
            //                Utils.validateSpecialCharacters(ExcelUtils.getStringCellValue(row.getCell(i)));
            //            }
            // set properties for vendor
            for (int i = 0; i < columnPropertyEntityList.size(); i++) {
                if (
                    columnPropertyEntityList.get(i).getIsRequired() == true &&
                    (
                        StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                        ExcelUtils.getStringCellValue(row.getCell(i)) == null
                    )
                ) {
                    if (!columnPropertyEntityList.get(i).getKeyName().equalsIgnoreCase("status")) {
                        throw new CustomException(
                            HttpStatus.BAD_REQUEST,
                            "cell.must.not.empty",
                            String.valueOf(i + 1),
                            String.valueOf(row.getRowNum() + 1)
                        );
                    }
                }
                switch (columnPropertyEntityList.get(i).getKeyName()) {
                    case "vendorCode":
                        vendorDTO.setVendorCode(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "vendorName":
                        vendorDTO.setVendorName(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "email":
                        vendorDTO.setEmail(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "otherName":
                        vendorDTO.setOtherName(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "address":
                        vendorDTO.setAddress(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "status":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null ||
                                ExcelUtils.getStringCellValue(row.getCell(i)).trim().toLowerCase().equalsIgnoreCase("Hoạt động".trim())
                            ) {
                                vendorDTO.setStatus(1);
                            } else if (
                                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) &&
                                ExcelUtils.getStringCellValue(row.getCell(i)) != null &&
                                ExcelUtils
                                    .getStringCellValue(row.getCell(i))
                                    .trim()
                                    .toLowerCase()
                                    .equalsIgnoreCase("Ngừng hoạt động".trim())
                            ) {
                                vendorDTO.setStatus(0);
                            } else throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                            break;
                        }
                    case "taxCode":
                        vendorDTO.setTaxCode(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "currency":
                        vendorDTO.setCurrency(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "phone":
                        vendorDTO.setPhone(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "faxCode":
                        vendorDTO.setFaxCode(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "contactId":
                        vendorDTO.setContactId(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "contactName":
                        vendorDTO.setContactName(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "contactTitle":
                        vendorDTO.setContactTitle(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "contactGender":
                        vendorDTO.setContactGender(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "contactPhone":
                        vendorDTO.setContactPhone(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "contactEmail":
                        vendorDTO.setContactEmail(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "contactBirthDate":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null
                            ) break;
                            try {
                                if (row.getCell(i) != null && row.getCell(i).getCellType() == STRING) {
                                    vendorDTO.setContactBirthDate(
                                        LocalDate.parse(
                                            ExcelUtils.getStringCellValue(row.getCell(i)),
                                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                        )
                                    );
                                } else if (row.getCell(i) != null) {
                                    vendorDTO.setContactBirthDate(
                                        row.getCell(i).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                    );
                                } else if (ExcelUtils.isEmpty(row.getCell(i)) || ExcelUtils.getStringCellValue(row.getCell(i)) == null) {
                                    break;
                                } else {
                                    throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                                }
                            } catch (Exception e) {
                                throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                            }
                            break;
                        }
                    case "contactAddress":
                        vendorDTO.setContactAddress(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "contactPosition":
                        vendorDTO.setContactPosition(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    default:
                        {
                            if (!StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i)))) {
                                vendorDTO.setVendorMap(
                                    columnPropertyEntityList.get(i).getKeyName(),
                                    ExcelUtils.getStringCellValue(row.getCell(i))
                                );
                            }
                            break;
                        }
                }
            }
            result.add(vendorDTO);
        }
        return result;
    }

    public List<VendorItemDTO> readVendorItem(InputStream file) throws IOException, ParseException {
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        // Lấy ra sheet đầu tiên từ workbook
        Sheet sheet = workbook.getSheetAt(0);
        List<VendorItemDTO> result = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            Cell firstCell = row.getCell(0);
            if (firstCell == null || firstCell.getCellType() == CellType.BLANK) break;
            VendorItemDTO vendorItemDTO = new VendorItemDTO();
            vendorItemDTO.setVendorCode(ExcelUtils.getStringCellValue(row.getCell(0)));
            vendorItemDTO.setItemCode(ExcelUtils.getStringCellValue(row.getCell(4)));
            result.add(vendorItemDTO);
        }
        return result;
    }

    public List<ErrorDTO> readErrorFromExcel(InputStream file) throws IOException, ParseException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
        Sheet sheet = xssfWorkbook.getSheetAt(0);
        List<ColumnPropertyEntity> keyDictionaryDTOS = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.ERROR
        );
        List<ErrorDTO> errorDTOS = new ArrayList<>();

        List<String> erorrCodeList = new ArrayList<>();
        List<String> erorrCodeDuplicate = new ArrayList<>();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            //            for (int i = 0; i < keyDictionaryDTOS.size(); i++) {
            //                if (ExcelUtils.getStringCellValue(row.getCell(i)) == null || ExcelUtils.isEmpty(row.getCell(i))) continue;
            //                Utils.validateSpecialCharacters(ExcelUtils.getStringCellValue(row.getCell(i)));
            //            }

            Map<String, String> keyValue = new HashMap<>();
            for (int i = 0; i < keyDictionaryDTOS.size(); i++) {
                try {
                    keyValue.put(keyDictionaryDTOS.get(i).getKeyName(), row.getCell(i).getStringCellValue());
                } catch (Exception ex) {
                    keyValue.put(keyDictionaryDTOS.get(i).getKeyName(), "");
                }
            }
            ErrorDTO errorDTO = new ErrorDTO();
            for (String key : keyValue.keySet()) {
                switch (key) {
                    case "errorName":
                        if (!StringUtils.isEmpty(keyValue.get(key))) {
                            errorDTO.setErrorName(keyValue.get(key));
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để trống tên lỗi");
                        }
                        break;
                    case "errorCode":
                        if (!StringUtils.isEmpty(keyValue.get(key))) {
                            if (erorrCodeList.contains(keyValue.get(key)) && !erorrCodeDuplicate.contains(keyValue.get(key))) {
                                erorrCodeDuplicate.add(keyValue.get(key));
                            }
                            erorrCodeList.add(keyValue.get(key));
                            String regex = "[!#$%^&*=?]";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(keyValue.get(key));
                            if (matcher.find() == true) throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "Không được import ký tự đặc biệt vào mã"
                            );
                            errorDTO.setErrorCode(keyValue.get(key));
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để mã lỗi");
                        }
                        break;
                    case "errorGroup":
                        List<String> errorGroupName = new ArrayList<>();
                        String[] names = keyValue.get(key).split(",");
                        for (String name : names) {
                            errorGroupName.add(name.trim());
                        }
                        errorDTO.setErrorGroup(errorGroupName);
                        break;
                    case "errorDesc":
                        errorDTO.setErrorDesc(keyValue.get(key));
                        break;
                    case "errorType":
                        errorDTO.setErrorType(keyValue.get(key));
                        break;
                    case "errorStatus":
                        if (
                            StringUtils.isEmpty(keyValue.get(key)) || keyValue.get(key).toLowerCase().equalsIgnoreCase("Hoạt động".trim())
                        ) {
                            errorDTO.setErrorStatus("Hoạt động");
                        } else if (keyValue.get(key).toLowerCase().equalsIgnoreCase("Ngừng hoạt động".trim())) {
                            errorDTO.setErrorStatus("Ngừng hoạt động");
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                        }
                        break;
                    default:
                        errorDTO.getErrorMap().put(key, keyValue.get(key));
                        break;
                }
            }
            errorDTOS.add(errorDTO);
        }

        if (erorrCodeDuplicate != null && erorrCodeDuplicate.size() > 0) {
            String errorCodeString = "";
            for (int i = 0; i < erorrCodeDuplicate.size() - 1; i++) {
                errorCodeString = errorCodeString + erorrCodeDuplicate.get(i) + ", ";
            }
            errorCodeString = errorCodeString + erorrCodeDuplicate.get(erorrCodeDuplicate.size() - 1) + ".";
            throw new CustomException(HttpStatus.CONFLICT, "Có các mã đang trùng nhau trong file vừa import là: " + errorCodeString);
        }

        return errorDTOS;
    }

    public Map<ErrorGroupDTO, List<ErrorDTO>> readErrorGroupExcel(InputStream file) throws IOException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
        Sheet sheet = xssfWorkbook.getSheetAt(0);
        //List<ErrorGroupDTO> errorGroupDTOList = new ArrayList<>();
        List<ErrorDTO> errorDTOSList = new ArrayList<>();
        Map<ErrorGroupDTO, List<ErrorDTO>> errorGroupDTOMap = new LinkedHashMap<>();
        List<ColumnPropertyEntity> keyDictionaryErrorGroup = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.ERRORGROUP
        );
        List<ColumnPropertyEntity> keyDictionaryError = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.ERROR
        );
        String errorGroupCode = "";
        ErrorGroupDTO errorGroupDTO = new ErrorGroupDTO();

        for (Row row : sheet) {
            int index = 0;
            if (row.getRowNum() == 0) continue;

            //            for (int i = 0; i < keyDictionaryErrorGroup.size() + keyDictionaryError.size(); i++) {
            //                if (ExcelUtils.getStringCellValue(row.getCell(i)) == null || ExcelUtils.isEmpty(row.getCell(i))) continue;
            //                Utils.validateSpecialCharacters(ExcelUtils.getStringCellValue(row.getCell(i)));
            //            }
            Map<String, String> keyValueErrorGroup = new HashMap<>();
            for (int i = 0; i < keyDictionaryErrorGroup.size(); i++) {
                index = i;
                try {
                    keyValueErrorGroup.put(keyDictionaryErrorGroup.get(i).getKeyName(), row.getCell(i).getStringCellValue());
                } catch (Exception exception) {
                    keyValueErrorGroup.put(keyDictionaryErrorGroup.get(i).getKeyName(), null);
                }
            }

            if (!errorGroupCode.equals(keyValueErrorGroup.get("errorGroupCode")) && !StringUtils.isEmpty(errorGroupCode.trim())) {
                errorGroupDTOMap.put(errorGroupDTO, errorDTOSList);
                errorDTOSList = new ArrayList<>();
                errorGroupDTO = new ErrorGroupDTO();
            }
            errorGroupCode = keyValueErrorGroup.get("errorGroupCode");

            index++;

            for (String keyErrorGroup : keyValueErrorGroup.keySet()) {
                switch (keyErrorGroup) {
                    case "errorGroupCode":
                        if (!StringUtils.isEmpty(keyValueErrorGroup.get(keyErrorGroup))) {
                            String regex = "[!#$%^&*=?]";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(keyValueErrorGroup.get(keyErrorGroup));
                            if (matcher.find() == true) throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "Không được import ký tự đặc biệt vào mã"
                            );
                            errorGroupDTO.setErrorGroupCode(keyValueErrorGroup.get(keyErrorGroup));
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để trống mã nhóm lỗi");
                        }
                        break;
                    case "errorGroupName":
                        if (!StringUtils.isEmpty(keyValueErrorGroup.get(keyErrorGroup))) {
                            errorGroupDTO.setErrorGroupName(keyValueErrorGroup.get(keyErrorGroup));
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để trống tên nhóm lỗi");
                        }
                        break;
                    case "errorGroupDesc":
                        errorGroupDTO.setErrorGroupDesc(keyValueErrorGroup.get(keyErrorGroup));
                        break;
                    case "errorGroupType":
                        errorGroupDTO.setErrorGroupType(keyValueErrorGroup.get(keyErrorGroup));
                        break;
                    case "errorGroupStatus":
                        if (
                            StringUtils.isEmpty(keyValueErrorGroup.get(keyErrorGroup)) ||
                            keyValueErrorGroup.get(keyErrorGroup).toLowerCase().equalsIgnoreCase("Hoạt động".trim())
                        ) {
                            errorGroupDTO.setErrorGroupStatus("Hoạt động");
                        } else if (keyValueErrorGroup.get(keyErrorGroup).toLowerCase().equalsIgnoreCase("Ngừng hoạt động".trim())) {
                            errorGroupDTO.setErrorGroupStatus("Ngừng hoạt động");
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                        }
                        break;
                    default:
                        errorGroupDTO.getErrorGroupMap().put(keyErrorGroup, keyValueErrorGroup.get(keyErrorGroup));
                        break;
                }
            }

            int checkError = -1;
            Map<String, String> keyValueError = new HashMap<>();
            for (ColumnPropertyEntity columnPropertyEntity : keyDictionaryError) {
                if (ExcelUtils.getStringCellValue(row.getCell(index)) == null || ExcelUtils.isEmpty(row.getCell(index))) checkError++;
                if (columnPropertyEntity.getKeyName().equals("errorGroup")) {
                    keyValueError.put(columnPropertyEntity.getKeyName(), null);
                    checkError++;
                } else {
                    try {
                        keyValueError.put(columnPropertyEntity.getKeyName(), row.getCell(index).getStringCellValue());
                    } catch (Exception ex) {
                        keyValueError.put(columnPropertyEntity.getKeyName(), null);
                    }
                    index++;
                }
            }

            if (checkError == keyDictionaryError.size()) continue;
            ErrorDTO errorDTO = new ErrorDTO();
            for (String keyError : keyValueError.keySet()) {
                switch (keyError) {
                    case "errorName":
                        if (!StringUtils.isEmpty(keyValueError.get(keyError))) {
                            errorDTO.setErrorName(keyValueError.get(keyError));
                        }
                        break;
                    case "errorCode":
                        if (!StringUtils.isEmpty(keyValueError.get(keyError))) {
                            String regex = "[!#$%^&*=?]";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(keyValueError.get(keyError));
                            if (matcher.find() == true) throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "Không được import ký tự đặc biệt vào mã"
                            );
                            errorDTO.setErrorCode(keyValueError.get(keyError));
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để trống mã lỗi");
                        }
                        break;
                    case "errorGroup":
                        List<String> errorGroupName = new ArrayList<>();
                        errorGroupName.add(keyValueErrorGroup.get("errorGroupName"));
                        errorDTO.setErrorGroup(errorGroupName);
                        break;
                    case "errorDesc":
                        errorDTO.setErrorDesc(keyValueError.get(keyError));
                        break;
                    case "errorType":
                        errorDTO.setErrorType(keyValueError.get(keyError));
                        break;
                    case "errorStatus":
                        if (
                            StringUtils.isEmpty(keyValueError.get(keyError)) ||
                            keyValueError.get(keyError).toLowerCase().equalsIgnoreCase("Hoạt động".trim())
                        ) {
                            errorDTO.setErrorStatus("Hoạt động");
                        } else if (keyValueError.get(keyError).toLowerCase().equalsIgnoreCase("Ngừng hoạt động".trim())) {
                            errorDTO.setErrorStatus("Ngừng hoạt động");
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                        }
                        break;
                    default:
                        errorDTO.getErrorMap().put(keyError, keyValueError.get(keyError));
                        break;
                }
            }
            if (errorDTO != null) {
                errorDTOSList.add(errorDTO);
            }
        }

        errorGroupDTOMap.put(errorGroupDTO, errorDTOSList);
        return errorGroupDTOMap;
    }

    public List<EmployeeDTO> importEmployeeFromExcel(InputStream file) throws IOException {
        List<EmployeeDTO> employeeDTOS = new ArrayList<>();
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
        Sheet sheet = xssfWorkbook.getSheetAt(0);
        List<ColumnPropertyEntity> keyDictionary = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(Contants.EntityType.EMPLOYEE);

        List<String> employeeCodeList = new ArrayList<>();
        List<String> employeeCodeDuplicate = new ArrayList<>();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            //            for (int i = 0; i < keyDictionary.size(); i++) {
            //                if (ExcelUtils.getStringCellValue(row.getCell(i)) == null || ExcelUtils.isEmpty(row.getCell(i))) continue;
            //                Utils.validateSpecialCharacters(ExcelUtils.getStringCellValue(row.getCell(i)));
            //            }

            Map<String, String> keyValue = new HashMap<>();
            for (int i = 0; i < keyDictionary.size(); i++) {
                try {
                    //                    keyValue.put(keyDictionary.get(i).getKeyName(), row.getCell(i).getStringCellValue());
                    //                    System.err.println("ssss: "+row.getCell(i).toString());
                    keyValue.put(keyDictionary.get(i).getKeyName(), ExcelUtils.getStringCellValue(row.getCell(i)));
                } catch (Exception ex) {
                    keyValue.put(keyDictionary.get(i).getKeyName(), "");
                }
            }
            EmployeeDTO employeeDTO = new EmployeeDTO();

            for (String key : keyValue.keySet()) {
                switch (key) {
                    case "employeeCode":
                        if (!StringUtils.isEmpty(keyValue.get(key))) {
                            if (employeeCodeList.contains(keyValue.get(key)) && !employeeCodeDuplicate.contains(keyValue.get(key))) {
                                employeeCodeDuplicate.add(keyValue.get(key));
                            }
                            employeeCodeList.add(keyValue.get(key));
                            String regex = "[!#$%^&*=?]";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(keyValue.get(key));
                            if (matcher.find() == true) throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "Không được import ký tự đặc biệt"
                            );
                            employeeDTO.setEmployeeCode(keyValue.get(key));
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để trống mã nhân viên");
                        }
                        break;
                    case "employeeName":
                        if (!StringUtils.isEmpty(keyValue.get(key))) {
                            employeeDTO.setEmployeeName(keyValue.get(key));
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để trống mã nhân viên");
                        }
                        break;
                    case "teamGroup":
                        employeeDTO.setTeamGroup(keyValue.get(key));
                        break;
                    case "employeePhone":
                        employeeDTO.setEmployeePhone(keyValue.get(key));
                        break;
                    case "employeeEmail":
                        employeeDTO.setEmployeeEmail(keyValue.get(key));
                        break;
                    case "employeeNote":
                        employeeDTO.setEmployeeNote(keyValue.get(key));
                        break;
                    case "employeeStatus":
                        if (
                            StringUtils.isEmpty(keyValue.get("employeeStatus")) ||
                            keyValue.get("employeeStatus").toLowerCase().equalsIgnoreCase("Hoạt động")
                        ) {
                            employeeDTO.setEmployeeStatus(1);
                        } else if (keyValue.get("employeeStatus").toLowerCase().equalsIgnoreCase("Ngừng hoạt động")) {
                            employeeDTO.setEmployeeStatus(0);
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                        }
                        break;
                    default:
                        employeeDTO.getEmployeeMap().put(key, keyValue.get(key));
                        break;
                }
            }

            employeeDTOS.add(employeeDTO);
        }
        if (employeeCodeDuplicate != null && employeeCodeDuplicate.size() > 0) {
            String employeeCode = "";
            for (int i = 0; i < employeeCodeDuplicate.size() - 1; i++) {
                employeeCode = employeeCode + employeeCodeDuplicate.get(i) + ", ";
            }
            employeeCode = employeeCode + employeeCodeDuplicate.get(employeeCodeDuplicate.size() - 1) + ".";
            throw new CustomException(HttpStatus.CONFLICT, "Có các mã đang trùng nhau trong file vừa import là: " + employeeCode);
        }

        return employeeDTOS;
    }

    public Map<TeamGroupDTO, List<EmployeeDTO>> importTeamGroupFromExcel(InputStream file) throws IOException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
        Sheet sheet = xssfWorkbook.getSheetAt(0);
        //List<ErrorGroupDTO> errorGroupDTOList = new ArrayList<>();
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        Map<TeamGroupDTO, List<EmployeeDTO>> teamGroupDTOMap = new LinkedHashMap<>();
        List<ColumnPropertyEntity> keyDictionaryTeamGroup = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.TEAM_GROUP
        );
        List<ColumnPropertyEntity> keyDictionaryEmployee = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.EMPLOYEE
        );
        String teamGroupCode = "";
        TeamGroupDTO teamGroupDTO = new TeamGroupDTO();

        List<String> employeeCodeList = new ArrayList<>();
        List<String> employeeCodeDuplicate = new ArrayList<>();

        for (Row row : sheet) {
            int index = 0;
            if (row.getRowNum() == 0) continue;

            //            for (int i = 0; i < keyDictionaryTeamGroup.size() + keyDictionaryEmployee.size(); i++) {
            //                if (ExcelUtils.getStringCellValue(row.getCell(i)) == null || ExcelUtils.isEmpty(row.getCell(i))) continue;
            //                Utils.validateSpecialCharacters(ExcelUtils.getStringCellValue(row.getCell(i)));
            //            }

            Map<String, String> keyValueTeamGroup = new HashMap<>();
            for (int i = 0; i < keyDictionaryTeamGroup.size(); i++) {
                if (!keyDictionaryTeamGroup.get(i).getKeyName().equals("numberOfEmployee")) {
                    try {
                        keyValueTeamGroup.put(keyDictionaryTeamGroup.get(i).getKeyName(), row.getCell(index).getStringCellValue());
                    } catch (Exception exception) {
                        keyValueTeamGroup.put(keyDictionaryTeamGroup.get(i).getKeyName(), null);
                    }
                    index++;
                }
            }

            if (!teamGroupCode.equals(keyValueTeamGroup.get("teamGroupCode")) && !StringUtils.isEmpty(teamGroupCode.trim())) {
                teamGroupDTOMap.put(teamGroupDTO, employeeDTOList);
                employeeDTOList = new ArrayList<>();
                teamGroupDTO = new TeamGroupDTO();
            }
            teamGroupCode = keyValueTeamGroup.get("teamGroupCode");

            for (String keyTeamGroup : keyValueTeamGroup.keySet()) {
                switch (keyTeamGroup) {
                    case "teamGroupCode":
                        if (!StringUtils.isEmpty(keyValueTeamGroup.get(keyTeamGroup))) {
                            String regex = "[!#$%^&*=?]";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(keyValueTeamGroup.get(keyTeamGroup));
                            if (matcher.find() == true) throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "Không được import ký tự đặc biệt vào mã"
                            );
                            teamGroupDTO.setTeamGroupCode(keyValueTeamGroup.get(keyTeamGroup));
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để trống mã nhóm tổ");
                        }
                        break;
                    case "numberOfEmployee":
                        teamGroupDTO.setNumberOfEmployee(null);
                        break;
                    case "teamGroupName":
                        if (!StringUtils.isEmpty(keyValueTeamGroup.get(keyTeamGroup))) {
                            teamGroupDTO.setTeamGroupName(keyValueTeamGroup.get(keyTeamGroup));
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để trống tên nhóm tổ");
                        }
                        break;
                    case "teamGroupQuota":
                        try {
                            teamGroupDTO.setTeamGroupQuota(keyValueTeamGroup.get(keyTeamGroup));
                        } catch (Exception ex) {
                            teamGroupDTO.setTeamGroupQuota(null);
                        }
                        break;
                    case "teamGroupNote":
                        teamGroupDTO.setTeamGroupNote(keyValueTeamGroup.get(keyTeamGroup));
                        break;
                    case "teamGroupStatus":
                        System.err.println("Giá trị status: " + keyValueTeamGroup.get(keyTeamGroup));
                        if (
                            StringUtils.isEmpty(keyValueTeamGroup.get(keyTeamGroup)) ||
                            keyValueTeamGroup.get(keyTeamGroup).toLowerCase().equalsIgnoreCase("Hoạt động")
                        ) {
                            teamGroupDTO.setTeamGroupStatus(1);
                        } else if (keyValueTeamGroup.get(keyTeamGroup).toLowerCase().equalsIgnoreCase("Ngừng hoạt động".trim())) {
                            teamGroupDTO.setTeamGroupStatus(0);
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                        }
                        break;
                    default:
                        teamGroupDTO.getTeamGroupMap().put(keyTeamGroup, keyValueTeamGroup.get(keyTeamGroup));
                        break;
                }
            }

            int checkEmployee = -1;
            Map<String, String> keyValueEmployee = new HashMap<>();
            for (ColumnPropertyEntity columnPropertyEntity : keyDictionaryEmployee) {
                if (columnPropertyEntity.getKeyName().equals("teamGroup")) {
                    keyValueEmployee.put(columnPropertyEntity.getKeyName(), null);
                    checkEmployee++;
                } else {
                    try {
                        keyValueEmployee.put(columnPropertyEntity.getKeyName(), row.getCell(index).getStringCellValue());
                    } catch (Exception ex) {
                        keyValueEmployee.put(columnPropertyEntity.getKeyName(), null);
                        checkEmployee++;
                    }
                    index++;
                }
            }
            if (checkEmployee == keyDictionaryEmployee.size()) continue;

            EmployeeDTO employeeDTO = new EmployeeDTO();
            for (String keyEmployee : keyValueEmployee.keySet()) {
                switch (keyEmployee) {
                    case "employeeCode":
                        if (!StringUtils.isEmpty(keyValueEmployee.get(keyEmployee))) {
                            if (
                                employeeCodeList.contains(keyValueEmployee.get(keyEmployee)) &&
                                !employeeCodeDuplicate.contains(keyValueEmployee.get(keyEmployee))
                            ) {
                                employeeCodeDuplicate.add(keyValueEmployee.get(keyEmployee));
                            }
                            employeeCodeList.add(keyValueEmployee.get(keyEmployee));
                            String regex = "[!#$%^&*=?]";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(keyValueEmployee.get(keyEmployee));
                            if (matcher.find() == true) throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "Không được import ký tự đặc biệt vào mã"
                            );
                            employeeDTO.setEmployeeCode(keyValueEmployee.get(keyEmployee));
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để trống mã nhân viên");
                        }
                        break;
                    case "employeeName":
                        if (!StringUtils.isEmpty(keyValueEmployee.get(keyEmployee))) {
                            employeeDTO.setEmployeeName(keyValueEmployee.get(keyEmployee));
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để trống tên nhân viên");
                        }
                        break;
                    case "teamGroup":
                        employeeDTO.setTeamGroup(keyValueTeamGroup.get("teamGroupCode"));
                        break;
                    case "employeePhone":
                        employeeDTO.setEmployeePhone(keyValueEmployee.get(keyEmployee));
                        break;
                    case "employeeEmail":
                        employeeDTO.setEmployeeEmail(keyValueEmployee.get(keyEmployee));
                        break;
                    case "employeeNote":
                        employeeDTO.setEmployeeNote(keyValueEmployee.get(keyEmployee));
                        break;
                    case "employeeStatus":
                        if (
                            StringUtils.isEmpty(keyValueEmployee.get("employeeStatus")) ||
                            keyValueEmployee.get("employeeStatus").toLowerCase().equalsIgnoreCase("Hoạt động")
                        ) {
                            employeeDTO.setEmployeeStatus(1);
                        } else if (keyValueEmployee.get("employeeStatus").toLowerCase().equalsIgnoreCase("Ngừng hoạt động")) {
                            employeeDTO.setEmployeeStatus(0);
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                        }
                        break;
                    default:
                        employeeDTO.getEmployeeMap().put(keyEmployee, keyValueEmployee.get(keyEmployee));
                        break;
                }
            }
            if (employeeDTO != null) {
                employeeDTOList.add(employeeDTO);
            }
        }
        if (employeeCodeDuplicate != null && employeeCodeDuplicate.size() > 0) {
            String employeeCode = "";
            for (int i = 0; i < employeeCodeDuplicate.size() - 1; i++) {
                employeeCode = employeeCode + employeeCodeDuplicate.get(i) + ", ";
            }
            employeeCode = employeeCode + employeeCodeDuplicate.get(employeeCodeDuplicate.size() - 1) + ".";
            throw new CustomException(HttpStatus.CONFLICT, "Có các mã đang trùng nhau trong file vừa import là: " + employeeCode);
        }
        teamGroupDTOMap.put(teamGroupDTO, employeeDTOList);
        return teamGroupDTOMap;
    }

    public Map<MerchandiseGroupDTO, List<CoittDTO>> importMerchandiseGroupFromExcel(InputStream file) throws IOException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
        Sheet sheet = xssfWorkbook.getSheetAt(0);
        //List<ErrorGroupDTO> errorGroupDTOList = new ArrayList<>();
        List<CoittDTO> coittDTOList = new ArrayList<>();
        Map<MerchandiseGroupDTO, List<CoittDTO>> merchandiseGroupDTOMap = new HashMap<>();
        List<ColumnPropertyEntity> keyDictionaryMerchandiseGroup = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.MERCHANDISE_GROUP
        );
        List<ColumnPropertyEntity> keyDictionaryCoitt = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(Contants.EntityType.BTP);
        String merchandiseGroupCode = "";
        MerchandiseGroupDTO merchandiseGroupDTO = new MerchandiseGroupDTO();

        List<String> coittCodeList = new ArrayList<>();
        List<String> coittCodeDuplicate = new ArrayList<>();

        boolean check = true;

        for (Row row : sheet) {
            int index = 0;
            if (row.getRowNum() == 0) continue;
            check = false;
            //            for (int i = 0; i < keyDictionaryMerchandiseGroup.size() + keyDictionaryMerchandise.size(); i++) {
            //                if (ExcelUtils.getStringCellValue(row.getCell(i)) == null || ExcelUtils.isEmpty(row.getCell(i))) continue;
            //                Utils.validateSpecialCharacters(row.getCell(i).getStringCellValue());
            //            }
            Map<String, String> keyValueMerchandiseGroup = new HashMap<>();
            for (int i = 0; i < keyDictionaryMerchandiseGroup.size(); i++) {
                try {
                    keyValueMerchandiseGroup.put(
                        keyDictionaryMerchandiseGroup.get(i).getKeyName(),
                        row.getCell(index).getStringCellValue()
                    );
                } catch (Exception exception) {
                    keyValueMerchandiseGroup.put(keyDictionaryMerchandiseGroup.get(i).getKeyName(), null);
                }
                index++;
            }

            if (
                !merchandiseGroupCode.equals(keyValueMerchandiseGroup.get("merchandiseGroupCode")) &&
                !StringUtils.isEmpty(merchandiseGroupCode.trim())
            ) {
                merchandiseGroupDTOMap.put(merchandiseGroupDTO, coittDTOList);
                coittDTOList = new ArrayList<>();
                merchandiseGroupDTO = new MerchandiseGroupDTO();
            }
            merchandiseGroupCode = keyValueMerchandiseGroup.get("merchandiseGroupCode");

            for (String keyMerchandiseGroup : keyValueMerchandiseGroup.keySet()) {
                switch (keyMerchandiseGroup) {
                    case "merchandiseGroupCode":
                        if (!StringUtils.isEmpty(keyValueMerchandiseGroup.get(keyMerchandiseGroup))) {
                            String regex = "[!#$%^&*=?]";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(keyValueMerchandiseGroup.get(keyMerchandiseGroup));
                            if (matcher.find() == true) throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "Không được import ký tự đặc biệt vào mã"
                            );
                            merchandiseGroupDTO.setMerchandiseGroupCode(keyValueMerchandiseGroup.get(keyMerchandiseGroup));
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để trống mã nhóm hàng hóa");
                        }
                        break;
                    //                    case "numberOfEmployee":
                    //                        teamGroupDTO.setNumberOfEmployee(null);
                    //                        break;
                    case "merchandiseGroupName":
                        if (!StringUtils.isEmpty(keyValueMerchandiseGroup.get(keyMerchandiseGroup))) {
                            if (keyValueMerchandiseGroup.get(keyMerchandiseGroup).equals("@")) {
                                throw new CustomException(HttpStatus.BAD_REQUEST, "Tên nhóm hàng hóa không được chứa ký tự đặc biệt");
                            }
                            merchandiseGroupDTO.setMerchandiseGroupName(keyValueMerchandiseGroup.get(keyMerchandiseGroup));
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để trống tên hàng hóa");
                        }
                        break;
                    case "merchandiseGroupDescription":
                        merchandiseGroupDTO.setMerchandiseGroupDescription(keyValueMerchandiseGroup.get(keyMerchandiseGroup));
                        break;
                    case "merchandiseGroupNote":
                        merchandiseGroupDTO.setMerchandiseGroupNote(keyValueMerchandiseGroup.get(keyMerchandiseGroup));
                        break;
                    case "merchandiseGroupStatus":
                        if (
                            StringUtils.isEmpty(keyValueMerchandiseGroup.get(keyMerchandiseGroup)) ||
                            keyValueMerchandiseGroup.get(keyMerchandiseGroup).toLowerCase().equalsIgnoreCase("Hoạt động")
                        ) {
                            merchandiseGroupDTO.setMerchandiseGroupStatus(1);
                        } else if (
                            keyValueMerchandiseGroup.get(keyMerchandiseGroup).toLowerCase().equalsIgnoreCase("Ngừng hoạt động".trim())
                        ) {
                            merchandiseGroupDTO.setMerchandiseGroupStatus(0);
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                        }
                        break;
                    default:
                        merchandiseGroupDTO.getPropertiesMap().put(keyMerchandiseGroup, keyValueMerchandiseGroup.get(keyMerchandiseGroup));
                        break;
                }
            }
            Map<String, String> keyValueCoitt = new LinkedHashMap<>();
            int checkCoitt = 0;
            //            for (ColumnPropertyEntity columnPropertyEntity : keyDictionaryCoitt) {
            //                if (ExcelUtils.getStringCellValue(row.getCell(index)) == null || ExcelUtils.isEmpty(row.getCell(index))) checkCoitt++;
            //                try {
            //                    keyValueCoitt.put(columnPropertyEntity.getKeyName(), row.getCell(index).getStringCellValue());
            //                } catch (Exception ex) {
            //                    keyValueCoitt.put(columnPropertyEntity.getKeyName(), null);
            //                }
            //                index++;
            //            }
            String itemGroup, merchandiseCode;

            if (ExcelUtils.getStringCellValue(row.getCell(index)) == null || ExcelUtils.isEmpty(row.getCell(index))) {
                merchandiseCode = "";
            } else {
                merchandiseCode = row.getCell(index).getStringCellValue();
            }

            //            index++;
            //
            //            if (ExcelUtils.getStringCellValue(row.getCell(index)) == null || ExcelUtils.isEmpty(row.getCell(index))) {
            //                itemGroup = "";
            //            } else {
            //                itemGroup = row.getCell(index).getStringCellValue();
            //            }
            //
            if (StringUtils.isEmpty(merchandiseCode)) continue;

            //if (checkCoitt == keyValueCoitt.size()) continue;

            CoittDTO coittDTO = new CoittDTO();

            if (!StringUtils.isEmpty(merchandiseCode)) {
                String regex = "[!#$%^&*=?]";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(merchandiseCode);
                if (matcher.find() == true) throw new CustomException(HttpStatus.BAD_REQUEST, "Không được import ký tự đặc biệt vào mã");

                if (coittCodeList.contains(merchandiseCode) && !coittCodeDuplicate.contains(merchandiseCode)) {
                    coittCodeDuplicate.add(merchandiseCode);
                }

                coittCodeList.add(merchandiseCode);
                coittDTO.setProductCode(merchandiseCode);
            }

            for (String keyCoitt : keyValueCoitt.keySet()) {
                switch (keyCoitt) {
                    case "productCode":
                        if (!StringUtils.isEmpty(keyValueCoitt.get(keyCoitt))) {
                            String regex = "[!#$%^&*=?]";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(keyValueCoitt.get(keyCoitt));
                            if (matcher.find() == true) throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "Không được import ký tự đặc biệt vào mã"
                            );
                            coittCodeList.add(keyValueCoitt.get(keyCoitt));
                            if (keyValueCoitt.get(keyCoitt).equals("@")) {
                                throw new CustomException(HttpStatus.BAD_REQUEST, "Mã hàng hóa không được chứa ký tự đặc biệt");
                            }
                            coittDTO.setProductCode(keyValueCoitt.get(keyCoitt));
                        } else {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để trống mã hàng hóa");
                        }
                        break;
                    //                    case "merchandiseDescription":
                    //                        merchandiseDTO.setMerchandiseDescription(keyValueCoitt.get(keyCoitt));
                    //                        break;
                    //                    case "technicalName":
                    //                        merchandiseDTO.setTechnicalName(keyValueCoitt.get(keyCoitt));
                    //                        break;
                    //                    case "merchandiseVersion":
                    //                        merchandiseDTO.setMerchandiseVersion(keyValueCoitt.get(keyCoitt));
                    //                        break;
                    //                    case "merchandiseUnit":
                    //                        merchandiseDTO.setMerchandiseUnit(keyValueCoitt.get(keyCoitt));
                    //                        break;
                    //                    case "merchandiseNote":
                    //                        merchandiseDTO.setMerchandiseNote(keyValueCoitt.get(keyCoitt));
                    //                        break;
                    //                    default:
                    //                        merchandiseDTO.getPropertiesMap().put(keyCoitt, keyValueCoitt.get(keyCoitt));
                    //                        break;
                    //                }
                }
            }

            //            if (!StringUtils.isEmpty(merchandiseCode)) {
            //                if (itemGroup.equals("NVL")) {
            //                    coittDTO.setItemGroupCode(Contants.ItemGroup.NVL);
            //                } else if (itemGroup.equals("TP")) {
            //                    coittDTO.setItemGroupCode(Contants.ItemGroup.TP);
            //                } else if (itemGroup.equals("BTP")) {
            //                    coittDTO.setItemGroupCode(Contants.ItemGroup.BTP);
            //                } else {
            //                    throw new CustomException(HttpStatus.BAD_REQUEST, "Loại hàng hóa không hợp lệ");
            //                }
            //            }

            if (coittDTO != null) {
                coittDTO.setMerchandiseGroup(merchandiseGroupDTO.getMerchandiseGroupCode());
            }
            merchandiseCode = "";
            coittDTOList.add(coittDTO);
        }
        if (check == true) throw new CustomException("File import không có dữ liệu");
        if (coittCodeDuplicate != null && coittCodeDuplicate.size() > 0) {
            String merchandiseCodeD = "";
            for (int i = 0; i < coittCodeDuplicate.size() - 1; i++) {
                merchandiseCodeD = merchandiseCodeD + coittCodeDuplicate.get(i) + ", ";
            }
            merchandiseCodeD = merchandiseCodeD + coittCodeDuplicate.get(coittCodeDuplicate.size() - 1) + ".";
            throw new CustomException(HttpStatus.CONFLICT, "Có các mã đang trùng nhau trong file vừa import là: " + merchandiseCodeD);
        }
        merchandiseGroupDTOMap.put(merchandiseGroupDTO, coittDTOList);
        return merchandiseGroupDTOMap;
    }

    public MachineExcel readMachineFromExcel(InputStream file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        MachineExcel machineExcel = new MachineExcel();
        List<MachineEntity> machineEntities = new ArrayList<>();
        List<MachineTypeEntity> allMachineType = machineTypeRepository.findAll();
        Map<String, MachineTypeEntity> machineTypeMap = allMachineType
            .stream()
            .collect(Collectors.toMap(machineType -> machineType.getMachineTypeName().toLowerCase(), Function.identity()));
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            if (ExcelUtils.isEmpty(row.getCell(0))) break;

            MachineEntity entity = new MachineEntity();
            ExcelUtils.validateRow(row, 0, 1);

            entity.setMachineCode(ExcelUtils.getStringCellValue(row.getCell(0)));
            entity.setMachineName(ExcelUtils.getStringCellValue(row.getCell(1)));
            String machineTypeName = ExcelUtils.getStringCellValue(row.getCell(2));
            if (machineTypeName != null) {
                if (machineTypeMap.containsKey(machineTypeName.toLowerCase())) {
                    entity.setMachineTypeEntity(machineTypeMap.get(machineTypeName.toLowerCase()));
                } else {
                    throw new CustomException(HttpStatus.BAD_REQUEST, "unknown.type", machineTypeName);
                }
            }
            entity.setProductivity(ExcelUtils.getNumberCellValue(row.getCell(3)));
            entity.setSupplier(ExcelUtils.getStringCellValue(row.getCell(4)));
            entity.setMaintenanceTime(ExcelUtils.getNumberCellValue(row.getCell(5)));
            entity.setMinProductionQuantity(ExcelUtils.getNumberCellValue(row.getCell(6)));
            entity.setMaxProductionQuantity(ExcelUtils.getNumberCellValue(row.getCell(7)));
            entity.setPurchaseDate(ExcelUtils.getDateCell(row.getCell(8)));
            entity.setMaxWaitingTime(ExcelUtils.getNumberCellValue(row.getCell(9)));
            entity.setCycleTime(ExcelUtils.getNumberCellValue(row.getCell(10)));
            entity.setDescription(ExcelUtils.getStringCellValue(row.getCell(11)));
            entity.setStatus(ExcelUtils.getIntegerCellValue(row.getCell(12)));

            machineEntities.add(entity);
        }
        machineExcel.setMachineEntities(machineEntities);
        return machineExcel;
    }

    public ProductionLineExcel readProductionLineFromExcel(InputStream file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        // Lấy ra sheet đầu tiên từ workbook
        Sheet sheet = workbook.getSheetAt(0);
        ProductionLineExcel productionLineExcel = new ProductionLineExcel();
        List<ProductionLineEntity> productionLineEntities = new ArrayList<>();
        Map<String, BaseDynamicDTO> properties = new HashMap<>();
        List<ProductionLineTypeEntity> allProductionLineType = productionLineTypeRepository.findAll();
        Map<String, ProductionLineTypeEntity> productionLineTypeMap = allProductionLineType
            .stream()
            .collect(
                Collectors.toMap(productionLineType -> productionLineType.getProductionLineTypeName().toLowerCase(), Function.identity())
            );
        List<ColumnPropertyEntity> columnPropertyEntityList = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.PRODUCTION_LINE
        );

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            //validate
            //            for (int i = 0; i <= columnPropertyEntityList.size() + 2; i++) {
            //                if (ExcelUtils.getStringCellValue(row.getCell(i)) == null || ExcelUtils.isEmpty(row.getCell(i))) continue;
            //                Utils.validateSpecialCharacters(ExcelUtils.getStringCellValue(row.getCell(i)));
            //            }
            ProductionLineEntity entity = new ProductionLineEntity();
            BaseDynamicDTO baseDynamicDTO = new BaseDynamicDTO();
            // set properties for ProductionLineEntity
            int a = 0;
            for (int i = 0; i < columnPropertyEntityList.size() + 2; i++) {
                int j = i - a;
                //validate requirements columns
                if (
                    columnPropertyEntityList.get(j).getIsRequired() == true &&
                    (
                        StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                        ExcelUtils.getStringCellValue(row.getCell(i)) == null
                    )
                ) {
                    if (!columnPropertyEntityList.get(j).getKeyName().equalsIgnoreCase("status")) {
                        throw new CustomException(
                            HttpStatus.BAD_REQUEST,
                            "cell.must.not.empty",
                            String.valueOf(i + 1),
                            String.valueOf(row.getRowNum() + 1)
                        );
                    }
                }
                switch (columnPropertyEntityList.get(j).getKeyName()) {
                    case "status":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null ||
                                ExcelUtils.getStringCellValue(row.getCell(i)).trim().toLowerCase().equalsIgnoreCase("Hoạt động".trim())
                            ) {
                                entity.setStatus(1);
                            } else if (
                                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) &&
                                ExcelUtils.getStringCellValue(row.getCell(i)) != null &&
                                ExcelUtils
                                    .getStringCellValue(row.getCell(i))
                                    .trim()
                                    .toLowerCase()
                                    .equalsIgnoreCase("Ngừng hoạt động".trim())
                            ) {
                                entity.setStatus(0);
                            } else throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                            break;
                        }
                    case "productionLineName":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null
                            ) break;
                            entity.setProductionLineName(ExcelUtils.getStringCellValue(row.getCell(i)));
                            break;
                        }
                    case "productionLineCode":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null
                            ) break;
                            entity.setProductionLineCode(ExcelUtils.getStringCellValue(row.getCell(i)));
                            break;
                        }
                    case "productionLineType":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null
                            ) break;
                            String machineTypeName = ExcelUtils.getStringCellValue(row.getCell(2));
                            if (machineTypeName != null) {
                                if (productionLineTypeMap.containsKey(machineTypeName.toLowerCase())) {
                                    entity.setProductionLineType(productionLineTypeMap.get(machineTypeName.toLowerCase()));
                                    break;
                                } else {
                                    throw new CustomException(HttpStatus.BAD_REQUEST, "unknown.type", machineTypeName);
                                }
                            }
                            break;
                        }
                    case "productivity":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null
                            ) break;
                            if (row.getCell(i).getCellType() != CellType.NUMERIC) throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "invalid.datatype",
                                ExcelUtils.getStringCellValue(row.getCell(i))
                            );
                            entity.setProductivity(ExcelUtils.getNumberCellValue(row.getCell(i)));
                            break;
                        }
                    case "supplier":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null
                            ) break;
                            entity.setSupplier(ExcelUtils.getStringCellValue(row.getCell(i)));
                            break;
                        }
                    case "description":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null
                            ) break;
                            entity.setDescription(ExcelUtils.getStringCellValue(row.getCell(i)));
                            break;
                        }
                    case "minProductionQuantity":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null
                            ) break;
                            if (row.getCell(i).getCellType() != CellType.NUMERIC) throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "invalid.datatype",
                                ExcelUtils.getStringCellValue(row.getCell(i))
                            );
                            entity.setMinProductionQuantity(ExcelUtils.getNumberCellValue(row.getCell(i)));
                            break;
                        }
                    case "maxProductionQuantity":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null
                            ) break;
                            if (row.getCell(i).getCellType() != CellType.NUMERIC) throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "invalid.datatype",
                                ExcelUtils.getStringCellValue(row.getCell(i))
                            );
                            entity.setMaxProductionQuantity(ExcelUtils.getNumberCellValue(row.getCell(i)));
                            break;
                        }
                    case "purchaseDate":
                        {
                            try {
                                if (row.getCell(i) != null && row.getCell(i).getCellType() == STRING) {
                                    entity.setPurchaseDate(
                                        LocalDate.parse(
                                            ExcelUtils.getStringCellValue(row.getCell(i)),
                                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                        )
                                    );
                                } else if (row.getCell(i) != null) {
                                    entity.setPurchaseDate(
                                        row.getCell(i).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                    );
                                } else if (ExcelUtils.isEmpty(row.getCell(i)) || ExcelUtils.getStringCellValue(row.getCell(i)) == null) {
                                    break;
                                } else {
                                    throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                                }
                            } catch (Exception e) {
                                throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                            }
                            break;
                        }
                    case "cycleTime":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null
                            ) break;
                            if (row.getCell(i).getCellType() != CellType.NUMERIC) throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "invalid.datatype",
                                ExcelUtils.getStringCellValue(row.getCell(i))
                            );
                            entity.setCycleTime(ExcelUtils.getNumberCellValue(row.getCell(i)));
                            break;
                        }
                    case "maintenanceTime":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null
                            ) {
                                a++;
                                i++;
                                break;
                            }
                            if (row.getCell(i).getCellType() != CellType.NUMERIC) throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "invalid.datatype",
                                ExcelUtils.getStringCellValue(row.getCell(i))
                            );
                            entity.setMaintenanceTime(ExcelUtils.getNumberCellValue(row.getCell(i)));
                            if (
                                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i + 1))) &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)) != null &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)).trim().toLowerCase().equalsIgnoreCase("Giây".trim())
                            ) {
                                entity.setMaintenanceTimeUnit(0);
                            } else if (
                                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i + 1))) &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)) != null &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)).trim().toLowerCase().equalsIgnoreCase("Phút".trim())
                            ) {
                                entity.setMaintenanceTimeUnit(1);
                            } else if (
                                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i + 1))) &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)) != null &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)).trim().toLowerCase().equalsIgnoreCase("Giờ".trim())
                            ) {
                                entity.setMaintenanceTimeUnit(2);
                            } else if (
                                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i + 1))) &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)) != null &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)).trim().toLowerCase().equalsIgnoreCase("Ngày".trim())
                            ) {
                                entity.setMaintenanceTimeUnit(3);
                            } else if (
                                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i + 1))) &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)) != null &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)).trim().toLowerCase().equalsIgnoreCase("Tháng".trim())
                            ) {
                                entity.setMaintenanceTimeUnit(4);
                            } else throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                            a++;
                            i++;
                            break;
                        }
                    case "maxWaitingTime":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null
                            ) {
                                a++;
                                i++;
                                break;
                            }
                            if (row.getCell(i).getCellType() != CellType.NUMERIC) throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "invalid.datatype",
                                ExcelUtils.getStringCellValue(row.getCell(i))
                            );
                            entity.setMaxWaitingTime(ExcelUtils.getNumberCellValue(row.getCell(i)));
                            if (
                                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i + 1))) &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)) != null &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)).trim().toLowerCase().equalsIgnoreCase("Giây".trim())
                            ) {
                                entity.setMaxWaitingTimeUnit(0);
                            } else if (
                                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i + 1))) &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)) != null &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)).trim().toLowerCase().equalsIgnoreCase("Phút".trim())
                            ) {
                                entity.setMaxWaitingTimeUnit(1);
                            } else if (
                                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i + 1))) &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)) != null &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)).trim().toLowerCase().equalsIgnoreCase("Giờ".trim())
                            ) {
                                entity.setMaxWaitingTimeUnit(2);
                            } else if (
                                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i + 1))) &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)) != null &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)).trim().toLowerCase().equalsIgnoreCase("Ngày".trim())
                            ) {
                                entity.setMaxWaitingTimeUnit(3);
                            } else if (
                                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i + 1))) &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)) != null &&
                                ExcelUtils.getStringCellValue(row.getCell(i + 1)).trim().toLowerCase().equalsIgnoreCase("Tháng".trim())
                            ) {
                                entity.setMaxWaitingTimeUnit(4);
                            } else throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                            a++;
                            i++;
                            break;
                        }
                    default:
                        {
                            if (!StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i)))) {
                                //set thuộc tính vào map
                                baseDynamicDTO.setPropertiesMap(
                                    columnPropertyEntityList.get(j).getKeyName(),
                                    ExcelUtils.getStringCellValue(row.getCell(i))
                                );
                            }
                            break;
                        }
                }
            }
            properties.put(entity.getProductionLineCode(), baseDynamicDTO);

            // Check maxProd > minProd
            if (
                entity.getMaxProductionQuantity() != null &&
                entity.getMinProductionQuantity() != null &&
                entity.getMaxProductionQuantity() < entity.getMinProductionQuantity()
            ) {
                String minProductionQuantityTitle = "";
                String maxProductionQuantityTitle = "";
                for (ColumnPropertyEntity column : columnPropertyEntityList) {
                    switch (column.getKeyName()) {
                        case "minProductionQuantity":
                            minProductionQuantityTitle = column.getKeyTitle();
                            break;
                        case "maxProductionQuantity":
                            maxProductionQuantityTitle = column.getKeyTitle();
                            break;
                    }
                }
                throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "object.must.be.greater.than.other.at",
                    maxProductionQuantityTitle,
                    minProductionQuantityTitle,
                    String.valueOf(row.getRowNum() + 1)
                );
            }
            productionLineEntities.add(entity);
        }
        productionLineExcel.setProductionLineEntities(productionLineEntities);
        productionLineExcel.setPropertiesOfProductionLine(properties);
        return productionLineExcel;
    }

    public List<CoittDTO> readProductFromExcel(InputStream file, Integer itemGroupCode) throws IOException, ParseException {
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        // Lấy ra sheet đầu tiên từ workbook
        Sheet sheet = workbook.getSheetAt(0);
        List<CoittDTO> result = new ArrayList<>();
        List<ColumnPropertyEntity> columnPropertyEntityList = new ArrayList<>();
        if (itemGroupCode == Contants.ItemGroup.BTP) {
            columnPropertyEntityList = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(Contants.EntityType.BTP);
        } else if (itemGroupCode == Contants.ItemGroup.TP) {
            columnPropertyEntityList = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(Contants.EntityType.TP);
        } else if (itemGroupCode == Contants.ItemGroup.NVL) {
            columnPropertyEntityList = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(Contants.EntityType.NVL);
        } else {
            throw new CustomException(HttpStatus.BAD_REQUEST, "unknow.item.group.code", itemGroupCode.toString());
        }
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            Cell firstCell = row.getCell(0);
            //            if (firstCell == null || firstCell.getCellType() == CellType.BLANK) continue;
            CoittDTO coittDTO = new CoittDTO();
            coittDTO.setItemGroupCode(itemGroupCode);
            //validate
            //            for (int i = 0; i <= columnPropertyEntityList.size(); i++) {
            //                if (ExcelUtils.getStringCellValue(row.getCell(i)) == null || ExcelUtils.isEmpty(row.getCell(i))) continue;
            //                Utils.validateSpecialCharacters(ExcelUtils.getStringCellValue(row.getCell(i)));
            //            }
            // set properties for product
            for (int i = 0; i < columnPropertyEntityList.size(); i++) {
                //validate requirements columns
                if (
                    columnPropertyEntityList.get(i).getIsRequired() == true &&
                    (
                        StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                        ExcelUtils.getStringCellValue(row.getCell(i)) == null
                    )
                ) {
                    if (!columnPropertyEntityList.get(i).getKeyName().equalsIgnoreCase("status")) {
                        throw new CustomException(
                            HttpStatus.BAD_REQUEST,
                            "cell.must.not.empty",
                            String.valueOf(i + 1),
                            String.valueOf(row.getRowNum() + 1)
                        );
                    }
                }
                switch (columnPropertyEntityList.get(i).getKeyName()) {
                    case "status":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null ||
                                ExcelUtils.getStringCellValue(row.getCell(i)).trim().toLowerCase().equalsIgnoreCase("Hoạt động".trim())
                            ) {
                                coittDTO.setStatus(1);
                            } else if (
                                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) &&
                                ExcelUtils.getStringCellValue(row.getCell(i)) != null &&
                                ExcelUtils
                                    .getStringCellValue(row.getCell(i))
                                    .trim()
                                    .toLowerCase()
                                    .equalsIgnoreCase("Ngừng hoạt động".trim())
                            ) {
                                coittDTO.setStatus(0);
                            } else throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                            break;
                        }
                    case "productCode":
                        coittDTO.setProductCode(ExcelUtils.getStringCellValueV2(row.getCell(i)));
                        break;
                    case "proName":
                        coittDTO.setProName(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "techName":
                        coittDTO.setTechName(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "unit":
                        coittDTO.setUnit(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "version":
                        coittDTO.setVersion(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "notice":
                        coittDTO.setNotice(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "kind":
                        coittDTO.setKind(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "note":
                        coittDTO.setNote(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "quantity":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null
                            ) {
                                break;
                            } else if (row.getCell(i).getCellType() == CellType.NUMERIC) {
                                coittDTO.setQuantity(ExcelUtils.getNumberCellValue(row.getCell(i)));
                            } else throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                            break;
                        }
                    case "parent":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null
                            ) {
                                break;
                            } else if (row.getCell(i).getCellType() == CellType.NUMERIC) {
                                coittDTO.setParent(ExcelUtils.getIntegerCellValue(row.getCell(i)));
                            } else throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                            break;
                        }
                    default:
                        {
                            if (!StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i)))) {
                                //set thuộc tính vào map
                                //                            System.err.println(row.getRowNum()+ 1 + "---"+ i+1  +"---" +ExcelUtils.getStringCellValue(row.getCell(i)));
                                coittDTO.setPropertiesMap(
                                    columnPropertyEntityList.get(i).getKeyName(),
                                    ExcelUtils.getStringCellValue(row.getCell(i))
                                );
                                //                            System.err.println(columnPropertyEntityList.get(i).getKeyName()+"----" + ExcelUtils.getStringCellValue(row.getCell(i)));
                            }
                            break;
                        }
                }
            }
            result.add(coittDTO);
        }
        return result;
    }

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
            switch (entityType) {
                case Contants.EntityType.PRODUCTIONSTAGE:
                    keyDictionaryDTO.setEntityType(Contants.EntityType.PRODUCTIONSTAGE);
                    break;
                case Contants.EntityType.VENDOR:
                    keyDictionaryDTO.setEntityType(Contants.EntityType.VENDOR);
                    break;
                case Contants.EntityType.JOB:
                    keyDictionaryDTO.setEntityType(Contants.EntityType.JOB);
                    break;
                case Contants.EntityType.ERROR:
                    keyDictionaryDTO.setEntityType(Contants.EntityType.ERROR);
                    break;
                case Contants.EntityType.ERRORGROUP:
                    keyDictionaryDTO.setEntityType(Contants.EntityType.ERRORGROUP);
                    break;
                case Contants.EntityType.MACHINE:
                    keyDictionaryDTO.setEntityType(Contants.EntityType.MACHINE);
                    break;
                case Contants.EntityType.PRODUCTION_LINE:
                    keyDictionaryDTO.setEntityType(Contants.EntityType.PRODUCTION_LINE);
                    break;
                case Contants.EntityType.BTP:
                    keyDictionaryDTO.setEntityType(Contants.EntityType.BTP);
                    break;
                case Contants.EntityType.TP:
                    keyDictionaryDTO.setEntityType(Contants.EntityType.TP);
                    break;
                case Contants.EntityType.NVL:
                    keyDictionaryDTO.setEntityType(Contants.EntityType.NVL);
                    break;
                case Contants.EntityType.EMPLOYEE:
                    keyDictionaryDTO.setEntityType(Contants.EntityType.EMPLOYEE);
                    break;
                case Contants.EntityType.TEAM_GROUP:
                    keyDictionaryDTO.setEntityType(Contants.EntityType.TEAM_GROUP);
                    break;
                case Contants.EntityType.MERCHANDISE_GROUP:
                    keyDictionaryDTO.setEntityType(Contants.EntityType.MERCHANDISE_GROUP);
                    break;
                case Contants.EntityType.CUSTOMER:
                    keyDictionaryDTO.setEntityType(Contants.EntityType.CUSTOMER);
                    break;
                default:
                    throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.datatype", entityType.toString());
            }
            result.add(keyDictionaryDTO);
        }
        return result;
    }

    private void excelToMqqPrice(Row row, MqqPriceExcelModel result, MultiValuedMap<String, String> itemVendorMap) {
        //        ExcelUtils.validateRow(row, 0, 6);
        ExcelUtils.validateRow(row, 0, 1);
        //        ExcelUtils.validateRow(row, 2, 3);
        //        ExcelUtils.validateRow(row, 6, 9);
        MqqPriceEntity mqqPriceEntity = new MqqPriceEntity();
        String vendorCode = ExcelUtils.getStringCellValue(row.getCell(0));
        String itemCode = ExcelUtils.getStringCellValue(row.getCell(1));

        mqqPriceEntity.setVendorCode(vendorCode);
        mqqPriceEntity.setItemCode(itemCode);
        mqqPriceEntity.setPromotion(false);

        //Update bản ghi này là của giá MOQ mới nhất
        mqqPriceEntity.setCheckNew(true);
        //update các giá MOQ của nhà cung cấp và của mã item này về cũ ( new = false )
        mqqPriceRepository.updateNewestMoq(itemCode, vendorCode);

        //        try {
        //            mqqPriceEntity.setTimeEnd((row.getCell(7).getDateCellValue() == null)? null : row.getCell(7).getDateCellValue());
        //        } catch (Exception e) {
        //            throw new CustomException(HttpStatus.BAD_REQUEST, "unparsable.date");
        //        }
        if (ExcelUtils.getStringCellValue(row.getCell(4)) != null) {
            mqqPriceEntity.setTimeStart(row.getCell(4).getDateCellValue());
        }

        Integer rangeStart = ExcelUtils.getIntegerCellValue(row.getCell(5));
        Integer rangeEnd = ExcelUtils.getIntegerCellValue(row.getCell(6));
        if (rangeStart > rangeEnd) throw new CustomException(
            HttpStatus.BAD_REQUEST,
            "object.must.be.greater.than.other.at",
            "số lượng tối đa",
            "số lượng tối thiểu",
            String.valueOf(row.getRowNum() + 1)
        );
        mqqPriceEntity.setRangeStart(rangeStart);
        mqqPriceEntity.setRangeEnd(rangeEnd);
        mqqPriceEntity.setPrice(ExcelUtils.getNumberCellValue(row.getCell(7)));
        mqqPriceEntity.setCurrency(ExcelUtils.getStringCellValue(row.getCell(8)));
        result.getMqqPriceEntities().add(mqqPriceEntity);

        if (itemVendorMap.containsMapping(itemCode, vendorCode)) return;

        //Check Exist leadTime on DB
        LeadTimeEntity leadTimeEntity = leadTimeRepository.getLeadTimeByVendorCodeAndItemCode(vendorCode, itemCode);

        if (leadTimeEntity == null) {
            leadTimeEntity = new LeadTimeEntity();
            leadTimeEntity.setVendorCode(vendorCode);
            leadTimeEntity.setItemCode(itemCode);
        }
        //        if (ExcelUtils.getStringCellValue(row.getCell(3)) != null) {
        leadTimeEntity.setLeadTime(ExcelUtils.getIntegerCellValue(row.getCell(3)));
        //        }
        //        leadTimeEntity.setNote(ExcelUtils.getStringCellValue(row.getCell(10)));

        result.getLeadTimeEntities().add(leadTimeEntity);

        //        if (!itemVendorMap.containsKey(itemCode)) {
        //            CoittEntity itemEntity = new CoittEntity();
        //            itemEntity.setProductCode(itemCode);
        //            itemEntity.setProName(ExcelUtils.getStringCellValue(row.getCell(3)));
        //            itemEntity.setStatus(1);
        //            result.getItemEntities().add(itemEntity);
        //        }

        itemVendorMap.put(itemCode, vendorCode);
    }

    public MqqPriceExcelModel readPriceExcel(InputStream file) throws IOException, ParseException {
        XSSFWorkbook workbook = new XSSFWorkbook(file);

        // Lấy ra sheet đầu tiên từ workbook
        XSSFSheet sheet = workbook.getSheetAt(0);
        MqqPriceExcelModel result = new MqqPriceExcelModel();
        MultiValuedMap<String, String> itemVendorMap = new ArrayListValuedHashMap<>();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            Cell firstCell = row.getCell(0);
            if (firstCell == null || firstCell.getCellType() == CellType.BLANK) break;
            excelToMqqPrice(row, result, itemVendorMap);
        }
        //        List<String> checkItem = checkItemCode(result.getMqqPriceEntities());
        //        String check = "";
        //        if(checkItem.size() > 0){
        //            for (String item:checkItem
        //            ) {
        //                check += item+" ,";
        //            }
        //            return new CommonResponse<>()
        //                .isOk(false)
        //                .errorCode("400")
        //                .message("Những mã item sau bị trùng: "+check);
        //        }
        return result;
    }

    public List<CustomerDTO> readCustomerInfo(InputStream file) throws IOException, ParseException {
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        // Lấy ra sheet đầu tiên từ workbook
        Sheet sheet = workbook.getSheetAt(0);
        List<ColumnPropertyEntity> columnPropertyEntityList = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.CUSTOMER
        );
        List<CustomerDTO> result = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            CustomerDTO customerDTO = new CustomerDTO();
            // set properties for vendor
            for (int i = 0; i < columnPropertyEntityList.size(); i++) {
                if (
                    columnPropertyEntityList.get(i).getIsRequired() == true &&
                    (
                        StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                        ExcelUtils.getStringCellValue(row.getCell(i)) == null
                    )
                ) {
                    if (!columnPropertyEntityList.get(i).getKeyName().equalsIgnoreCase("status")) {
                        throw new CustomException(
                            HttpStatus.BAD_REQUEST,
                            "cell.must.not.empty",
                            String.valueOf(i + 1),
                            String.valueOf(row.getRowNum() + 1)
                        );
                    }
                }
                switch (columnPropertyEntityList.get(i).getKeyName()) {
                    case "customerCode":
                        customerDTO.setCustomerCode(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "customerName":
                        customerDTO.setCustomerName(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "customerEmail":
                        customerDTO.setCustomerEmail(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "customerPhone":
                        customerDTO.setCustomerPhone(ExcelUtils.getStringCellValueV2(row.getCell(i)));
                        break;
                    case "address":
                        customerDTO.setAddress(ExcelUtils.getStringCellValue(row.getCell(i)));
                        break;
                    case "customerType":
                        customerDTO.setCustomerType(ExcelUtils.getStringCellValue(row.getCell(i)));
                    case "status":
                        {
                            if (
                                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) ||
                                ExcelUtils.getStringCellValue(row.getCell(i)) == null ||
                                ExcelUtils.getStringCellValue(row.getCell(i)).trim().toLowerCase().equalsIgnoreCase("Hoạt động".trim())
                            ) {
                                customerDTO.setStatus(1);
                            } else if (
                                !StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i))) &&
                                ExcelUtils.getStringCellValue(row.getCell(i)) != null &&
                                ExcelUtils
                                    .getStringCellValue(row.getCell(i))
                                    .trim()
                                    .toLowerCase()
                                    .equalsIgnoreCase("Ngừng hoạt động".trim())
                            ) {
                                customerDTO.setStatus(0);
                            } else throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                            break;
                        }
                    default:
                        {
                            if (!StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(i)))) {
                                customerDTO.setPropertiesMap(
                                    columnPropertyEntityList.get(i).getKeyName(),
                                    ExcelUtils.getStringCellValue(row.getCell(i))
                                );
                            }
                            break;
                        }
                }
            }
            result.add(customerDTO);
        }
        return result;
    }

    public List<DataToFillBom> importDataToFillBom(InputStream file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        // Lấy ra sheet đầu tiên từ workbook
        Sheet sheet = workbook.getSheetAt(0);

        List<DataToFillBom> result = new ArrayList<>();
        List<String> productsCode = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            if (StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(0)))) throw new CustomException(
                HttpStatus.BAD_REQUEST,
                "Không được để trống mã hàng hóa"
            );
            DataToFillBom data = new DataToFillBom();
            data.setProductCode(ExcelUtils.getStringCellValue(row.getCell(0)));
            data.setVersion(ExcelUtils.getStringCellValue(row.getCell(1)));
            data.setProductionNorm(ExcelUtils.getNumberCellValue(row.getCell(2)));
            data.setVendor(ExcelUtils.getStringCellValue(row.getCell(3)));
            result.add(data);
        }
        return result;
    }

    public Map<CoittDTO, List<CoittDTO>> readBomFromExcel(InputStream file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        // Lấy ra sheet đầu tiên từ workbook
        Sheet sheet = workbook.getSheetAt(0);

        Map<CoittDTO, List<CoittDTO>> result = new LinkedHashMap<>();

        List<ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.BOM);

        String productCode = "";
        CoittDTO coittDTO = new CoittDTO();
        List<CoittDTO> dataToFillBomList = new ArrayList<>();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            if (
                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(0))) ||
                StringUtils.isEmpty(ExcelUtils.getStringCellValue(row.getCell(1)))
            ) throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để trống mã sản phẩm");
            productCode = ExcelUtils.getStringCellValue(row.getCell(0));

            if (coittDTO != null && !StringUtils.isEmpty(coittDTO.getProductCode()) && !productCode.equals(coittDTO.getProductCode())) {
                result.put(coittDTO, dataToFillBomList);
                coittDTO = new CoittDTO();
                dataToFillBomList = new ArrayList<>();
            }
            int j = 0;
            for (int i = 0; i < columnPropertyEntities.size(); i++) {
                switch (columnPropertyEntities.get(i).getKeyName()) {
                    case "productCode":
                        coittDTO.setProductCode(ExcelUtils.getStringCellValue(row.getCell(j)));
                        j++;
                        break;
                    case "proName":
                        break;
                    case "itemGroup":
                        break;
                    case "version":
                        coittDTO.setVersion(ExcelUtils.getStringCellValue(row.getCell(j)));
                        j++;
                        break;
                    case "proDesc":
                        coittDTO.setProDesc(ExcelUtils.getStringCellValue(row.getCell(j)));
                        j++;
                        break;
                    case "notice":
                        coittDTO.setNotice(ExcelUtils.getStringCellValue(row.getCell(j)));
                        j++;
                        break;
                    case "note":
                        coittDTO.setNote(ExcelUtils.getStringCellValue(row.getCell(j)));
                        j++;
                        break;
                    case "status":
                        coittDTO.setStatus(1);
                        break;
                    case "wareHouse":
                        coittDTO.setWareHouse(ExcelUtils.getStringCellValue(row.getCell(j)));
                        j++;
                        break;
                    case "quantity":
                        coittDTO.setQuantity(ExcelUtils.getNumberCellValue(row.getCell(j)));
                        j++;
                        break;
                    default:
                        coittDTO.setPropertiesMap(
                            columnPropertyEntities.get(i).getKeyName(),
                            ExcelUtils.getStringCellValue(row.getCell(j))
                        );
                        j++;
                        break;
                }
            }

            CoittDTO data = new CoittDTO();
            data.setProductCode(ExcelUtils.getStringCellValue(row.getCell(j)));
            data.setVersion(ExcelUtils.getStringCellValue(row.getCell(j + 1)));
            data.setQuantity(ExcelUtils.getNumberCellValue(row.getCell(j + 2)));
            data.setVendor(ExcelUtils.getStringCellValue(row.getCell(j + 3)));
            if (data != null && !StringUtils.isEmpty(data.getProductCode())) {
                data.setProParent(coittDTO.getProductCode());
                dataToFillBomList.add(data);
            }
        }
        result.put(coittDTO, dataToFillBomList);
        return result;
    }
}
