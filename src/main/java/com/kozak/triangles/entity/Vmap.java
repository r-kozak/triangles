package com.kozak.triangles.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * таблица с данными name-value
 * 
 * @author Roman: 21 июня 2015 г. 13:11:47
 */
@Entity(name = "vmap")
@Table(name = "vmap")
public class Vmap extends BaseEntity {

    @Column(name = "NAME", length = 20)
    private String name;

    @Column(name = "VALUE")
    private String value;

    public Vmap() {
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
