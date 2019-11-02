package com.bkav.newMusic;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class BaseListSongFrament extends Fragment implements SongAdapter.OnClickItemView  {
    MediaPlaybackService myService;
    ConstraintLayout constraintLayout;
    ConstraintLayout mConstraitLayout;
    TextView NameSongPlaying;
    TextView nameSong;
    TextView artist;
    ImageView buttonPlay;
    ImageView disk;
    SongAdapter songAdapter;
    RecyclerView recycleview;
    private String SHARED_PREFERENCES_NAME = "com.bkav.mymusic";
    private SharedPreferences mSharePreferences;
    private int position=0;
    private String mURL = "content://com.bkav.provider";
    private Uri mURISong = Uri.parse(mURL);
    private MediaPlaybackFragment songFragment=new MediaPlaybackFragment();
    ArrayList<Song> songs = new ArrayList<>();
    boolean ispotraist;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        songAdapter = new SongAdapter(songs, getContext());
        View view = inflater.inflate(R.layout.list_baihat, container, false);
        View View=inflater.inflate(R.layout.item_baihat,container,false);
        recycleview = view.findViewById(R.id.recyclerview);
        constraintLayout=view.findViewById(R.id.constraintLayoutItem);
        NameSongPlaying=view.findViewById(R.id.namePlaySong);
        buttonPlay=view.findViewById(R.id.play);
        artist=view.findViewById(R.id.Artist);
        mConstraitLayout=view.findViewById(R.id.constraintLayout);
        disk=view.findViewById(R.id.disk);
        nameSong=View.findViewById(R.id.namesong);
        recycleview.setHasFixedSize(true);
        ispotraist=getResources().getBoolean(R.bool.ispotraist);
        mSharePreferences = this.getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        position=mSharePreferences.getInt("position",0);
        NameSongPlaying.setText(mSharePreferences.getString("namesong","NameSong"));
        artist.setText(mSharePreferences.getString("artist","NameArtist"));
        final String file=mSharePreferences.getString("file","");
        recycleview.setAdapter(songAdapter);

        // Log.d("nameSong", "onCreateView: "+nameSong.getText());
        @SuppressLint("WrongConstant") LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycleview.setLayoutManager(linearLayoutManager);

        if(ispotraist==true){
            mConstraitLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    songFragment.setMyService(myService);
                    Log.d("srv", "onClick: "+myService);
                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment1,songFragment).commit();
                }
            });}else{
            mConstraitLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   songFragment.setMyService(myService);
                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment2,songFragment).commit();
                }
            });
        }
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myService.isMusicPlay()) {
                    if (myService.isPlaying()) {
                        myService.pauseSong();
                    } else {
                        try {
                            myService.playSong(songs.get(position));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                updateUI();
                myService.setmMinIndex(mSharePreferences.getInt("position",0));

            }
        });
        if(ispotraist==false){
            mConstraitLayout.setVisibility(android.view.View.GONE);
        }else if(ispotraist){
            mConstraitLayout.setVisibility(android.view.View.VISIBLE);
        }
        if(myService!=null){
            updateUI();
            mConstraitLayout.setVisibility(View.VISIBLE);
        }
        ((MainActivity)getActivity()).setiConnectActivityAndBaseSong(new MainActivity.IConnectActivityAndBaseSong() {
            @Override
            public void connectActivityAndBaseSong() {
                myService=((MainActivity)getActivity()).myService;
                Log.d("service", "connectActivityAndBaseSong: "+myService);
                songAdapter.setMyService(myService);
                songFragment.setMyService(myService);
            }
        });
        if(myService!=null){
            updateUI();
        }
        return view;
    }
    public void setSong(ArrayList<Song> songs){
        this.songs=songs;
        songAdapter.setmSong(songs);
    }


    public void updateUI(){

        if(myService.isMusicPlay()){
            Log.d("abc1", "ClickItem: "+myService.getNameSong());
            disk.setImageBitmap(myService.getAlbumn(myService.getFile()));
            NameSongPlaying.setText(myService.getNameSong());
            artist.setText(myService.getNameArtist());
            if(myService.isPlaying()){
                buttonPlay.setImageResource(R.drawable.ic_pause);
            }else
                buttonPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            songAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void ClickItem(int position) {
        myService.setmStateMedia(0);
        myService.setListSong(songs);
        myService.setmMinIndex(position);
        if(ispotraist==true) {
            mConstraitLayout.setVisibility(View.VISIBLE);
        }else{
            songFragment.setMyService(myService);
            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment2,songFragment).commit();
        }

        try {
                if (myService.isPlaying()) {
                   myService.getMediaPlayer().pause();
                    myService.playSong(songs.get(position));
                }else
                    myService.playSong(songs.get(position));
            String selection = " id_provider =" + songs.get(position).getId();
            Cursor c = getActivity().managedQuery(mURISong, null, selection, null, null);
            if (c.moveToFirst()) {
                do {
                    //Log.d("ID",c.getString(c.getColumnIndex("id_provider")));
                    if (c.getInt(c.getColumnIndex(FavoriteSongsProvider.FAVORITE)) != 1)
                        if (c.getInt(c.getColumnIndex(FavoriteSongsProvider.COUNT)) < 2) {
                            ContentValues values = new ContentValues();
                            values.put(FavoriteSongsProvider.COUNT, c.getInt(c.getColumnIndex(FavoriteSongsProvider.COUNT)) + 1);
                            getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, FavoriteSongsProvider.ID_PROVIDER + "= " + songs.get(position).getId(), null);
                            //   Log.d("ID",c.getString(c.getColumnIndex(FavoriteSongsProvider.COUNT))+"//"+c.getString(c.getColumnIndex(FavoriteSongsProvider.FAVORITE)));
                        } else {
                            if (c.getInt(c.getColumnIndex(FavoriteSongsProvider.COUNT)) == 2) {
                                ContentValues values = new ContentValues();
                                values.put(FavoriteSongsProvider.COUNT, 0);
                                values.put(FavoriteSongsProvider.FAVORITE, 2);
                                getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, FavoriteSongsProvider.ID_PROVIDER + "= " +songs.get(position).getId(), null);
                                //   Log.d("ID1", c.getString(c.getColumnIndex(FavoriteSongsProvider.COUNT)) + "//" + c.getString(c.getColumnIndex(FavoriteSongsProvider.FAVORITE)));
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
        if(myService!=null) {
            updateUI();
        }
        songAdapter.setMyService(myService);
    }
}

