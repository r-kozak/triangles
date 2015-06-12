package com.kozak.triangles.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.kozak.triangles.interfaces.BuildingTypes;

/**
 * Рынок недвижимости
 * 
 * buildingType - тип строения (Киоск, Маркет, ...)
 * appearDate - дата появления на рынке
 * lossDate - дата ухода с рынка
 * valid - предложение еще действительно
 * 
 * 
 * 
 * @author Roman: 12 июня 2015 г. 22:25:55
 */

@Entity
@Table(name = "r_e_market")
public class RealEstateMarket {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "building_type")
	private BuildingTypes buildingType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "appear_date")
	private Date appearDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "loss_date")
	private Date lossDate;

	@Column(name = "valid")
	private boolean valid;

}
