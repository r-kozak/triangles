package com.kozak.triangles.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "Test2")
@PrimaryKeyJoinColumn(name = "ID")
public class Test2 extends AbstrTest {

    @Column(name = "t2")
    private int t2;

    @OneToOne
    private Test1 test1;

    public Test1 getTest1() {
	return test1;
    }

    public void setTest1(Test1 test1) {
	this.test1 = test1;
    }

    public int getT2() {
	return t2;
    }

    public void setT2(int t2) {
	this.t2 = t2;
    }

    public Test2() {
    }
}