package com.facenet.mdm.repository.custom;

import java.util.List;

public interface AutoCompleteCustomRepository<T> {
    List<String> getAutoComplete(String keyName, String value, Class<? extends T> type);
}
