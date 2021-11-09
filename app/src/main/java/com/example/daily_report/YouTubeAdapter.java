package com.example.daily_report;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class YouTubeAdapter extends RecyclerView.Adapter<YouTubeAdapter.YouTubeViewHolder>{

    private ArrayList<YouTubeContent> youTubeContentArrayList;

    private SetYouTubeOnclickListener setYouTubeOnclickListener;



    public void setYoutubeOnclickListener(SetYouTubeOnclickListener setYouTubeOnclickListener){
        this.setYouTubeOnclickListener = setYouTubeOnclickListener;
    }
    public YouTubeAdapter(ArrayList<YouTubeContent> youTubeContentArrayList) {
        this.youTubeContentArrayList = youTubeContentArrayList;
    }

    @NonNull
    @Override
    public YouTubeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_youtube_search_result_layout,parent,false);

        YouTubeViewHolder viewHolder = new YouTubeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull YouTubeViewHolder holder, int position) {

        holder.youtubeTitle.setText(youTubeContentArrayList.get(position).getYoutubeName());
        holder.youtubeChannelId.setText(youTubeContentArrayList.get(position).getChannelId());
        holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        holder.youtubeLikeNumber.setText(youTubeContentArrayList.get(position).getLikeNumber());
        holder.youtubeViewCount.setText(youTubeContentArrayList.get(position).getViewCount());
        String imgUrl=youTubeContentArrayList.get(position).getImgUrl();

        Glide.with(holder.imageView).load(imgUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return youTubeContentArrayList.size();
    }

    public void setYouTubeContentArrayList(ArrayList<YouTubeContent> arrayList){

        this.youTubeContentArrayList=arrayList;

    }

    public class YouTubeViewHolder extends RecyclerView.ViewHolder{

        TextView youtubeTitle, youtubeChannelId,youtubeLikeNumber,youtubeViewCount;
        ImageView imageView;
        public YouTubeViewHolder(@NonNull View itemView) {
            super(itemView);

            youtubeTitle = itemView.findViewById(R.id.youtube_title);
            youtubeChannelId = itemView.findViewById(R.id.youtube_channel_id);
            imageView =itemView.findViewById(R.id.youtube_thumbnail_image);
            youtubeLikeNumber=itemView.findViewById(R.id.youtube_like_number);
            youtubeViewCount=itemView.findViewById(R.id.youtube_viewcount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAbsoluteAdapterPosition();

                    Log.e("123","adpater position : "+ position);

                    if(setYouTubeOnclickListener!=null){
                        setYouTubeOnclickListener.setOnClick(position);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int position = getAbsoluteAdapterPosition();
                    if(setYouTubeOnclickListener!=null){
                        setYouTubeOnclickListener.setLongClick(position);
                        return true;
                    }
                    return false;
                }
            });


        }

    }
}
