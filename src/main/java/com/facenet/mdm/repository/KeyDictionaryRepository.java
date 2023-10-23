package com.facenet.mdm.repository;

import com.facenet.mdm.domain.KeyDictionaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface KeyDictionaryRepository extends JpaRepository<KeyDictionaryEntity, Integer> {
    List<KeyDictionaryEntity> findByKeyNameIn(Collection<String> keyNames);
    KeyDictionaryEntity findByKeyNameAndIsActiveTrue(String keyName);

    @Query ("select kd from KeyDictionaryEntity kd where kd.isActive = true")
    List<KeyDictionaryEntity> getAllKeyDictionary();
}
