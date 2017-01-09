package com.company;

import java.sql.Date;

/**
 * Created by mac on 2016. 12. 13..
 */
public class Domain {
    private int offerId;

    private int companyId;
    private String companyName;

    private String offerSubject;

    private String offerRegistDate;
    private String offerState;

    private String offerExpireDate;

    private String offerExp;

    private String skillName;
    private int skillCount;
    private String position;

    /* common */
    private int id;
    private Date created;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public int getSkillCount() {
        return skillCount;
    }

    public void setSkillCount(int skillCount) {
        this.skillCount = skillCount;
    }

    public String getPosition() {
        return position;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public String getOfferSubject() {
        return offerSubject;
    }

    public void setOfferSubject(String offerSubject) {
        this.offerSubject = offerSubject;
    }

    public String getOfferRegistDate() {
        return offerRegistDate;
    }

    public void setOfferRegistDate(String offerRegistDate) {
        this.offerRegistDate = offerRegistDate;
    }

    public String getOfferState() {
        return offerState;
    }

    public void setOfferState(String offerState) {
        this.offerState = offerState;
    }

    public String getOfferExpireDate() {
        return offerExpireDate;
    }

    public void setOfferExpireDate(String offerexpireDate) {
        this.offerExpireDate = offerexpireDate;
    }

    public String getOfferExp() {
        return offerExp;
    }

    public void setOfferExp(String offerExp) {
        this.offerExp = offerExp;
    }
}
