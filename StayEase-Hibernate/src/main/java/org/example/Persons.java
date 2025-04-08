package org.example;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Persons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToOne
    private Passport passport;
}
