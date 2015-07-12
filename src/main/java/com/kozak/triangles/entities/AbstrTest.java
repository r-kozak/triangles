package com.kozak.triangles.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "at")
@Inheritance(strategy = InheritanceType.JOINED)
public class AbstrTest {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "at1")
    private int at1;

    @Column(name = "at2")
    private int at2;

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public int getAt1() {
	return at1;
    }

    public void setAt1(int at1) {
	this.at1 = at1;
    }

    public int getAt2() {
	return at2;
    }

    public void setAt2(int at2) {
	this.at2 = at2;
    }

    public AbstrTest() {
    }
}
