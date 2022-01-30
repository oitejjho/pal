package com.factory.pal.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity(name = "machine")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MachineEntity implements Serializable {

    private static final long serialVersionUID = 1481959712381175988L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;

    private String name;

    private LocalDateTime createdAt;

    @JsonManagedReference
    @OneToMany(mappedBy = "machineEntity", cascade = CascadeType.ALL)
    @OrderBy("created_at desc")
    List<ParameterValueEntity> parameters;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}
