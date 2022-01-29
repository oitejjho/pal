package com.factory.pal.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity(name = "parameter")
public class ParameterEntity implements Serializable {

    private static final long serialVersionUID = -7458478766831925010L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;

    private String name;

    private String type;

    private String unit;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "machine_key",
            referencedColumnName = "key"
    )
    private MachineEntity machineEntity;

    @OneToMany(mappedBy = "parameterEntity", cascade = CascadeType.ALL)
    List<ParameterValueEntity> parameterValueEntities;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
