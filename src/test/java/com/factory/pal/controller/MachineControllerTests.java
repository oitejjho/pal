package com.factory.pal.controller;

import com.factory.pal.controller.MachineController;
import com.factory.pal.entity.MachineEntity;
import com.factory.pal.entity.ParameterValueEntity;
import com.factory.pal.exception.ExceptionConstants;
import com.factory.pal.exception.MachineKeyInvalidException;
import com.factory.pal.model.dto.MachineStat;
import com.factory.pal.model.dto.PropertyStat;
import com.factory.pal.model.dto.Stats;
import com.factory.pal.model.request.MachineRequest;
import com.factory.pal.service.MachineService;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MachineControllerTests {

    @Mock
    private MachineService machineService;

    private MachineController machineController;

    private Page<MachineEntity> machineEntityPage;
    private List<MachineEntity> machineEntityList;
    private MachineEntity machineEntity;
    private Page<MachineStat> machineStatPage;
    private List<MachineStat> machineStatList;
    private MachineStat machineStat;

    @BeforeEach
    public void setup() {
        machineController = new MachineController(machineService);

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
        machineEntityList = new ArrayList<>();
        machineEntityList.add(machineEntity);
        machineEntityPage = new PageImpl<>(machineEntityList);

        Stats stats = new Stats();
        stats.setMin(15.0);
        stats.setMax(19.0);
        stats.setAverage(17.0);
        stats.setMedian(17.0);
        PropertyStat propertyStat = new PropertyStat();
        propertyStat.setKey("TS_setpoint_tail_length");
        propertyStat.setStats(stats);
        List<PropertyStat> propertyStats = new ArrayList<>();
        propertyStats.add(propertyStat);
        machineStat = new MachineStat();
        machineStat.setMachineKey("machine_key");
        machineStat.setPropertyStats(propertyStats);
        machineStatList = new ArrayList<>();
        machineStatList.add(machineStat);
        machineStatPage = new PageImpl<>(machineStatList);

    }

    @Test
    public void testGetMachinesSuccess() {
        when(this.machineService.getMachines(any(PageRequest.class))).thenReturn(machineEntityPage);

        ResponseEntity<Page<MachineEntity>> actual = this.machineController.getMachines(0, 10, Sort.Direction.DESC, "createdAt");

        verify(this.machineService, times(1)).getMachines(any(PageRequest.class));
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(200, actual.getStatusCodeValue());
        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(1, actual.getBody().getContent().size());
        Assertions.assertEquals(2, actual.getBody().getContent().get(0).getParameters().size());
    }

    @Test
    public void testGetMachinesSuccessWithEmptyPage() {
        machineEntityPage = new PageImpl<>(new ArrayList<>());
        when(this.machineService.getMachines(any(PageRequest.class))).thenReturn(machineEntityPage);

        ResponseEntity<Page<MachineEntity>> actual = this.machineController.getMachines(0, 10, Sort.Direction.DESC, "createdAt");

        verify(this.machineService, times(1)).getMachines(any(PageRequest.class));
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(200, actual.getStatusCodeValue());
        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(0, actual.getBody().getContent().size());
    }

    @Test
    public void testGetMachinesByKeySuccess() {
        when(this.machineService.getMachine(anyString())).thenReturn(machineEntity);

        ResponseEntity<MachineEntity> actual = this.machineController.getMachinesByKey(machineEntity.getKey());

        verify(this.machineService, times(1)).getMachine(anyString());
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(200, actual.getStatusCodeValue());
        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(2, actual.getBody().getParameters().size());
    }

    @Test
    public void testGetMachinesByKeyWithInvalidMachineKey() {
        when(this.machineService.getMachine(anyString())).thenThrow(new MachineKeyInvalidException(ExceptionConstants.MACHINE_KEY_INVALID));

        assertThrows(MachineKeyInvalidException.class, () -> {
            ResponseEntity<MachineEntity> actual = this.machineController.getMachinesByKey(machineEntity.getKey());
        });

        verify(this.machineService, times(1)).getMachine(anyString());
    }

    @Test
    public void testSaveMachineParametersSuccess() {
        Mockito.doNothing().when(this.machineService).save(any(MachineRequest.class));

        this.machineController.saveMachineParameters(new MachineRequest());

        verify(this.machineService, times(1)).save(any(MachineRequest.class));
    }

    @Test
    public void testSaveMachineParametersInvalidMachineKey() {
        Mockito.doThrow(new MachineKeyInvalidException(ExceptionConstants.MACHINE_KEY_INVALID)).when(this.machineService).save(any(MachineRequest.class));

        assertThrows(MachineKeyInvalidException.class, () -> this.machineController.saveMachineParameters(new MachineRequest()));

        verify(this.machineService, times(1)).save(any(MachineRequest.class));
    }

    @Test
    public void testGetMachinesStatsByKeySuccess() {
        when(this.machineService.getMachineStatsByKey(anyString(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(machineStat);

        ResponseEntity<MachineStat> actual = this.machineController.getMachinesStatsByKey("machine_key", LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7));

        verify(this.machineService, times(1)).getMachineStatsByKey(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(200, actual.getStatusCodeValue());
        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(1, actual.getBody().getPropertyStats().size());
    }

    @Test
    public void testGetMachinesStatsByKeyWithInvalidMachineKey() {
        Mockito.doThrow(new MachineKeyInvalidException(ExceptionConstants.MACHINE_KEY_INVALID)).when(this.machineService).getMachineStatsByKey(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));

        assertThrows(MachineKeyInvalidException.class, () -> {
            ResponseEntity<MachineStat> actual = this.machineController.getMachinesStatsByKey("machine_key", LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7));
        });

        verify(this.machineService, times(1)).getMachineStatsByKey(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void testGetMachinesStatsSuccess() {
        when(this.machineService.getMachineStats(any(PageRequest.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(machineStatPage);

        ResponseEntity<Page<MachineStat>> actual = this.machineController.getMachinesStats(0, 10, Sort.Direction.DESC, "createdAt", LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7));

        verify(this.machineService, times(1)).getMachineStats(any(PageRequest.class), any(LocalDateTime.class), any(LocalDateTime.class));
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(200, actual.getStatusCodeValue());
        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(1, actual.getBody().getContent().size());
    }

    @Test
    public void testGetMachinesStatsWithInvalidMachineKey() {
        Mockito.doThrow(new MachineKeyInvalidException(ExceptionConstants.MACHINE_KEY_INVALID)).when(this.machineService).getMachineStats(any(PageRequest.class), any(LocalDateTime.class), any(LocalDateTime.class));

        assertThrows(MachineKeyInvalidException.class, () -> {
            ResponseEntity<Page<MachineStat>> actual = this.machineController.getMachinesStats(0, 10, Sort.Direction.DESC, "createdAt", LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7));
        });

        verify(this.machineService, times(1)).getMachineStats(any(PageRequest.class), any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
