package com.factory.pal.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "parameter_value")
public class ParameterValueEntity implements Serializable {

    private static final long serialVersionUID = 3115420759895732657L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parameter_id",
            referencedColumnName = "id"
    )
    private ParameterEntity parameterEntity;

    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
