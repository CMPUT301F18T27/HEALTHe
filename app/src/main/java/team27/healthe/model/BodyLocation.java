package team27.healthe.model;


/** Represents body location image
 * @author Chris
 *
 */
public class BodyLocation {
    /**
     * selected location on body location image (coordinates on image)
     */
    float x_location;
    float y_location;
    /**
     * id of associated patient (body locations are unique to a patient)
     */
    String patient_id;
    /**
     * identifier for this object (used as id in elasticsearch as well as filename)
     */
    String body_location_id;
    /**
     * title for this photo (given by user)
     * used for searching
     */
    String body_string;

    public BodyLocation(float x, float y){
        x_location = x;
        y_location = y;
        body_string = "";
    }

    public BodyLocation(){
        x_location = 0;
        y_location = 0;
        body_string = "";
    }

    public void setPoint(float x, float y){
        x_location = x;
        y_location = y;
    }

    public float getX(){
        return x_location;
    }
    public float getY(){
        return y_location;
    }

    public String getLocationName(){

        return body_string;
    }
    public void setLocation(String body_location){
        body_string = body_location;
    }

    public void setPatientId(String id){
        patient_id = id;
    }

    public String getPatientId(){
        return patient_id;
    }

    public void setBodyLocationId(String body_location_id){
        this.body_location_id = body_location_id;
    }

    public String getBodyLocationId(){
        return body_location_id;
    }
}
