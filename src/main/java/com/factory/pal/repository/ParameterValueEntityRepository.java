package com.factory.pal.repository;

import com.factory.pal.entity.MachineEntity;
import com.factory.pal.entity.ParameterValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParameterValueEntityRepository extends JpaRepository<ParameterValueEntity, Long> {

    List<ParameterValueEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<ParameterValueEntity> findByMachineEntityAndCreatedAtBetween(MachineEntity machineEntity, LocalDateTime startDate, LocalDateTime endDate);
}
