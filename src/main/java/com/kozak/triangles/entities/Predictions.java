package com.kozak.triangles.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * таблица со всеми предсказаниями
 */
@Entity(name = "Predictions")
@Table(name = "Predictions")
public class Predictions extends BaseEntity {

    @Column(name = "PREDICTION")
    private String prediction;

    public Predictions() {
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }
}
