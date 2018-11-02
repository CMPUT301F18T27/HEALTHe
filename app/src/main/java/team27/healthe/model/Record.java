package team27.healthe.model;

import android.content.Context;
import android.media.Image;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import team27.healthe.R;

public class Record {
    private String title;
    private Date rdate;
    private String description;

    private CommentList commentList;
    private String body_location_s; // @TODO: is this necessary?
    private BodyLocation bodyLocation;
    private String geo_location_s; // @TODO: is this necessary?
    private GeoLocation geoLocation;
    private ImageList imageList;

    public Record(String ttl, Date date, String desc, CommentList comments,
                  BodyLocation bodyLoc, GeoLocation geoLoc, ImageList images) {

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
        imageList = images;
    }

    public Record(String ttl) {
        title = ttl;
        rdate = Calendar.getInstance().getTime();
        description = null; // not sure if I can actually do this
        commentList = null;
        bodyLocation = null;
        geoLocation = null;
        imageList = null;
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

    public CommentList getCommentList() {
        return commentList;
    }

    public void setCommentList(CommentList comments) {
        this.commentList = comments;
    }

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

    public ImageList getImageList() {
        return imageList;
    }

    public void setImageList(ImageList images) {
        this.imageList = images;
    }

}
