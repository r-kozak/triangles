package com.kozak.triangles.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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
	@Column(name = "ID")
    @GeneratedValue
    private Integer id;

	@Column(name = "LOGIN", length = 20)
    @Length(min = 4, max = 20, message = "Длина логина должна быть 4-20!")
    private String login;

	@Column(name = "PASSWORD", length = 32)
    @Length(min = 4, max = 32, message = "Длина пароля должна быть 4-32!")
    private String password;

	@Column(name = "EMAIL", length = 50)
    @Email(message = "Это не e-mail!")
    private String email;

    @Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_ENTER")
    private Date lastEnter;

    // дата последнего бонуса
    @Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_BONUS")
    private Date lastBonus;

    // номер дня, за который был бонус
	@Column(name = "DAY_NUMBER")
    private int dayNumber;

    // доминантность
	@Column(name = "DOMI")
    private int domi;

    // //////// зашифрованный логин
	@Column(name = "ENCR_LOGIN")
    private String encrLogin;
    // /////////////////

    // лицензии юзера
    @OneToOne(cascade = CascadeType.ALL)
    private UserLicense userLicense;

    // количество лотерейных билетов у пользователя
	@Column(name = "LOTTERY_TICKETS")
    private int lotteryTickets;

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

    public UserLicense getUserLicense() {
        return userLicense;
    }

    public void setUserLicense(UserLicense userLicense) {
        this.userLicense = userLicense;
    }

    public void setDomi(int domi) {
        this.domi = domi;
    }

    public int getLotteryTickets() {
        return lotteryTickets;
    }

    public void setLotteryTickets(int lotteryTickets) {
        this.lotteryTickets = lotteryTickets;
    }
}
