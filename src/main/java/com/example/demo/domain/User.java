package com.example.demo.domain;

import lombok.Data;
import org.springframework.stereotype.Repository;

import javax.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = AUTO)
    private int id;

    private String username;

    private String name;

    private String phoneNumber;

    private String email;

    private Boolean isValidEmail;

    private Boolean isValidPhoneNumber;

    private Date birthday;

    private String contactId;

    private String languageCode;

    private Boolean isDeleted = false;

    private String password;

    private String oneTimePassword;

    private Date lastSentVerification;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getValidEmail() {
        return isValidEmail;
    }

    public void setValidEmail(Boolean validEmail) {
        isValidEmail = validEmail;
    }

    public Boolean getValidPhoneNumber() {
        return isValidPhoneNumber;
    }

    public void setValidPhoneNumber(Boolean validPhoneNumber) {
        isValidPhoneNumber = validPhoneNumber;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOneTimePassword() {
        return oneTimePassword;
    }

    public void setOneTimePassword(String oneTimePassword) {
        this.oneTimePassword = oneTimePassword;
    }

    public Date getLastSentVerification() {
        return lastSentVerification;
    }

    public void setLastSentVerification(Date lastSentVerification) {
        this.lastSentVerification = lastSentVerification;
    }

    public User(){}

    public User(String name, Date birthday, String languageCode, String password) {
        this.name = name;
        this.birthday = birthday;
        this.languageCode = languageCode;
        this.password = password;
    }

    public String toString(){
        return "{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", isValidEmail='" + isValidEmail.toString() + '\'' +
                ", isValidPhoneNumber='" + isValidPhoneNumber.toString() + '\'' +
                ", birthday='" + birthday.toString() + '\'' +
                ", contactId='" + contactId + '\'' +
                ", languageCode='" + languageCode + '\'' +
                ", isDeleted='" + isDeleted.toString() + '\'' +
                ", oneTimePassword='" + oneTimePassword + '\'' +
                ", lastSentVerification='" + lastSentVerification.toString() + '\'' +
                "}";
    }

    public HashMap<String, Object> getJson(){
        HashMap<String, Object> rs = new HashMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        rs.put("id", id);
        rs.put("username", username);
        rs.put("name", name);
        rs.put("phoneNumber", phoneNumber);
        rs.put("email", email);
        rs.put("isValidEmail", isValidEmail);
        rs.put("isValidPhoneNumber", isValidPhoneNumber);
        rs.put("birthday", (birthday != null) ? formatter.format(birthday) : "");
        rs.put("contactId", contactId);
        rs.put("languageCode", languageCode);

        return rs;
    }
}
