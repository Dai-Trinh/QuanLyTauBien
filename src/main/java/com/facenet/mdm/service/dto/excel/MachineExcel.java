package com.facenet.mdm.service.dto.excel;

import com.facenet.mdm.domain.KeyValueEntity;
import com.facenet.mdm.domain.KeyValueEntityV2;
import com.facenet.mdm.domain.MachineEntity;
import com.facenet.mdm.service.dto.BaseDynamicDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineExcel {

    private List<MachineEntity> machineEntities = new ArrayList<>();
    private Map<String, List<KeyValueEntityV2>> properties = new HashMap<>();

    public List<MachineEntity> getMachineEntities() {
        return machineEntities;
    }

    public void setMachineEntities(List<MachineEntity> machineEntities) {
        this.machineEntities = machineEntities;
    }

    public Map<String, List<KeyValueEntityV2>> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, List<KeyValueEntityV2>> properties) {
        this.properties = properties;
    }
}
