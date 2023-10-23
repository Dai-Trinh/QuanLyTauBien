package com.facenet.mdm.service.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import java.util.Map;

public class TeamGroupDTO {

    private String teamGroupCode;

    private String teamGroupName;

    private String teamGroupQuota;

    private Integer numberOfEmployee;

    private String teamGroupNote;

    private Integer teamGroupStatus;

    private Map<String, String> teamGroupMap = new HashMap<>();

    public String getTeamGroupCode() {
        return teamGroupCode;
    }

    public void setTeamGroupCode(String teamGroupCode) {
        this.teamGroupCode = teamGroupCode;
    }

    public String getTeamGroupName() {
        return teamGroupName;
    }

    public void setTeamGroupName(String teamGroupName) {
        this.teamGroupName = teamGroupName;
    }

    public String getTeamGroupQuota() {
        return teamGroupQuota;
    }

    public void setTeamGroupQuota(String teamGroupQuota) {
        this.teamGroupQuota = teamGroupQuota;
    }

    public Integer getNumberOfEmployee() {
        return numberOfEmployee;
    }

    public void setNumberOfEmployee(Integer numberOfEmployee) {
        this.numberOfEmployee = numberOfEmployee;
    }

    public String getTeamGroupNote() {
        return teamGroupNote;
    }

    public void setTeamGroupNote(String teamGroupNote) {
        this.teamGroupNote = teamGroupNote;
    }

    public Integer getTeamGroupStatus() {
        return teamGroupStatus;
    }

    public void setTeamGroupStatus(Integer teamGroupStatus) {
        this.teamGroupStatus = teamGroupStatus;
    }

    @JsonAnyGetter
    public Map<String, String> getTeamGroupMap() {
        return teamGroupMap;
    }

    @JsonAnySetter
    public void setTeamGroupMap(String key, String value) {
        this.teamGroupMap.put(key, value);
    }
}
