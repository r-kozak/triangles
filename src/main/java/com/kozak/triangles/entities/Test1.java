package com.kozak.triangles.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "Test1")
@PrimaryKeyJoinColumn(name = "ID")
public class Test1 extends AbstrTest {

    @Column(name = "t1")
    private int t1;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "test1", cascade = CascadeType.ALL)
    private List<Test2> test2;

    public List<Test2> getTest2() {
	return test2;
    }

    public void setTest2(List<Test2> test2) {
	this.test2 = test2;
    }

    public int getT1() {
	return t1;
    }

    public void setT1(int t1) {
	this.t1 = t1;
    }

    public Test1() {
    }
}