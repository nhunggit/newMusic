package com.bkav.newMusic;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class BaseListSongFrament extends Fragment implements SongAdapter.OnClickItemView  {
       private ConstraintLayout mConstraitLayout;
       private TextView mNameSong;
       private TextView mArtist;
       private ImageView mbtPlay;
       private ImageView mDisk;
       protected SongAdapter mSongAdapter;
       private RecyclerView mRecycleView;
       private final String SHARED_PREFERENCES_NAME = "com.bkav.mymusic";
       private SharedPreferences mSharePreferences;
       private int mPosition =0;
       private String mURL = "content://com.bkav.provider";
       private Uri mURISong = Uri.parse(mURL);
       private MediaPlaybackFragment mMediaPlaybackFragment =new MediaPlaybackFragment();
       private ArrayList<Song> mListSong = new ArrayList<>();
       private boolean mIspotraist;
       protected MediaPlaybackService mMediaPlaybackService;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mSongAdapter = new SongAdapter(mListSong, getContext());
        View view = inflater.inflate(R.layout.list_baihat, container, false);
        View View=inflater.inflate(R.layout.item_baihat,container,false);
        mRecycleView = view.findViewById(R.id.recyclerview);
        mNameSong =view.findViewById(R.id.namePlaySong);
        mbtPlay =view.findViewById(R.id.play);
        mArtist =view.findViewById(R.id.Artist);
        mConstraitLayout=view.findViewById(R.id.constraintLayout);
        mDisk =view.findViewById(R.id.disk);
        mRecycleView.setHasFixedSize(true);
        mIspotraist =getResources().getBoolean(R.bool.ispotraist);
        mSharePreferences = this.getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        mPosition =mSharePreferences.getInt("position",0);
        mNameSong.setText(mSharePreferences.getString("namesong","NameSong"));
        mArtist.setText(mSharePreferences.getString("artist","NameArtist"));
        final String file=mSharePreferences.getString("file","");
        mRecycleView.setAdapter(mSongAdapter);

        @SuppressLint("WrongConstant") LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecycleView.setLayoutManager(linearLayoutManager);

        if(mIspotraist ==true){
            mConstraitLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMediaPlaybackFragment.setmMediaPlaybackService(mMediaPlaybackService);
                    Log.d("srv", "onClick: "+ mMediaPlaybackService);
                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment1, mMediaPlaybackFragment).commit();
                }
            });}else{
            mConstraitLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   mMediaPlaybackFragment.setmMediaPlaybackService(mMediaPlaybackService);
                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment2, mMediaPlaybackFragment).commit();
                }
            });
        }

        mbtPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaPlaybackService.isMusicPlay()) {
                    if (mMediaPlaybackService.isPlaying()) {
                        mMediaPlaybackService.pauseSong();
                    } else {
                        try {
                            mMediaPlaybackService.playSong(mListSong.get(mPosition));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                updateUI();
                mMediaPlaybackService.setmMinIndex(mSharePreferences.getInt("position",0));

            }
        });

        if(mIspotraist ==false|| mMediaPlaybackService ==null){
            mConstraitLayout.setVisibility(android.view.View.GONE);
        }
        if(mMediaPlaybackService !=null){
            updateUI();
            mConstraitLayout.setVisibility(View.VISIBLE);
        }
        ((MainActivity)getActivity()).setiConnectActivityAndBaseSong(new MainActivity.IConnectActivityAndBaseSong() {
            @Override
            public void connectActivityAndBaseSong() {
                mMediaPlaybackService =((MainActivity)getActivity()).mMediaPlaybackService;
                Log.d("service", "connectActivityAndBaseSong: "+ mMediaPlaybackService);
                mSongAdapter.setmMediaPlaybackService(mMediaPlaybackService);
                mMediaPlaybackFragment.setmMediaPlaybackService(mMediaPlaybackService);
            }
        });
        if(mMediaPlaybackService !=null){
            updateUI();
        }
        return view;
    }

    public void setSong(ArrayList<Song> songs){
        this.mListSong =songs;
        mSongAdapter.setmListSong(songs);
    }


    public void updateUI(){

        if(mMediaPlaybackService.isMusicPlay()){
            Log.d("abc1", "ClickItem: "+ mMediaPlaybackService.getNameSong());
            mDisk.setImageBitmap(mMediaPlaybackService.getAlbumn(mMediaPlaybackService.getFile()));
            mNameSong.setText(mMediaPlaybackService.getNameSong());
            mArtist.setText(mMediaPlaybackService.getNameArtist());
            if(mMediaPlaybackService.isPlaying()){
                mbtPlay.setImageResource(R.drawable.ic_pause);
            }else
                mbtPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            mSongAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void ClickItem(int position) {
        mMediaPlaybackService.setmStateMedia(0);
        mMediaPlaybackService.setListSong(mListSong);
        mMediaPlaybackService.setmMinIndex(position);
        if(mIspotraist ==true) {
            mConstraitLayout.setVisibility(View.VISIBLE);
        }else{
            mMediaPlaybackFragment.setmMediaPlaybackService(mMediaPlaybackService);
            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment2, mMediaPlaybackFragment).commit();
        }

        try {
                if (mMediaPlaybackService.isPlaying()) {
                   mMediaPlaybackService.getmMediaPlayer().pause();
                    mMediaPlaybackService.playSong(mListSong.get(position));
                }else
                    mMediaPlaybackService.playSong(mListSong.get(position));
            String selection = " id_provider =" + mListSong.get(position).getId();
            Cursor c = getActivity().managedQuery(mURISong, null, selection, null, null);
            if (c.moveToFirst()) {
                do {
                    if (c.getInt(c.getColumnIndex(FavoriteSongsProvider.FAVORITE)) != 1)
                        if (c.getInt(c.getColumnIndex(FavoriteSongsProvider.COUNT)) < 2) {
                            ContentValues values = new ContentValues();
                            values.put(FavoriteSongsProvider.COUNT, c.getInt(c.getColumnIndex(FavoriteSongsProvider.COUNT)) + 1);
                            getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, FavoriteSongsProvider.ID_PROVIDER + "= " + mListSong.get(position).getId(), null);
                        } else {
                            if (c.getInt(c.getColumnIndex(FavoriteSongsProvider.COUNT)) == 2) {
                                ContentValues values = new ContentValues();
                                values.put(FavoriteSongsProvider.COUNT, 0);
                                values.put(FavoriteSongsProvider.FAVORITE, 2);
                                getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, FavoriteSongsProvider.ID_PROVIDER + "= " + mListSong.get(position).getId(), null);
                            }
                        }

                } while (c.moveToNext());

            }


            updateUI();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        if(mMediaPlaybackService !=null) {
            updateUI();
        }
        mSongAdapter.setmMediaPlaybackService(mMediaPlaybackService);
    }
}

