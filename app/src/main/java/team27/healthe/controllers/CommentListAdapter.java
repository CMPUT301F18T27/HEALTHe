package team27.healthe.controllers;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import team27.healthe.R;
import team27.healthe.model.Comment;

/**
 * Comment adapter for holding comments in a ListView
 * @author Cody
 */
public class CommentListAdapter extends ArrayAdapter<Comment> {
    private final Activity context;
    private ArrayList<Comment> comments; //Array of patient ids

    //Constructor
    public CommentListAdapter(Activity context, ArrayList<Comment> comments) {
        super(context, R.layout.comment_list_item, comments);
        this.context = context;
        this.comments = comments;
    }

    /**
     * gets the list view and called for each row and item in the listview
     * @param position (int)
     * @param view (the view)
     * @param parent
     * @return (list_row) returns the view of the row
     */
    public View getView (int position, View view, ViewGroup parent) {
        Comment comment = getItem(position); // get patient at current position

        // inflate and get the view layout from the patient_list_item.xml file
        LayoutInflater inflater = context.getLayoutInflater();
        View list_row = inflater.inflate(R.layout.comment_list_item, parent, false);

        TextView author_text = (TextView) list_row.findViewById(R.id.textCommentAuthor);
        TextView comment_text = (TextView) list_row.findViewById(R.id.textComment);

        // Set the two text items in the row to the emotion type and emotion date
       author_text.setText(comment.getCommenter() + ":");
       comment_text.setText(comment.getText());

        return list_row; // return the view(row)
    }

    /**
     * recreates the listview when changes are made
     * @param comments (list of comments)
     */
    public void refresh(ArrayList<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }}
