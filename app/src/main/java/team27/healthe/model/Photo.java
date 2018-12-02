package team27.healthe.model;

import org.elasticsearch.common.UUID;

/**
 * Class for representing record images
 * @author [fill in]
 */
public class Photo {
    private String id;
    private BodyLocation body_location;

    /**
     * TODO: explain
     */
    public Photo () {
        id = UUID.randomUUID().toString();
        body_location = null;
    }

    public Photo(String id) {
        this.id = id;
        body_location = null;
    }

    public Photo(BodyLocation body_location) {
        this.id = id;
        this.body_location = body_location;
    }

    public String getId() {return id;}

    public void setId(String key) {this.id = key;}

    public BodyLocation getBodyLocation() {return body_location;}

    public void setBodyLocation(BodyLocation body_location) {this.body_location = body_location;}

}
