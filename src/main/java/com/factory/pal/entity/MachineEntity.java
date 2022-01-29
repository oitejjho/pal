package com.factory.pal.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity(name = "machine")
public class MachineEntity implements Serializable {

    private static final long serialVersionUID = 1481959712381175988L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;

    private String name;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "machineEntity", cascade = CascadeType.ALL)
    List<ParameterEntity> parameterEntities;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}
