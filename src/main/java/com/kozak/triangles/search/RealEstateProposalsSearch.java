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

    private String dateStartFrom = "";
    private String dateStartTo = "";

    private String dateEndFrom = "";
    private String dateEndTo = "";

    public void clear() {
        super.clear();
        types = new ArrayList<CommBuildingsT>();
        areas = new ArrayList<CityAreasT>();
        this.priceFrom = 0;
        this.priceTo = 0;
        this.dateStartFrom = "";
        this.dateStartTo = "";
        this.dateEndFrom = "";
        this.dateEndTo = "";
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

    public String getDateStartFrom() {
        return dateStartFrom;
    }

    public void setDateStartFrom(String dateStartFrom) {
        this.dateStartFrom = dateStartFrom;
    }

    public String getDateStartTo() {
        return dateStartTo;
    }

    public void setDateStartTo(String dateStartTo) {
        this.dateStartTo = dateStartTo;
    }

    public String getDateEndFrom() {
        return dateEndFrom;
    }

    public void setDateEndFrom(String dateEndFrom) {
        this.dateEndFrom = dateEndFrom;
    }

    public String getDateEndTo() {
        return dateEndTo;
    }

    public void setDateEndTo(String dateEndTo) {
        this.dateEndTo = dateEndTo;
    }

}
