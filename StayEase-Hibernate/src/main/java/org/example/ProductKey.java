package org.example;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class ProductKey implements Serializable {
    private int number;

    private long code;
}
