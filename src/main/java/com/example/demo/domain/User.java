package com.example.demo.domain;

import com.dropbox.core.v2.DbxClientV2;
import com.example.demo.DropBox.DropBoxAuthenticator;
import com.example.demo.DropBox.DropBoxUploader;
import com.example.demo.Helpers.UserHelper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Reference;
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

    private Boolean isValidEmail = false;

    private Boolean isValidPhoneNumber = false;

    private Date birthday;

    private String contactId;

    private String languageCode;

    private Boolean isDeleted = false;

    private String password;

    private String oneTimePassword;

    private Date lastSentVerification;

    private String avatar;

    //    @Column(name = "social_account_type")
    @ManyToOne
    @JoinColumn(name = "social_account_type", referencedColumnName = "id")
    private SocialAccountType socialAccountType;

    @Column(name = "reset_pw_otp")
    private String resetPasswordOTP;

    public String getResetPasswordOTP() {
        return resetPasswordOTP;
    }

    public void setResetPasswordOTP(String resetPasswordOTP) {
        this.resetPasswordOTP = resetPasswordOTP;
    }

    public Date getResetPasswordOTPIssuedAt() {
        return resetPasswordOTPIssuedAt;
    }

    public void setResetPasswordOTPIssuedAt(Date resetPasswodOTPIssedAt) {
        this.resetPasswordOTPIssuedAt = resetPasswodOTPIssedAt;
    }

    @Column(name = "reset_pw_otp_issued_at")
    private Date resetPasswordOTPIssuedAt;

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


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public SocialAccountType getSocialAccountType() {
        return socialAccountType;
    }

    public void setSocialAccountType(SocialAccountType socialAccountType) {
        this.socialAccountType = socialAccountType;
    }

    public User() {
    }

    public User(String name, Date birthday, String languageCode, String password) {
        this.name = name;
        this.birthday = birthday;
        this.languageCode = languageCode;
        this.password = password;
    }

    public String toString() {
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

    public HashMap<String, Object> getJson() {
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
        rs.put("languageCode", (languageCode == null) ? null : languageCode.trim());
        rs.put("authType", socialAccountType.getJson());
        rs.put("avatar", getAvatar());

        return rs;
    }
}
