package com.factory.pal.service;

import com.factory.pal.entity.MachineEntity;
import com.factory.pal.entity.ParameterValueEntity;
import com.factory.pal.exception.ExceptionConstants;
import com.factory.pal.exception.MachineKeyInvalidException;
import com.factory.pal.model.request.MachineRequest;
import com.factory.pal.repository.MachineEntityRepository;
import com.factory.pal.repository.ParameterValueEntityRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Service
public class MachineService {

    private static final Logger LOG = LogManager.getLogger(MachineService.class);

    private final MachineEntityRepository machineEntityRepository;
    private final ParameterValueEntityRepository parameterValueEntityRepository;

    public MachineService(MachineEntityRepository machineEntityRepository,
                          ParameterValueEntityRepository parameterValueEntityRepository) {
        this.machineEntityRepository = machineEntityRepository;
        this.parameterValueEntityRepository = parameterValueEntityRepository;
    }

    public void save(MachineRequest request) {
        LOG.info("saving existing machine with parameters, machine key {}", request.getMachineKey());
        String machineKey = request.getMachineKey();

        MachineEntity machineEntity = this.machineEntityRepository.findByKey(machineKey)
                .orElseThrow(() -> new MachineKeyInvalidException(ExceptionConstants.MACHINE_KEY_INVALID));

        List<ParameterValueEntity> parameterValueEntities = new ArrayList<>();

        request.getParameters().forEach((paramKey, value) -> {

            ParameterValueEntity parameterValueEntity = new ParameterValueEntity();
            parameterValueEntity.setMachineEntity(machineEntity);
            parameterValueEntity.setKey(paramKey);
            parameterValueEntity.setValue(value);

            parameterValueEntities.add(parameterValueEntity);
        });

        this.parameterValueEntityRepository.saveAll(parameterValueEntities);
        LOG.info("done saving existing machine with parameters, machine key {}", request.getMachineKey());

    }

    public Page<MachineEntity> getMachines(Pageable pageable) {
        LOG.info("getting all latest parameter of all machine");
        Page<MachineEntity> machineEntityPage = this.machineEntityRepository.findAll(pageable);
        List<MachineEntity> machineEntitiesWithLatestParams = machineEntityPage.stream().map(machineEntity -> {
            List<ParameterValueEntity> latestParameters = machineEntity.getParameters().stream()
                    .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(ParameterValueEntity::getKey))),
                            ArrayList::new));
            machineEntity.setParameters(latestParameters);
            return machineEntity;
        }).collect(Collectors.toList());

        LOG.info("done getting all latest parameter of all machine");
        return new PageImpl<>(machineEntitiesWithLatestParams);
    }

    public MachineEntity getMachines(String key) {
        LOG.info("getting all latest parameter by machine key {}", key);
        MachineEntity machineEntity = this.machineEntityRepository.findByKey(key)
                .orElseThrow(() -> new MachineKeyInvalidException(ExceptionConstants.MACHINE_KEY_INVALID));
        List<ParameterValueEntity> latestParameters = machineEntity.getParameters().stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(ParameterValueEntity::getKey))),
                ArrayList::new));
        machineEntity.setParameters(latestParameters);
        LOG.info("done getting all latest parameter by machine key {}", key);
        return machineEntity;
    }
}
