package com.facenet.mdm.service.utils;

import com.facenet.mdm.service.exception.CustomException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

public class Utils {

    public static boolean validateTimeUnit(String timeUnit) {
        if (StringUtils.isEmpty(timeUnit)) return false;
        return Contants.TIME_UNIT.containsKey(timeUnit.toLowerCase());
    }

    public static Pageable getPageable(int pageNumber, int pageSize) {
        if (pageSize == 0) return Pageable.unpaged();
        return PageRequest.of(pageNumber, pageSize);
    }
}
