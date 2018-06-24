package com.learning.omar.firebaseuploadimages;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.MyViewHolder> {

    Context context;
    List<UploadModel> imagesArrayList;

    private OnItemClickListener onItemClickListener;


    public ImagesAdapter(Context context, List<UploadModel> umagesArrayList) {
        this.context = context;
        this.imagesArrayList = umagesArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        ButterKnife.bind(this, view);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        UploadModel uploadModel = imagesArrayList.get(position);
        holder.tvName.setText(uploadModel.getImg_name());
        Picasso.with(context)
                .load(uploadModel.getImg_url())
                .placeholder(R.drawable.ic_launcher_background)
                .fit()
                .centerCrop()
                .into(holder.imgUploaded);


    }

    @Override
    public int getItemCount() {
        return imagesArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener ,
            MenuItem.OnMenuItemClickListener{
        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.img_uploaded)
        ImageView imgUploaded;

        public MyViewHolder(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.OnItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select action");
            MenuItem doWhatever = contextMenu.add(Menu.NONE, 1, 1, "Do whatever");
            MenuItem delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");

            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (onItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
             switch (menuItem.getItemId())
             {
                 case 1 :
                     onItemClickListener.OnWhateverClick(position);
                     return true;

                 case 2 :
                     onItemClickListener.OndeleteClick(position);
                     return true;


             }
             }

            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);

        void OnWhateverClick(int position);

        void OndeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }
}
