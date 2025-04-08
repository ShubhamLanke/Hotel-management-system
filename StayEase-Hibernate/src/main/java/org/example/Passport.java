package org.example;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Passport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String number;

}
