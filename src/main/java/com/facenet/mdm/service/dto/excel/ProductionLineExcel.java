package com.facenet.mdm.service.dto.excel;

import com.facenet.mdm.domain.KeyValueEntity;
import com.facenet.mdm.domain.MachineEntity;
import com.facenet.mdm.domain.ProductionLineEntity;
import com.facenet.mdm.service.dto.BaseDynamicDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductionLineExcel {

    private List<ProductionLineEntity> productionLineEntities = new ArrayList<>();
    private Map<String, List<KeyValueEntity>> properties = new HashMap<>();
    private Map<String, BaseDynamicDTO> propertiesOfProductionLine = new HashMap<>();

    public List<ProductionLineEntity> getProductionLineEntities() {
        return productionLineEntities;
    }

    public void setProductionLineEntities(List<ProductionLineEntity> productionLineEntities) {
        this.productionLineEntities = productionLineEntities;
    }

    public Map<String, List<KeyValueEntity>> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, List<KeyValueEntity>> properties) {
        this.properties = properties;
    }

    public Map<String, BaseDynamicDTO> getPropertiesOfProductionLine() {
        return propertiesOfProductionLine;
    }

    public void setPropertiesOfProductionLine(Map<String, BaseDynamicDTO> propertiesOfProductionLine) {
        this.propertiesOfProductionLine = propertiesOfProductionLine;
    }
}
