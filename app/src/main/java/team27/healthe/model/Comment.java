package team27.healthe.model;

public class Comment {
    private String text;
    private User commenter;

    public Comment(String text, User commenter){
        this.text = text;
        this.commenter = commenter;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setCommenter(User commenter) {
        this.commenter = commenter;
    }

    public User getCommenter() {
        return this.commenter;
    }
}
