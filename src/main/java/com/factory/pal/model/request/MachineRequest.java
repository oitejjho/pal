package com.factory.pal.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Data
public class MachineRequest {
    @NotBlank(message = "Machine key can not be empty.")
    String machineKey;

    @NotEmpty(message = "Parameters can not be empty")
    Map<String, String> parameters;
}
