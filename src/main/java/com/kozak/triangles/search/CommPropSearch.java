package com.kozak.triangles.search;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import com.kozak.triangles.entities.Property;
import com.kozak.triangles.enums.buildings.CommBuildingsT;

public class CommPropSearch extends AbstractSearch {
    private String name;
    private List<CommBuildingsT> types;
    private long sellPriceFrom;
    private long sellPriceTo;
    private long sellPriceMin;
    private long sellPriceMax;
    private double depreciationFrom;
    private double depreciationTo;

    public void clear() {
	this.name = "";
	setNeedClear(false);
	setPage("1");
	types = new ArrayList<CommBuildingsT>();
	this.sellPriceFrom = 0;
	this.sellPriceTo = 0;
	this.depreciationFrom = 0.0;
	this.depreciationTo = 0.0;
    }

    public void setPrice(List<Property> comProps) {
	TreeSet<Long> sellingPrices = new TreeSet<Long>();
	for (Property prop : comProps) {
	    sellingPrices.add(prop.getSellingPrice());
	}
	this.setSellPriceMin(sellingPrices.first());
	this.setSellPriceMax(sellingPrices.last());
	if (this.getSellPriceFrom() == 0) {
	    // получить минимальную цену имущества
	    this.setSellPriceFrom(sellingPrices.first());
	}
	if (this.getSellPriceTo() == 0) {
	    // получить максимальную цену имущества
	    this.setSellPriceTo(sellingPrices.last());
	}
    }

    public void setDepreciation(List<Property> comProps) {
	TreeSet<Double> depreciations = new TreeSet<Double>();
	for (Property prop : comProps) {
	    depreciations.add(prop.getDepreciationPercent());
	}
	if (this.getDepreciationFrom() == 0) {
	    // получить минимальный процент износа
	    this.setDepreciationFrom(depreciations.first());
	}
	if (this.getDepreciationTo() == 0) {
	    // получить максимальный процент износа
	    this.setDepreciationTo(Math.ceil(depreciations.last()));
	}
    }

    // ////////////////////////////////////
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<CommBuildingsT> getTypes() {
	return types;
    }

    public void setTypes(List<CommBuildingsT> types) {
	this.types = types;
    }

    public long getSellPriceFrom() {
	return sellPriceFrom;
    }

    public void setSellPriceFrom(long sellPriceFrom) {
	this.sellPriceFrom = sellPriceFrom;
    }

    public long getSellPriceTo() {
	return sellPriceTo;
    }

    public void setSellPriceTo(long sellPriceTo) {
	this.sellPriceTo = sellPriceTo;
    }

    public long getSellPriceMin() {
	return sellPriceMin;
    }

    public void setSellPriceMin(long sellPriceMin) {
	this.sellPriceMin = sellPriceMin;
    }

    public long getSellPriceMax() {
	return sellPriceMax;
    }

    public void setSellPriceMax(long sellPriceMax) {
	this.sellPriceMax = sellPriceMax;
    }

    public double getDepreciationFrom() {
	return depreciationFrom;
    }

    public void setDepreciationFrom(double depreciationFrom) {
	this.depreciationFrom = depreciationFrom;
    }

    public double getDepreciationTo() {
	return depreciationTo;
    }

    public void setDepreciationTo(double depreciationTo) {
	this.depreciationTo = depreciationTo;
    }
}
