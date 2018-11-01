package team27.healthe;

import android.content.Context;

public class Record {
    private String body_location_s;
    private BodyLocation body_location;
    private String  geo_location_s;
    private GeoLocation geo_location;
//    private CommentList comment_list;
    private ImageList image_list;

    public Record(Context c) {

        // Note: either need to pass in context or add this file to the record activity
        body_location_s = c.getString(R.string.def_body_location_s);
        geo_location_s = c.getString(R.string.def_geo_location_s);
    }


}
