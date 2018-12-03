package team27.healthe.model;

import org.elasticsearch.common.UUID;

/**
 * Class for representing record images
 * @author Chase
 */
public class Photo {
    private String id;

    /**
     * TODO: explain
     */
    public Photo () {
        id = UUID.randomUUID().toString();
    }

    public Photo(String id) {
        this.id = id;
    }

    public Photo(BodyLocation body_location) {
        this.id = id;
    }

    public String getId() {return id;}

    public void setId(String key) {this.id = key;}


}
