package com.bkav.newMusic;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class AllSongsFragment extends Fragment implements SongAdapter.OnClickItemView, LoaderManager.LoaderCallbacks<Cursor> {
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
    private static final int LOADER_ID = 1;
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

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
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
                if(myService.isPlaying()){
                    myService.pauseSong();
                }else{
                    try {
                        myService.playSong(songs.get(position));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                updateUI();
                myService.setmMinIndex(mSharePreferences.getInt("position",0));

            }
        });
        if(myService!=null){
            if(ispotraist==true){
                mConstraitLayout.setVisibility(android.view.View.VISIBLE);
            }
            updateUI();
            songAdapter.setMyService(myService);
        }

        return view;
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
            myService.getMediaPlayer().setOnCompletionListener(myService);
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
        //iListennerSong.dataSong(songs.get(position));
        int k=0;
       if(ispotraist==true) {
            mConstraitLayout.setVisibility(View.VISIBLE);
      }else{
            songFragment.setMyService(myService);
            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment2,songFragment).commit();
        }

        songAdapter.setK(0);
        try {
            myService.setListSong(songs);
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

    public void setMyService(MediaPlaybackService service){
        this.myService = service;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection={MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION};
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        CursorLoader cursorLoader=new CursorLoader(getContext(),MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,selection,null,null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mSharePreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        boolean isCreate = mSharePreferences.getBoolean("create_db", false);
        int id = 0;
        String title = "";
        String file = "";
        String artist = "";
        int duration = 0;
        Song song = new Song();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            while (data.moveToNext()) {
                id++;
                song.setId(id);
                song.setTitle(data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                song.setFile(data.getString(data.getColumnIndex(MediaStore.Audio.Media.DATA)));
                song.setArtist(data.getString(data.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                song.setDuration(data.getInt(data.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                title = song.getTitle();
                file = song.getFile();
                artist = song.getArtist();
                duration = song.getDuration();
                songs.add(new Song(id, title, file, artist, duration));
//                if (isCreate == false) {
//                    ContentValues values = new ContentValues();
//                    values.put(FavoriteSongsProvider.ID_PROVIDER, id);
//                    values.put(FavoriteSongsProvider.FAVORITE, 0);
//                    values.put(FavoriteSongsProvider.COUNT, 0);
//                    Uri uri = getActivity().getContentResolver().insert(FavoriteSongsProvider.CONTENT_URI, values);
//                    mSharePreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
//                    SharedPreferences.Editor editor = mSharePreferences.edit();
//                    editor.putBoolean("create_db", true);
//                    editor.commit();
//                }
                Log.d("size", "ClickItem: " + songs.size());
                recycleview.setAdapter(songAdapter);
                songAdapter.setOnClickItemView(
                        this);

            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}




