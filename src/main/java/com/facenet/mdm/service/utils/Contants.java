package com.facenet.mdm.service.utils;

import java.util.*;
import liquibase.util.StringUtil;

public class Contants {

    public static final int INT_VALUE = 1;
    public static final int FLOAT_VALUE = 2;
    public static final int STRING_VALUE = 3;
    public static final int JSON_VALUE = 4;
    public static final int DATE_VALUE = 5;
    public static final int BOOLEAN_VALUE = 6;
    public static final int TP = 104;
    public static final int BTP = 101;
    public static final Map<String, Integer> TIME_UNIT = new HashMap<>(5);

    static {
        TIME_UNIT.put("ngày", 86400);
        TIME_UNIT.put("tháng", 2592000);
        TIME_UNIT.put("giờ", 3600);
        TIME_UNIT.put("phút", 60);
        TIME_UNIT.put("giây", 1);
    }

    public interface ErrorStatus {
        Integer STATUS_ACTIVE = 1;
        Integer STATUS_NO_ACTIVE = 0;
        Integer STATUS_OTHER = 2;

        static Integer getStatus(String status) {
            if (StringUtil.isEmpty(status)) {
                return null;
            } else if (status.equals("Hoạt động")) {
                return STATUS_ACTIVE;
            } else if (status.equals("Ngừng hoạt động")) {
                return STATUS_NO_ACTIVE;
            } else return STATUS_OTHER;
        }
    }

    public interface MachineStatus {
        int ACTIVE = 1;
        int INACTIVE = 0;

        static Integer getStatus(String status) {
            if (StringUtil.isEmpty(status)) return null;
            switch (status.toLowerCase()) {
                case "hoạt động":
                    return ACTIVE;
                case "ngừng hoạt động":
                    return INACTIVE;
                default:
                    return null;
            }
        }
    }

    public interface MachineTimeUnit {
        int SECOND = 0;
        int MINUTE = 1;
        int HOUR = 2;
        int DAY = 3;
        int MONTH = 4;
    }

    public interface EntityType {
        int PRODUCTIONSTAGE = 1;
        int VENDOR = 2;
        int JOB = 3;

        int ERROR = 4;
        int ERRORGROUP = 5;
        int MACHINE = 6;
        int PRODUCTION_LINE = 7;
        int BTP = 8;
        int TP = 9;
        int NVL = 10;
        int EMPLOYEE = 11;
        int TEAM_GROUP = 12;

        int MERCHANDISE_GROUP = 13;

        int MERCHANDISE = 14;
        int CUSTOMER = 15;

        int BOM = 16;
    }

    public static final Map<Integer, String> FUNCTION_NAME = Collections.unmodifiableMap(
        new HashMap<>() {
            {
                put(EntityType.MACHINE, "Quản lý máy móc");
                put(EntityType.PRODUCTION_LINE, "Quản lý dây chuyền");
                put(EntityType.PRODUCTIONSTAGE, "Quản lý công đoạn");
                put(EntityType.VENDOR, "Quản lý nhà cung cấp");
                put(EntityType.JOB, "Quản lý Job");
                put(EntityType.BTP, "Quản lý bán thành phẩm");
                put(EntityType.TP, "Quản lý thành phẩm");
                put(EntityType.NVL, "Quản lý nguyên vật liệu");
                put(EntityType.CUSTOMER, "Quản lý thông tin khách hàng");
                put(EntityType.ERROR, "Quản lý lỗi");
                put(EntityType.ERRORGROUP, "Quản lý nhóm lỗi");
                put(EntityType.EMPLOYEE, "Quản lý nhân viên");
                put(EntityType.TEAM_GROUP, "Quản lý nhóm tổ");
                put(EntityType.MERCHANDISE_GROUP, "Quản lý nhóm hàng hóa");
            }
        }
    );

    static {
        TIME_UNIT.put("ngày", 86400);
        TIME_UNIT.put("tháng", 2592000);
        TIME_UNIT.put("giờ", 3600);
        TIME_UNIT.put("phút", 60);
        TIME_UNIT.put("giây", 1);
    }

    public interface ItemGroup {
        Integer TP = 103;
        Integer BTP = 102;
        Integer NVL = 101;
    }

    public interface TimeUnit {
        Integer SECOND = 0;
        Integer MINUTE = 1;
        Integer HOUR = 2;
        Integer DAY = 3;
        Integer MONTH = 4;
    }
}
