package com.facenet.mdm.repository;

import com.facenet.mdm.domain.ColumnPropertyEntity;
import com.facenet.mdm.domain.CustomerEntity;
import com.facenet.mdm.repository.custom.CustomerCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {
    CustomerEntity findByCustomerCodeIgnoreCaseAndIsActive(String customerCode, Boolean isActive);
}
