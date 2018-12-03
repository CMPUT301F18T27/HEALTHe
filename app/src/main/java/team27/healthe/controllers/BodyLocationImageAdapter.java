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
//TODO: remove as well (see other removal todo - server down)
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

import team27.healthe.R;

/**
 * Image adapter for holding body location images (and allow selection from RecyclerView)
 * @author Chris
 */
public class BodyLocationImageAdapter extends RecyclerView.Adapter <BodyLocationImageAdapter.ImageViewHolder> {
//    private ArrayList<String> image_list;
    private Context context;
    private BodyLocationListener bl;
    private ImageController ic;

    public BodyLocationImageAdapter(Context c,  ImageController image_controller, BodyLocationListener bl){
        this.context = c;
        ic = image_controller;
//        this.image_list = image_list;
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
        String filename = ic.get(i);
        // TODO: Test this removal when elasticsearch server is back
//        if(filename.startsWith("\\w+_")){
//            String r = "(^\\w+_)([\\w\\s]+)";
//            Pattern pattern = Pattern.compile(r);
//            Matcher m = pattern.matcher(filename);
//            if(m.find()){
//                viewHolder.title.setText(m.group(1));
//            }
//        }
        System.out.println("Here!\n filename: "+filename);

        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get().load(filename).into(viewHolder.img);
        viewHolder.img.setImageBitmap(BitmapFactory.decodeFile(filename));

    }

    @Override
    public int getItemCount() {
        return ic.size();
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
