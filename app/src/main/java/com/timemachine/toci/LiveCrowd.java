package com.timemachine.toci;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Victor Ruelas on 3/7/16.
 */
public class LiveCrowd {

    private String id;
    private String title;
    private String address;
    private String latlng;
    private String city;
    private String timeago;
    private float distance;
    private HashMap<Integer, ArrayList<String>> picUrls;

    /**
     * Empty constructor
     */
    public LiveCrowd() {
        // Default constructor
    }

    /**
     * LiveCrowd constructor
     * @param id
     * @param title
     * @param address
     * @param latlng
     * @param city
     * @param timeago
     * @param distance
     * @param picUrls
     */
    public LiveCrowd(String id, String title, String address, String latlng, String city,
                     String timeago, float distance, HashMap picUrls) {
        super();
        this.id = id;
        this.title = title;
        this.address = address;
        this.latlng = latlng;
        this.city = city;
        this.timeago = timeago;
        this.distance = distance;
        this.picUrls = picUrls;
    }

    /**
     * Return LiveCrowd's id
     * @return
     */
    public String getId() {
        return this.id;
    }

    /**
     * Return LiveCrowd's title
     * @return
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Return LiveCrowd's address
     * @return
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Return LiveCrowd's latitude and longitude
     * @return
     */
    public String getLatlng() {
        return this.latlng;
    }

    /**
     * Return LiveCrowd's city
     * @return
     */
    public String getCity() {
        return this.city;
    }

    /**
     * Return time ago to display on crowd in the list
     * @return
     */
    public String getTimeago() {
        return this.timeago;
    }

    /**
     * Return distance to LiveCrowd from current location
     * @return
     */
    public float getDistance() {
        return this.distance;
    }

    /**
     * Return urls of LiveCrowds pictures
     * @return
     */
    public HashMap<Integer, ArrayList<String>> getPicUrls() {
        return this.picUrls;
    }

    /**
     * Set city
     * @param city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Set id
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Set distance
     * @param distance
     */
    public void setDistance(float distance) {
        this.distance = distance;
    }

    /**
     * Set time ago to display on crowd in the list
     * @param timeago
     */
    public void setTimeago(String timeago) {
        this.timeago = timeago;
    }

    /**
     * Set title
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set picture urls
     * @param picUrls
     */
    public void setPicUrls(HashMap<Integer, ArrayList<String>> picUrls) {
        this.picUrls = picUrls;
    }

    /**
     * Stringified LiveCrowd
     * @return
     */
    @Override
    public String toString() {
        return "LiveCrowd [id=" + id + ", title=" + title + ", " + "address=" + address +
                ", latlng="+ latlng + ", city=" + city + ", timeago=" + timeago +
                ", distance=" + distance + ", picUrls=" + picUrls + "]";
    }
}
