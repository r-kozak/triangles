package com.kozak.triangles.search;

import java.util.ArrayList;
import java.util.List;

import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.TradeBuildingType;

public class RealEstateProposalsSearch extends AbstractSearch {
    private List<TradeBuildingType> types = new ArrayList<TradeBuildingType>();
    private List<CityArea> areas = new ArrayList<CityArea>();
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
        types = new ArrayList<TradeBuildingType>();
        areas = new ArrayList<CityArea>();
        this.priceFrom = 0;
        this.priceTo = 0;
        this.appearDateFrom = "";
        this.appearDateTo = "";
        this.lossDateFrom = "";
        this.lossDateTo = "";
    }

    public void setPrice(Object minPrice, Object maxPrice) {
        this.setPriceMin((long) minPrice);
        this.setPriceMax((long) maxPrice);

        if (this.getPriceFrom() == 0) {
            this.setPriceFrom((long) minPrice);
        }
        if (this.getPriceTo() == 0) {
            this.setPriceTo((long) maxPrice);
        }
    }

    // ////////////////////////////////////
    public List<TradeBuildingType> getTypes() {
        return types;
    }

    public void setTypes(List<TradeBuildingType> types) {
        this.types = types;
    }

    public List<CityArea> getAreas() {
        return areas;
    }

    public void setAreas(List<CityArea> areas) {
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
