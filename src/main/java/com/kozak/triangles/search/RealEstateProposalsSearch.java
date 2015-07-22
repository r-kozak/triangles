package com.kozak.triangles.search;

import java.util.ArrayList;
import java.util.List;

import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.enums.buildings.CommBuildingsT;

public class RealEstateProposalsSearch extends AbstractSearch {
    private List<CommBuildingsT> types = new ArrayList<CommBuildingsT>();
    private List<CityAreasT> areas = new ArrayList<CityAreasT>();
    private long priceFrom;
    private long priceTo;
    private long priceMin;
    private long priceMax;

    private String appearDateFrom = "";
    private String appearDateTo = "";

    private String lossDateFrom = "";
    private String lossDateTo = "";

    public void clear() {
	super.clear();
	types = new ArrayList<CommBuildingsT>();
	areas = new ArrayList<CityAreasT>();
	this.priceFrom = 0;
	this.priceTo = 0;
	this.appearDateFrom = "";
	this.appearDateTo = "";
	this.lossDateFrom = "";
	this.lossDateTo = "";
    }

    public void setPrice(Object minPrice, Object maxPrice) {
	this.setPriceMin((Long) minPrice);
	this.setPriceMax((Long) maxPrice);

	if (this.getPriceFrom() == 0) {
	    this.setPriceFrom((Long) minPrice);
	}
	if (this.getPriceTo() == 0) {
	    this.setPriceTo((Long) maxPrice);
	}
    }

    // ////////////////////////////////////
    public List<CommBuildingsT> getTypes() {
	return types;
    }

    public void setTypes(List<CommBuildingsT> types) {
	this.types = types;
    }

    public List<CityAreasT> getAreas() {
	return areas;
    }

    public void setAreas(List<CityAreasT> areas) {
	this.areas = areas;
    }

    public long getPriceFrom() {
	return priceFrom;
    }

    public void setPriceFrom(long priceFrom) {
	this.priceFrom = priceFrom;
    }

    public long getPriceTo() {
	return priceTo;
    }

    public void setPriceTo(long priceTo) {
	this.priceTo = priceTo;
    }

    public long getPriceMin() {
	return priceMin;
    }

    public void setPriceMin(long priceMin) {
	this.priceMin = priceMin;
    }

    public long getPriceMax() {
	return priceMax;
    }

    public void setPriceMax(long priceMax) {
	this.priceMax = priceMax;
    }

    public String getAppearDateFrom() {
	return appearDateFrom;
    }

    public void setAppearDateFrom(String appearDateFrom) {
	this.appearDateFrom = appearDateFrom;
    }

    public String getAppearDateTo() {
	return appearDateTo;
    }

    public void setAppearDateTo(String appearDateTo) {
	this.appearDateTo = appearDateTo;
    }

    public String getLossDateFrom() {
	return lossDateFrom;
    }

    public void setLossDateFrom(String lossDateFrom) {
	this.lossDateFrom = lossDateFrom;
    }

    public String getLossDateTo() {
	return lossDateTo;
    }

    public void setLossDateTo(String lossDateTo) {
	this.lossDateTo = lossDateTo;
    }
}
