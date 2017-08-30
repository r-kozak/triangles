package com.kozak.triangles.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Таблица для хранения информации об участках земли
 * 
 * @author Roman: 30 августа 2017 г.
 */
@Entity(name = "LAND_LOT")
@Table(name = "LAND_LOT")
public class LandLot extends BaseEntity {

    @Column(name = "NAME", length = 20)
    private String name;

    @Column(name = "VALUE")
    private String value;

    public LandLot() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
