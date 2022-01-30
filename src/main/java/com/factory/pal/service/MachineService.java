package com.factory.pal.service;

import com.factory.pal.entity.MachineEntity;
import com.factory.pal.entity.ParameterValueEntity;
import com.factory.pal.exception.ExceptionConstants;
import com.factory.pal.exception.MachineKeyInvalidException;
import com.factory.pal.model.dto.MachineStat;
import com.factory.pal.model.dto.PropertyStat;
import com.factory.pal.model.dto.Stats;
import com.factory.pal.model.request.MachineRequest;
import com.factory.pal.repository.MachineEntityRepository;
import com.factory.pal.repository.ParameterValueEntityRepository;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

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
        List<MachineEntity> machineEntitiesWithLatestParams = machineEntityPage.getContent().stream().map(machineEntity -> {
            List<ParameterValueEntity> latestParameters = Optional.ofNullable(machineEntity.getParameters()).map(Collection::stream).orElse(Stream.empty())
                    .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(ParameterValueEntity::getKey))),
                            ArrayList::new));
            machineEntity.setParameters(latestParameters);
            return machineEntity;
        }).collect(Collectors.toList());

        LOG.info("done getting all latest parameter of all machine");
        return new PageImpl<>(machineEntitiesWithLatestParams);
    }

    public MachineEntity getMachine(String key) {
        LOG.info("getting all latest parameter by machine key {}", key);
        MachineEntity machineEntity = this.machineEntityRepository.findByKey(key)
                .orElseThrow(() -> new MachineKeyInvalidException(ExceptionConstants.MACHINE_KEY_INVALID));
        List<ParameterValueEntity> latestParameters = Optional.ofNullable(machineEntity.getParameters()).map(Collection::stream).orElse(Stream.empty())
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(ParameterValueEntity::getKey))),
                        ArrayList::new));
        machineEntity.setParameters(latestParameters);
        LOG.info("done getting all latest parameter by machine key {}", key);
        return machineEntity;
    }

    public Page<MachineStat> getMachineStats(Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LOG.info("getting stats of all machine");
        Page<MachineEntity> machineEntityPage = this.machineEntityRepository.findAll(pageable);
        List<MachineStat> machineStats = machineEntityPage.getContent().stream()
                .map(machineEntity -> this.getMachineStatsByKey(machineEntity.getKey(), startDateTime, endDateTime))
                .collect(toList());

        LOG.info("done getting stats of all machine");
        return new PageImpl<>(machineStats);
    }

    public MachineStat getMachineStatsByKey(String key, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LOG.info("getting all stats by machine key {}", key);
        MachineEntity machineEntity = this.machineEntityRepository.findByKey(key)
                .orElseThrow(() -> new MachineKeyInvalidException(ExceptionConstants.MACHINE_KEY_INVALID));
        List<ParameterValueEntity> filteredParameters = Optional.ofNullable(machineEntity.getParameters()).map(Collection::stream).orElse(Stream.empty())
                .filter(parameterValueEntity -> parameterValueEntity.getCreatedAt().isAfter(startDateTime) && parameterValueEntity.getCreatedAt().isBefore(endDateTime))
                .collect(Collectors.toList());
        machineEntity.setParameters(filteredParameters);
        LOG.info("done getting all stats by machine key {}", key);
        return this.getStatisticsByMachine(machineEntity);
    }


    public MachineStat getStatisticsByMachine(MachineEntity machineEntity) {
        Map<String, List<String>> keyValues = Optional.ofNullable(machineEntity.getParameters()).map(Collection::stream).orElse(Stream.empty())
                .collect(groupingBy(ParameterValueEntity::getKey, mapping(ParameterValueEntity::getValue, toList())));

        MachineStat machineStat = new MachineStat();
        machineStat.setMachineKey(machineEntity.getKey());
        List<PropertyStat> propertyStats = new ArrayList<>();
        keyValues.forEach((k, v) -> {
            PropertyStat propertyStat = new PropertyStat();
            propertyStat.setKey(k);
            Stats stats = new Stats(this.getMin(v).getAsDouble(), this.getMax(v).getAsDouble(), this.getAverage(v).getAsDouble(), this.getMedian(v));
            propertyStat.setStats(stats);
            propertyStats.add(propertyStat);
        });
        machineStat.setPropertyStats(propertyStats);
        return machineStat;
    }

    private Double getMedian(List<String> values) {
        Median median = new Median();
        return median.evaluate(values.stream()
                .mapToDouble(v -> Double.parseDouble(v)).toArray());
    }

    private OptionalDouble getAverage(List<String> values) {
        return values.stream()
                .mapToDouble(v -> Double.parseDouble(v))
                .average();

    }

    private OptionalDouble getMin(List<String> values) {
        return values.stream()
                .mapToDouble(v -> Double.parseDouble(v))
                .min();
    }

    private OptionalDouble getMax(List<String> values) {
        return values.stream()
                .mapToDouble(v -> Double.parseDouble(v))
                .max();
    }


}
