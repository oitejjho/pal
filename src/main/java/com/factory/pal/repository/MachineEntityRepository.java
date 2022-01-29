package com.factory.pal.repository;

import com.factory.pal.entity.MachineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MachineEntityRepository extends JpaRepository<MachineEntity, Long> {

    Optional<MachineEntity> findByKey(String key);
}
