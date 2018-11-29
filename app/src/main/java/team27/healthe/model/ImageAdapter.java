package team27.healthe.model;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;
import com.squareup.picasso.*;
import java.util.ArrayList;

import team27.healthe.R;

public class ImageAdapter extends RecyclerView.Adapter <ImageAdapter.ImageViewHolder> {
    private ArrayList<String> image_list;
    private Context context;

    public ImageAdapter(Context c, ArrayList<String> image_list){
        this.context = c;
        this.image_list = image_list;
    }

    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder viewHolder, int i) {
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get().load(image_list.get(i)).into(viewHolder.img);
//        viewHolder.img.setImageResource();

    }

    @Override
    public int getItemCount() {
        return image_list.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
//        private TextView title;
        private ImageView img;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
//            title = (TextView) itemView.findViewById(R.id.title);
            img = (ImageView) itemView.findViewById(R.id.img);
        }
    }


}
