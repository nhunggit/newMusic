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
    ArrayList<Song> mSong;
    Context mcontext;
    OnClickItemView onClickItemView;
    MediaPlaybackService myService;
    private ArrayList<Song> listSongFavorite=new ArrayList<>();
    private String mTypeSong="";
    int k=0;
    TextView mnameSong;

    public void setListSongFavorite(ArrayList<Song> listSongFavorite) {
        this.listSongFavorite = listSongFavorite;
    }
    public void updateList(ArrayList<Song> songs){
        mSong=songs;
        notifyDataSetChanged();
    }

    public void setmTypeSong(String mTypeSong) {
        this.mTypeSong = mTypeSong;
    }

    public Context getMcontext() {
        return mcontext;
    }

    public void setMyService(MediaPlaybackService myService) {
        this.myService = myService;
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
            //Log.d("adapter", "onBindViewHolder: "+"ok");
            if((myService.getNameSong()).equals(mSong.get(position).getTitle())==true){
                Log.d("compare", "onBindViewHolder: "+myService.getNameSong()+mSong.get(position).getTitle());
                holder.mnameSong.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
                holder.mstt.setText("");
                holder.mstt.setBackgroundResource(R.drawable.ic_equalizer_black_24dp);
            }
            else{
                holder.mstt.setBackgroundResource(R.drawable.ic_equalizer_while_24dp);
                holder.mstt.setText(mSong.get(position).getId() + "");
                holder.mnameSong.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
            }
        }

        holder.mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mcontext, holder.mMore);
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
                                getMcontext().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, FavoriteSongsProvider.ID_PROVIDER + "= " + mSong.get(position).getId(), null);
                                Toast.makeText(mcontext, "addFavorite song //" + mSong.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.removeFavorite:
                                ContentValues values1 = new ContentValues();
                                values1.put(FavoriteSongsProvider.FAVORITE, 1);
                                values1.put(FavoriteSongsProvider.COUNT, 0);
                                mcontext.getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values1, FavoriteSongsProvider.ID_PROVIDER + "= " + mSong.get(position).getId(), null);

                                Toast.makeText(mcontext, "removeFavorite song //" +mSong.get(position).getTitle(), Toast.LENGTH_SHORT).show();// lôi
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();

            }
        });



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

