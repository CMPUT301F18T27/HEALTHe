package team27.healthe;

public class Comment {
    String uid;
    String content;

    public Comment(String c, String u){
        content = c;
        uid = u;
    }

    public String getUserid(){
        return uid;
    }

    public String getContent(){
        return content;
    }
}
