package com.factory.pal.controller;

import com.factory.pal.entity.MachineEntity;
import com.factory.pal.model.dto.MachineStat;
import com.factory.pal.model.request.MachineRequest;
import com.factory.pal.service.MachineService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/internal-api/machine")
public class MachineController {

    private final MachineService machineService;

    public MachineController(MachineService machineService) {
        this.machineService = machineService;
    }

    @GetMapping
    public ResponseEntity<Page<MachineEntity>> getMachines(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                           @RequestParam(value = "count", defaultValue = "10", required = false) int size,
                                                           @RequestParam(value = "order", defaultValue = "DESC", required = false) Sort.Direction direction,
                                                           @RequestParam(value = "sort", defaultValue = "createdAt", required = false) String sortProperty) {
        PageRequest pageRequest = PageRequest.of(page, size, direction, sortProperty);
        return ResponseEntity.ok(machineService.getMachines(pageRequest));
    }

    @GetMapping(value = "/{key}")
    public ResponseEntity<MachineEntity> getMachinesByKey(@PathVariable String key) {
        return ResponseEntity.ok(machineService.getMachine(key));
    }


    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveValues(@Valid @RequestBody MachineRequest request) {
        machineService.save(request);
    }

    @GetMapping(value = "/{key}/stats")
    public ResponseEntity<MachineStat> getMachinesStatsByKey(@PathVariable String key,
                                                             @RequestParam("startDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
                                                             @RequestParam("endDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {
        return ResponseEntity.ok(machineService.getMachineStatsByKey(key, startDateTime, endDateTime));
    }

    @GetMapping(value = "/stats")
    public ResponseEntity<Page<MachineStat>> getMachinesStats(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                                @RequestParam(value = "count", defaultValue = "10", required = false) int size,
                                                                @RequestParam(value = "order", defaultValue = "DESC", required = false) Sort.Direction direction,
                                                                @RequestParam(value = "sort", defaultValue = "createdAt", required = false) String sortProperty,
                                                                @RequestParam("startDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
                                                                @RequestParam("endDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {
        PageRequest pageRequest = PageRequest.of(page, size, direction, sortProperty);
        return ResponseEntity.ok(machineService.getMachineStats(pageRequest, startDateTime, endDateTime));
    }
}
