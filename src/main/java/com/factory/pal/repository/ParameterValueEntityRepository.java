package com.factory.pal.repository;

import com.factory.pal.entity.ParameterValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParameterValueEntityRepository extends JpaRepository<ParameterValueEntity, Long> {

}
