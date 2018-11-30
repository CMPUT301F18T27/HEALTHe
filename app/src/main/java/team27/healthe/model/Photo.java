package team27.healthe.model;

public class Photo {
    private String id;
    private BodyLocation body_location;

    public Photo () {
        id = null;
        body_location = null;
    }

    public Photo(String id) {
        this.id = id;
    }

    public Photo(String id, BodyLocation body_location) {
        this.id = id;
        this.body_location = body_location;
    }

    public String getId() {return id;}

    public void setId(String key) {this.id = key;}

    public BodyLocation getBodyLocation() {return body_location;}

    public void setBodyLocation(BodyLocation body_location) {this.body_location = body_location;}

}
