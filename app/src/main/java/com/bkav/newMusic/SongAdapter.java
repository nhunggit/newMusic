package com.bkav.newMusic;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.WordViewHolder> {
    private ArrayList<Song> mListSong;
    private Context mContext;
    private OnClickItemView iOnClickItemView;
    private MediaPlaybackService mMediaPlaybackService;
    private String mTypeSong="";


    public void updateList(ArrayList<Song> songs){
        mListSong =songs;
        notifyDataSetChanged();
    }

    public void setmTypeSong(String mTypeSong) {
        this.mTypeSong = mTypeSong;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmMediaPlaybackService(MediaPlaybackService mMediaPlaybackService) {
        this.mMediaPlaybackService = mMediaPlaybackService;
    }

    public void setmListSong(ArrayList<Song> mListSong) {
        this.mListSong = mListSong;
    }

    public SongAdapter(ArrayList<Song> mSong, Context mcontext) {
        this.mListSong = mSong;
        this.mContext = mcontext;
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

        holder.mstt.setText(mListSong.get(position).getId() + "");
        holder.mnameSong.setText(mListSong.get(position).getTitle());
        SimpleDateFormat formmatTime = new SimpleDateFormat("mm:ss");
        holder.mHours.setText(formmatTime.format(mListSong.get(position).getDuration()));
        holder.mMore.setImageResource(R.drawable.ic_more_vert_black_24dp);
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOnClickItemView.ClickItem(position);
            }
        });

        if(mMediaPlaybackService !=null){
            //Log.d("adapter", "onBindViewHolder: "+"ok");
            if((mMediaPlaybackService.getNameSong()).equals(mListSong.get(position).getTitle())==true){
                Log.d("compare", "onBindViewHolder: "+ mMediaPlaybackService.getNameSong()+ mListSong.get(position).getTitle());
                holder.mnameSong.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
                holder.mstt.setText("");
                holder.mstt.setBackgroundResource(R.drawable.ic_equalizer_black_24dp);
            }
            else{
                holder.mstt.setBackgroundResource(R.drawable.ic_equalizer_while_24dp);
                holder.mstt.setText(mListSong.get(position).getId() + "");
                holder.mnameSong.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
            }
        }

        holder.mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.mMore);
                if (mTypeSong.equals("AllSong")) {
                    popupMenu.inflate(R.menu.add_song);
                }
                if (mTypeSong.equals("FavoriteSong")) {
                    popupMenu.inflate(R.menu.remove_song);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.addFavorite:
                                ContentValues values = new ContentValues();
                                values.put(FavoriteSongsProvider.FAVORITE, 2);
                                getmContext().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, FavoriteSongsProvider.ID_PROVIDER + "= " + mListSong.get(position).getId(), null);
                                Toast.makeText(mContext, "addFavorite song //" + mListSong.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.removeFavorite:
                                ContentValues values1 = new ContentValues();
                                values1.put(FavoriteSongsProvider.FAVORITE, 1);
                                values1.put(FavoriteSongsProvider.COUNT, 0);
                                mContext.getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values1, FavoriteSongsProvider.ID_PROVIDER + "= " + mListSong.get(position).getId(), null);

                                Toast.makeText(mContext, "removeFavorite song //" + mListSong.get(position).getTitle(), Toast.LENGTH_SHORT).show();// lôi
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();

            }
        });



    }

    public void setiOnClickItemView(OnClickItemView iOnClickItemView) {
        this.iOnClickItemView = iOnClickItemView;
    }

    @Override
    public int getItemCount() {
        return mListSong.size();
    }


    public class WordViewHolder extends RecyclerView.ViewHolder  {
        TextView mstt;
        TextView mnameSong;
        TextView mHours;
        ImageButton mMore;
        ConstraintLayout constraintLayout;

        final SongAdapter mAdapter;

        public WordViewHolder(@NonNull View itemView, SongAdapter adapter){
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

