package com.kozak.triangles.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "User")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name = "login", length = 20)
    @Length(min = 4, max = 20, message = "Login length must be 4-20!")
    private String login;

    @Column(name = "password", length = 32)
    @Length(min = 4, max = 32, message = "Password length must be 4-32!")
    private String password;

    @Column(name = "email", length = 50)
    @Email(message = "this is not e-mail")
    private String email;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_enter")
    private Date lastEnter;

    // дата последнего бонуса
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_bonus")
    private Date lastBonus;

    // номер дня, за который был бонус
    @Column(name = "day_number")
    private int dayNumber;

    // доминантность
    @Column(name = "domi")
    private int domi;

    // //////// зашифрованный логин
    @Column(name = "encr_login")
    private String encrLogin;
    // /////////////////

    @Transient
    private String confirmPassword;

    @Transient
    private boolean authenticated;

    public String getLogin() {
	return login;
    }

    public void setLogin(String login) {
	this.login = login;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getConfirmPassword() {
	return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
	this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public boolean isAuthenticated() {
	return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
	this.authenticated = authenticated;
    }

    public Date getLastEnter() {
	return lastEnter;
    }

    public void setLastEnter(Date lastEnter) {
	this.lastEnter = lastEnter;
    }

    public int getDayNumber() {
	return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
	this.dayNumber = dayNumber;
    }

    public Date getLastBonus() {
	return lastBonus;
    }

    public void setLastBonus(Date lastBonus) {
	this.lastBonus = lastBonus;
    }

    public String getEncrLogin() {
	return encrLogin;
    }

    public void setEncrLogin(String encrLogin) {
	this.encrLogin = encrLogin;
    }

    public int getDomi() {
	return domi;
    }

    public void setDomi(int domi) {
	this.domi = domi;
    }
}
