package com.eyupakky.mylibrary;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eyupakky.mylibrary.Pojo.SetBookData;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by eyupakkaya on 10.03.2017.
 */

public class  ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    ItemClickListener itemClickListener;
    List<SetBookData>list;
    Context context;

    public ItemAdapter(List<SetBookData>list, ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
        this.list=list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        View v= LayoutInflater.from(context).inflate(R.layout.list_item,null);
        final ViewHolder viewHolder=new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClickListener(view,viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SetBookData data = list.get(position);
        Picasso.with(context).load(R.mipmap.ic_launcher_round).into(holder.bookImage);
        holder.header.setText(data.getBookName());
        holder.explanation.setText(data.getBookExplanation());
        Picasso.with(context).load(data.getBookImageUri()).into(holder.bookImage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView bookImage;
        TextView header;
        TextView explanation;
        public ViewHolder(View v) {
            super(v);
            bookImage=v.findViewById(R.id.bookImage);
            header=v.findViewById(R.id.header);
            explanation=v.findViewById(R.id.explanation);

        }
    }
}
