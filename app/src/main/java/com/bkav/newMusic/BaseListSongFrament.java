package com.bkav.newMusic;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
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
    ImageButton buttonPlay;
    ImageView disk;
    SongAdapter songAdapter;
    RecyclerView recycleview;
    private String SHARED_PREFERENCES_NAME = "com.bkav.mymusic";
    private SharedPreferences mSharePreferences;
    private int position=0;
    private MediaPlaybackFragment songFragment=new MediaPlaybackFragment();
    ArrayList<Song> songs = new ArrayList<>();
    public ArrayList<Song> getListsong() {
        return songs;
    }
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

        // Log.d("nameSong", "onCreateView: "+nameSong.getText());
        @SuppressLint("WrongConstant") LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycleview.setLayoutManager(linearLayoutManager);

        if(ispotraist==true){
            mConstraitLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    songFragment.setMyService(myService);
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
//        if (mSharePreferences.getString("nameSong", "").equals(""))
//            constraintLayout.setVisibility(View.GONE);
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

            }
        });
        if(ispotraist==false){
            mConstraitLayout.setVisibility(android.view.View.GONE);
        }
        if(myService!=null){
            Log.d("isplay", "onCreateView: "+"ok");
            updateUI();
        }
        ((MainActivity)getActivity()).setiConnectActivityAndBaseSong(new MainActivity.IConnectActivityAndBaseSong() {
            @Override
            public void connectActivityAndBaseSong() {
                myService=((MainActivity)getActivity()).myService;
                Log.d("service", "connectActivityAndBaseSong: "+myService);
                songAdapter.setMyService(myService);
                updateUI();

            }
        });

        return view;
    }
    public void setSong(ArrayList<Song> songs){
        this.songs=songs;
        songAdapter.setmSong(songs);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        baihatAdapter.setMyService(myService);
//    }
    //    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
//
//    }

    public void updateUI(){

        if(myService.isMusicPlay()){
            Log.d("abc1", "ClickItem: "+myService.getNameSong());
            //constraintLayout.setVisibility(View.VISIBLE);
            myService.updateTime();
            disk.setImageBitmap(myService.getAlbumn(myService.getFile()));
            NameSongPlaying.setText(myService.getNameSong());
            artist.setText(myService.getNameArtist());
            if(myService.isPlaying()){
                buttonPlay.setImageResource(R.drawable.ic_pause);
            }else
                buttonPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);

            // if((myService.getNameSong()).equals(songs))

        }
    }

    @Override
    public void ClickItem(int position) {
        Log.d("ntkc", "ClickItem: "+myService);
        if(ispotraist==true) {
            mConstraitLayout.setVisibility(View.VISIBLE);
        }else{
            songFragment.setMyService(myService);
            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment2,songFragment).commit();
        }

        try {
            if (myService.isMusicPlay()) {
                if (!myService.isPlaying()) {
                    myService.playSong(songs.get(position));
                } else {
                    myService.pauseSong();
                    myService.playSong(songs.get(position));
                }
            }
            else {
                myService.playSong(songs.get(position));
            }
            updateUI();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
