package team27.healthe.model;

public class BodyLocationPhoto {
    /**
     * id of associated patient (body locations are unique to a patient)
     */
    String patient_id;
    /**
     * id for elasticsearch (and filename)
     */
    String body_location_photo_id;
    String body_location;

    public BodyLocationPhoto(){

    }

    public void setPatientId(String pid){
        patient_id = pid;
    }

    public String getPatientId(){
        return patient_id;
    }

    public void setBodyLocationPhotoId(String bl_id){
        body_location_photo_id = bl_id;
    }

    public String getBodyLocationPhotoId(){
        return body_location_photo_id;
    }

    public void setBodyLocation(String body_location){
        this.body_location = body_location;
    }

    public String getBodyLocation(){
        return body_location;
    }
}
