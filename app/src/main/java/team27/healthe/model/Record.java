package team27.healthe.model;

import android.content.Context;
import android.media.Image;

import org.elasticsearch.common.UUID;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import team27.healthe.R;

/**
 * Represents a record (associated with a problem)
 * @author [fill in]
 */
public class Record {
    private String title;
    private Date rdate;
    private String description;

    private ArrayList<String> commentList;
    private String body_location_s; // @TODO: is this necessary?
    private BodyLocation bodyLocation;
    private String geo_location_s; // @TODO: is this necessary?
    private GeoLocation geoLocation;
    private ArrayList<Photo> photos;
    private String record_id;

    public Record(String ttl, Date date, String desc, ArrayList<String> comments,
                  BodyLocation bodyLoc, GeoLocation geoLoc, ArrayList<Photo> images) {

        // Note: either need to pass in context or add this file to the record activity
        // Not sure how this is used right now
        //body_location_s = c.getString(R.string.def_body_location_s);
        //geo_location_s = c.getString(R.string.def_geo_location_s);
        title = ttl;
        rdate = date;
        description = desc;
        commentList = comments;
        bodyLocation = bodyLoc;
        geoLocation = geoLoc;
        photos = images;
        record_id = UUID.randomUUID().toString();
    }

    //Temp constructor for testing, ignore it
    public Record(ArrayList<String> comments) {
        // Note: either need to pass in context or add this file to the record activity
        // Not sure how this is used right now
        // body_location_s = c.getString(R.string.def_body_location_s);
        // geo_location_s = c.getString(R.string.def_geo_location_s);
        title = "Test title";
        rdate = new Date();
        description = "This is a record";
        commentList = comments;
        bodyLocation = null;
        geoLocation = new GeoLocation();
        photos = new ArrayList();
        record_id = UUID.randomUUID().toString();
    }

    public Record(String ttl, Date date, String desc) {
        title = ttl;
        rdate = date;
        description = desc;
        commentList = new ArrayList<>();
        bodyLocation = null;
        geoLocation = new GeoLocation();
        photos = new ArrayList();
        record_id = UUID.randomUUID().toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getRdate() {
        return rdate;
    }

    public void setRdate(Date rdate) {
        this.rdate = rdate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getCommentList() {
        return commentList;
    }

    public void setCommentList(ArrayList<String> comments) {
        this.commentList = comments;
    }

    public void addCommment(String comment) { this.commentList.add(comment); }

    public BodyLocation getBodyLocation() {
        return bodyLocation;
    }

    public void setBodyLocation(BodyLocation bodyLoc) {
        this.bodyLocation = bodyLoc;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLoc) {
        this.geoLocation = geoLoc;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> images) {
        this.photos = images;
    }

    public void addPhoto(Photo photo) {this.photos.add(photo);}

    public String getRecordID(){
        return record_id;
    }

    public void setRecordID(String rid){
        record_id = rid;
    }

}
