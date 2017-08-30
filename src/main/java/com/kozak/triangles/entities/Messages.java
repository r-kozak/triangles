package com.kozak.triangles.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.kozak.triangles.enums.MessageLevel;

/**
 * сообщения из беседки
 */
@Entity(name = "Messages")
@Table(name = "Messages")
public class Messages extends BaseEntity {

    // логин автора
    @Column(name = "AUTHOR")
    private String author;

    // текст сообщения
    @Column(name = "MESSAGE", columnDefinition = "TEXT")
    private String message;

    // уровень сообщения
    @Column(name = "LEVEL")
    @Enumerated
    private MessageLevel level = MessageLevel.DEFAULT;

    // дата сообщения
    @Column(name = "DATE")
    private Date date = new Date();

    public Messages() {
    }

    public Messages(String author, String message) {
        this.author = author;
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageLevel getLevel() {
        return level;
    }

    public void setLevel(MessageLevel level) {
        this.level = level;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
