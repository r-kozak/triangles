package com.kozak.triangles.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * таблица с данными name-value
 * 
 * @author Roman: 21 июня 2015 г. 13:11:47
 */
@Entity(name = "vmap")
@Table(name = "vmap")
public class Vmap {
    @Id
	@Column(name = "ID")
    @GeneratedValue
    private Integer id;

	@Column(name = "NAME", length = 20)
    private String name;

	@Column(name = "VALUE")
    private String value;

    public Vmap() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
