package com.facenet.mdm.service.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import java.util.Map;

public class BaseDynamicDTO {

    private final Map<String, String> properties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, String> getPropertiesMap() {
        return properties;
    }

    @JsonAnySetter
    public void setPropertiesMap(String key, String value) {
        properties.put(key, value);
    }
}
