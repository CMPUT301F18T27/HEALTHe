package team27.healthe.controllers;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.*;

import java.io.File;
import java.util.ArrayList;
//TODO: remove as well (see other removal todo - server down)
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

import team27.healthe.R;
import team27.healthe.model.BodyLocationPhoto;

/**
 * Image adapter for holding body location images (and allow selection from RecyclerView)
 * @author Chris
 */
public class BodyLocationImageAdapter extends RecyclerView.Adapter <BodyLocationImageAdapter.ImageViewHolder> {
    private ArrayList<BodyLocationPhoto> image_list;
    private Context context;
    private BodyLocationListener bl;

    public BodyLocationImageAdapter(Context c, ArrayList<BodyLocationPhoto> image_list, BodyLocationListener bl){
        this.context = c;
        this.image_list = image_list;
        this.bl = bl;
    }

    @NonNull
    @Override
    public BodyLocationImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BodyLocationImageAdapter.ImageViewHolder viewHolder, int i) {
        String file_name = image_list.get(i).getBodyLocationPhotoId();
        String photo_file_name = context.getFilesDir().getAbsolutePath() + File.separator + file_name + ".jpg";


        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        viewHolder.img.setImageBitmap(BitmapFactory.decodeFile(photo_file_name));

    }

    @Override
    public int getItemCount() {
        return image_list.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView title;
        private ImageView img;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            img = (ImageView) itemView.findViewById(R.id.img);
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v){
            System.out.println("DEBUG----CLICKED");
            bl.recyclerViewClicked(v, this.getLayoutPosition());
        }
    }


}
