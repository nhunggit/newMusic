package com.bkav.newMusic;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.WordViewHolder> {
    ArrayList<Song> mSong;
    Context mcontext;
    OnClickItemView onClickItemView;
    MediaPlaybackService myService;
    int k=0;
    TextView nameSong;

    public SongAdapter() {
    }

    public void setNameSong(TextView nameSong) {
        this.nameSong = nameSong;
    }

    public Context getMcontext() {
        return mcontext;
    }

    public void setMcontext(Context mcontext) {
        this.mcontext = mcontext;
    }

    public TextView getNameSong() {
        return nameSong;
    }

    public void setMyService(MediaPlaybackService myService) {
        this.myService = myService;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int getK() {
        return k;
    }

    public void setmSong(ArrayList<Song> mSong) {
        this.mSong = mSong;
    }

    public SongAdapter(ArrayList<Song> mSong, Context mcontext) {
        this.mSong = mSong;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_baihat, parent, false);
        return new WordViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final WordViewHolder holder, final int position) {
        holder.mstt.setText(mSong.get(position).getId() + "");
        holder.mnameSong.setText(mSong.get(position).getTitle());
        SimpleDateFormat formmatTime = new SimpleDateFormat("mm:ss");
        holder.mHours.setText(formmatTime.format(mSong.get(position).getDuration()));
        holder.mMore.setImageResource(R.drawable.ic_more_vert_black_24dp);
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItemView.ClickItem(position);
            }
        });
        if(myService!=null){
            if(myService.getNameSong().equals(mSong.get(position).getTitle())){
                holder.mnameSong.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
                holder.mstt.setText("");
                holder.mstt.setBackgroundResource(R.drawable.ic_equalizer_black_24dp);
            }else{
                holder.mstt.setText(mSong.get(position).getId() + "");
                holder.mnameSong.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
            }
        }




       // Log.d("position", "onBindViewHolder: "+k);


//        if(myService!=null){
//            Log.d("service", "onBindViewHolder: "+"ok");
//            if((myService.getNameSong()).equals(mSong.get(position).getTitle())==true){
//                holder.mnameSong.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
//                holder.mstt.setText("");
//                holder.mstt.setBackgroundResource(R.drawable.ic_equalizer_black_24dp);
//            }
//        }
//        else{
//            holder.mstt.setText(mSong.get(position).getId() + "");
//            holder.mnameSong.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
//        }
    }

    public void setOnClickItemView(OnClickItemView onClickItemView) {
        this.onClickItemView = onClickItemView;
    }

    @Override
    public int getItemCount() {
        return mSong.size();
    }


    public class WordViewHolder extends RecyclerView.ViewHolder  {
        TextView mstt;
        TextView mnameSong;
        TextView mHours;
        ImageButton mMore;
        ConstraintLayout constraintLayout;

        final SongAdapter mAdapter;

        public WordViewHolder(@NonNull View itemView, SongAdapter adapter) {
            super(itemView);
            this.mAdapter = adapter;
            mstt = (TextView) itemView.findViewById(R.id.stt);
            mnameSong = (TextView) itemView.findViewById(R.id.namesong);
            mHours = (TextView) itemView.findViewById(R.id.hours);
            mMore = (ImageButton) itemView.findViewById(R.id.more);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraintLayoutItem);
        }
    }


    interface OnClickItemView {
        void ClickItem(int position);

    }
}

