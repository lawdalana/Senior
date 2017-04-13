package com.Senior.Faff.model;

/**
 * Created by Not_Today on 12/15/2016.
 */

import android.provider.BaseColumns;

import java.util.ArrayList;

public class Promotion {

    public static final String TABLE = "promotion";

    public class Column {
        public static final String ID = BaseColumns._ID;
        public static final String Title = "Title";
        public static final String StartDate = "StartDate";
        public static final String EndDate = "EndDate";
        public static final String PromotionDetail = "PromotionDetail";
        public static final String Location = "Location";
        public static final String Type = "Type";
    }

    private int id;
    private String title;
    private String startDate;
    private String endDate;
    private String promotionDetail;
    private String googleMapLink;
    private String type;

    // Constructor

    public Promotion(String title, String startDate, String endDate, String promotionDetail, String googleMapLink, String type) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.promotionDetail = promotionDetail;
        this.googleMapLink = googleMapLink;
        this.type = type;
    }


    //region getter setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPromotionDetail() {
        return promotionDetail;
    }

    public void setPromotionDetail(String promotionDetail) {
        this.promotionDetail = promotionDetail;
    }

    public String getGoogleMapLink() {
        return googleMapLink;
    }

    public void setGoogleMapLink(String googleMapLink) {
        this.googleMapLink = googleMapLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    //endregion
}
