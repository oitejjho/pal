package com.factory.pal.service;

import com.factory.pal.entity.MachineEntity;
import com.factory.pal.entity.ParameterValueEntity;
import com.factory.pal.exception.MachineKeyInvalidException;
import com.factory.pal.model.dto.MachineStat;
import com.factory.pal.model.request.MachineRequest;
import com.factory.pal.repository.MachineEntityRepository;
import com.factory.pal.repository.ParameterValueEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MachineServiceTests {

    @Mock
    private MachineEntityRepository machineEntityRepository;
    @Mock
    private ParameterValueEntityRepository parameterValueEntityRepository;

    private MachineService machineService;
    private MachineRequest machineRequest;
    private MachineEntity machineEntity;

    @BeforeEach
    public void setUp() {
        this.machineService = new MachineService(machineEntityRepository, parameterValueEntityRepository);
        machineRequest = new MachineRequest();
        machineRequest.setMachineKey("aufwickler");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("log_diameter", "14");
        parameters.put("speed", "24");
        machineRequest.setParameters(parameters);

        machineEntity = new MachineEntity();
        machineEntity.setKey("machine_key");
        List<ParameterValueEntity> parameterValueEntities = new ArrayList<>();

        ParameterValueEntity param1 = new ParameterValueEntity();
        param1.setMachineEntity(machineEntity);
        param1.setKey("TS_setpoint_tail_length");
        param1.setValue("15");
        param1.setCreatedAt(LocalDateTime.now());
        parameterValueEntities.add(param1);

        ParameterValueEntity param2 = new ParameterValueEntity();
        param2.setMachineEntity(machineEntity);
        param2.setKey("TS_setpoint_tail_length");
        param2.setValue("19");
        param2.setCreatedAt(LocalDateTime.now());
        parameterValueEntities.add(param2);
        machineEntity.setParameters(parameterValueEntities);
    }

    @Test
    public void testSaveSuccess() {
        when(this.machineEntityRepository.findByKey(Mockito.anyString())).thenReturn(Optional.of(machineEntity));
        when(this.parameterValueEntityRepository.saveAll(any(List.class))).thenReturn(new ArrayList());

        this.machineService.save(machineRequest);

        verify(this.machineEntityRepository, times(1)).findByKey(Mockito.anyString());
        verify(this.parameterValueEntityRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    public void testSaveSuccessWithMachineEntityEmpty() {
        when(this.machineEntityRepository.findByKey(Mockito.anyString())).thenReturn(Optional.empty());

        assertThrows(MachineKeyInvalidException.class, () -> this.machineService.save(machineRequest));

        verify(this.machineEntityRepository, times(1)).findByKey(Mockito.anyString());
    }

    @Test
    public void testSaveSuccessWithParameterValueRepositoryException() {
        when(this.machineEntityRepository.findByKey(Mockito.anyString())).thenReturn(Optional.of(machineEntity));
        when(this.parameterValueEntityRepository.saveAll(any(List.class))).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> this.machineService.save(machineRequest));

        verify(this.machineEntityRepository, times(1)).findByKey(Mockito.anyString());
        verify(this.parameterValueEntityRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    public void testGetMachinesSuccess() {
        List<MachineEntity> machineEntities = new ArrayList<>();
        machineEntities.add(machineEntity);
        Page<MachineEntity> machineEntityPage = new PageImpl<>(machineEntities);
        when(this.machineEntityRepository.findAll(any(Pageable.class))).thenReturn(machineEntityPage);

        Page<MachineEntity> actual = this.machineService.getMachines(PageRequest.of(0, 10));

        verify(this.machineEntityRepository, times(1)).findAll(any(Pageable.class));
        Assertions.assertEquals(1, actual.getContent().size());
    }

    @Test
    public void testGetMachinesSuccessWithoutContent() {
        List<MachineEntity> machineEntities = new ArrayList<>();
        Page<MachineEntity> machineEntityPage = new PageImpl<>(machineEntities);
        when(this.machineEntityRepository.findAll(any(Pageable.class))).thenReturn(machineEntityPage);

        Page<MachineEntity> actual = this.machineService.getMachines(PageRequest.of(0, 10));

        verify(this.machineEntityRepository, times(1)).findAll(any(Pageable.class));
        Assertions.assertEquals(0, actual.getContent().size());
    }

    @Test
    public void testGetMachineSuccess() {
        when(this.machineEntityRepository.findByKey(anyString())).thenReturn(Optional.of(machineEntity));

        MachineEntity actual = this.machineService.getMachine(anyString());

        verify(this.machineEntityRepository, times(1)).findByKey(anyString());
        Assertions.assertNotNull(actual);
    }

    @Test
    public void testGetMachineSuccessWithMachineEntityNull() {
        when(this.machineEntityRepository.findByKey(anyString())).thenReturn(Optional.empty());

        assertThrows(MachineKeyInvalidException.class, () -> {
            MachineEntity actual = this.machineService.getMachine(anyString());
        });

        verify(this.machineEntityRepository, times(1)).findByKey(anyString());
    }

    @Test
    public void testGetMachineStatsSuccess() {
        List<MachineEntity> machineEntities = new ArrayList<>();
        machineEntities.add(machineEntity);
        Page<MachineEntity> machineEntityPage = new PageImpl<>(machineEntities);
        when(this.machineEntityRepository.findAll(any(Pageable.class))).thenReturn(machineEntityPage);
        when(this.machineEntityRepository.findByKey(Mockito.anyString())).thenReturn(Optional.of(machineEntity));

        Page<MachineStat> actual = this.machineService.getMachineStats(PageRequest.of(0, 10), LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7));

        verify(this.machineEntityRepository, times(1)).findAll(any(Pageable.class));
        verify(this.machineEntityRepository, times(1)).findByKey(Mockito.anyString());
        Assertions.assertEquals(1, actual.getContent().size());
        Assertions.assertEquals("machine_key", actual.getContent().get(0).getMachineKey());
        Assertions.assertEquals("TS_setpoint_tail_length", actual.getContent().get(0).getPropertyStats().get(0).getKey());
        Assertions.assertEquals(15.0, actual.getContent().get(0).getPropertyStats().get(0).getStats().getMin());
        Assertions.assertEquals(19.0, actual.getContent().get(0).getPropertyStats().get(0).getStats().getMax());
        Assertions.assertEquals(17.0, actual.getContent().get(0).getPropertyStats().get(0).getStats().getAverage());
        Assertions.assertEquals(17.0, actual.getContent().get(0).getPropertyStats().get(0).getStats().getMedian());
    }

    @Test
    public void testGetMachineStatsSuccessWithNoMachine() {
        List<MachineEntity> machineEntities = new ArrayList<>();
        Page<MachineEntity> machineEntityPage = new PageImpl<>(machineEntities);
        when(this.machineEntityRepository.findAll(any(Pageable.class))).thenReturn(machineEntityPage);

        Page<MachineStat> actual = this.machineService.getMachineStats(PageRequest.of(0, 10), LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7));

        verify(this.machineEntityRepository, times(1)).findAll(any(Pageable.class));
        Assertions.assertEquals(0, actual.getContent().size());
    }

    @Test
    public void testGetMachineStatsByKeySuccess() {
        when(this.machineEntityRepository.findByKey(Mockito.anyString())).thenReturn(Optional.of(machineEntity));

        MachineStat actual = this.machineService.getMachineStatsByKey(machineEntity.getKey(), LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7));

        verify(this.machineEntityRepository, times(1)).findByKey(Mockito.anyString());
        Assertions.assertNotNull(actual);
        Assertions.assertEquals("machine_key", actual.getMachineKey());
        Assertions.assertEquals("TS_setpoint_tail_length", actual.getPropertyStats().get(0).getKey());
        Assertions.assertEquals(15.0, actual.getPropertyStats().get(0).getStats().getMin());
        Assertions.assertEquals(19.0, actual.getPropertyStats().get(0).getStats().getMax());
        Assertions.assertEquals(17.0, actual.getPropertyStats().get(0).getStats().getAverage());
        Assertions.assertEquals(17.0, actual.getPropertyStats().get(0).getStats().getMedian());
    }

    @Test
    public void testGetMachineStatsByKeyWithInvalidMachineKey() {
        when(this.machineEntityRepository.findByKey(Mockito.anyString())).thenReturn(Optional.empty());

        assertThrows(MachineKeyInvalidException.class, () -> {
            MachineStat actual = this.machineService.getMachineStatsByKey(machineEntity.getKey(), LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7));
        });

        verify(this.machineEntityRepository, times(1)).findByKey(Mockito.anyString());
    }

    @Test
    public void testGetStatisticsByMachineSuccess() {

        MachineStat actual = this.machineService.getStatisticsByMachine(machineEntity);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals("machine_key", actual.getMachineKey());
        Assertions.assertEquals("TS_setpoint_tail_length", actual.getPropertyStats().get(0).getKey());
        Assertions.assertEquals(15.0, actual.getPropertyStats().get(0).getStats().getMin());
        Assertions.assertEquals(19.0, actual.getPropertyStats().get(0).getStats().getMax());
        Assertions.assertEquals(17.0, actual.getPropertyStats().get(0).getStats().getAverage());
        Assertions.assertEquals(17.0, actual.getPropertyStats().get(0).getStats().getMedian());
    }

    @Test
    public void testGetStatisticsByMachineWithNullParameters() {
        machineEntity.setParameters(null);
        MachineStat actual = this.machineService.getStatisticsByMachine(machineEntity);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals("machine_key", actual.getMachineKey());
        Assertions.assertEquals(0, actual.getPropertyStats().size());
    }

    @Test
    public void testGetStatisticsByMachineWithEmptyArray() {
        machineEntity.setParameters(new ArrayList<>());
        MachineStat actual = this.machineService.getStatisticsByMachine(machineEntity);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals("machine_key", actual.getMachineKey());
        Assertions.assertEquals(0, actual.getPropertyStats().size());
    }
}
