package team27.healthe.model;

// Class for holding a base64 string of a photo for insertion into elastic search
public class ElasticSearchPhoto {
    private String base64image;

    public ElasticSearchPhoto (String base64_string) {
        this.base64image = base64_string;
    }

    public String getBase64String() {
        return base64image;
    }

    public void setBase64String(String base64_string) {
        this.base64image = base64_string;
    }

}
