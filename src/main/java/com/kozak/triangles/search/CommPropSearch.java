package com.kozak.triangles.search;

import java.util.ArrayList;
import java.util.List;

import com.kozak.triangles.enums.buildings.CommBuildingsT;

public class CommPropSearch extends AbstractSearch {
    private String name = "";
    private List<CommBuildingsT> types = new ArrayList<CommBuildingsT>();
    private long sellPriceFrom;
    private long sellPriceTo;
    private long sellPriceMin;
    private long sellPriceMax;
    private double depreciationMin;
    private double depreciationMax;
    private double depreciationFrom;
    private double depreciationTo;

    public void clear() {
        super.clear();
        this.name = "";
        types = new ArrayList<CommBuildingsT>();
        this.sellPriceFrom = 0;
        this.sellPriceTo = 0;
        this.depreciationFrom = 0.0;
        this.depreciationTo = 0.0;
    }

    public void setPrice(Object minPrice, Object maxPrice) {
        this.setSellPriceMin((Long) minPrice);
        this.setSellPriceMax((Long) maxPrice);

        if (this.getSellPriceFrom() == 0) {
            this.setSellPriceFrom((Long) minPrice);
        }
        if (this.getSellPriceTo() == 0) {
            this.setSellPriceTo((Long) maxPrice);
        }
    }

    public void setDepreciation(Object minPerc, Object maxPerc) {
        this.setDepreciationMin((Double) minPerc);
        this.setDepreciationMax(Math.ceil((Double) maxPerc));

        if (this.getDepreciationFrom() == 0) {
            // получить минимальный процент износа
            this.setDepreciationFrom((Double) minPerc);
        }
        if (this.getDepreciationTo() == 0) {
            // получить максимальный процент износа
            this.setDepreciationTo(Math.ceil((Double) maxPerc));
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

    public double getDepreciationMin() {
        return depreciationMin;
    }

    public void setDepreciationMin(double depreciationMin) {
        this.depreciationMin = depreciationMin;
    }

    public double getDepreciationMax() {
        return depreciationMax;
    }

    public void setDepreciationMax(double depreciationMax) {
        this.depreciationMax = depreciationMax;
    }
}
