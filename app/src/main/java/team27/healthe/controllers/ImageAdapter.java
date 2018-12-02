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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import team27.healthe.R;

public class ImageAdapter extends RecyclerView.Adapter <ImageAdapter.ImageViewHolder> {
    private ArrayList<String> image_list;
    private Context context;
    private BodyLocationListener bl;
//    private RecyclerViewClickListener listener;
//    private ImageController ic;

    public ImageAdapter(Context c, ArrayList<String> image_list, BodyLocationListener bl){
        this.context = c;
        this.image_list = image_list;
        this.bl = bl;
//        this.ic = new ImageController(c, "body_locations");
    }

    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder viewHolder, int i) {
        String filename = image_list.get(i);
        if(filename.startsWith("\\w+_")){
            String r = "(^\\w+_)([\\w\\s]+)";
            Pattern pattern = Pattern.compile(r);
            Matcher m = pattern.matcher(filename);
            if(m.find()){
                viewHolder.title.setText(m.group(1));
            }
        }

        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get().load(filename).into(viewHolder.img);
        viewHolder.img.setImageBitmap(BitmapFactory.decodeFile(filename));

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
//            img.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v){
//                    bl.recyclerViewClicked(v, this.get);
//                }
//            });
        }
        @Override
        public void onClick(View v){
            System.out.println("DEBUG----CLICKED");
            bl.recyclerViewClicked(v, this.getLayoutPosition());
        }
    }


}
