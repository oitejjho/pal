package com.factory.pal.controller;

import com.factory.pal.entity.MachineEntity;
import com.factory.pal.model.request.MachineRequest;
import com.factory.pal.service.MachineService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
        return ResponseEntity.ok(machineService.getMachines(key));
    }


    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveValues(@Valid @RequestBody MachineRequest request) {
        machineService.save(request);
    }
}
