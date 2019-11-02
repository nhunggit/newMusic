package com.bkav.newMusic;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.util.ArrayList;

public class FavoriteSongFament extends BaseListSongFrament implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID=1;
    private ArrayList<Song> mListAllSong;

    public FavoriteSongFament(ArrayList<Song> mListAllSong, MediaPlaybackService service){
        this.mListAllSong=new ArrayList<>();
        this.mListAllSong=mListAllSong;
        this.mMediaPlaybackService=service;
    }

    public FavoriteSongFament() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID,null,this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String URL = "content://com.bkav.provider";
        Uri uriSongs = Uri.parse(URL);
        return new CursorLoader(getContext(),uriSongs, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        ArrayList<Song> mListFavoriteSongs = new ArrayList<>();
        Song song =null;
        int dem=0;
        if (data.moveToFirst()) {
            do {

                for(int i=0;i<mListAllSong.size();i++){
                    //   Log.d("SONG size ","//"+mListAllSong.size());
                    if(mListAllSong.get(i).getId()== data.getInt(data.getColumnIndex(FavoriteSongsProvider.ID_PROVIDER))){
                        Log.d("song F", data.getInt(data.getColumnIndex(FavoriteSongsProvider.ID_PROVIDER))+"//"+mListAllSong.get(i).getId());
                        if( data.getInt(data.getColumnIndex(FavoriteSongsProvider.FAVORITE)) == 2){

                            // Log.d("song F1", "//"+cursor.getInt(cursor.getColumnIndex(FavoriteSongsProvider.ID_PROVIDER))+"//");
                            song = new Song( dem,
                                    mListAllSong.get(i).getTitle(),
                                    mListAllSong.get(i).getFile(),
                                    mListAllSong.get(i).getArtist(),
                                    mListAllSong.get(i).getDuration());
                            dem++;
                            mListFavoriteSongs.add(song);
                        }
                        mSongAdapter.setiOnClickItemView( this);
                        mSongAdapter.updateList(mListFavoriteSongs);
                        Log.d("favorite", "onLoadFinished: "+mListFavoriteSongs.size());
                        setSong(mListFavoriteSongs);
                        mSongAdapter.setmTypeSong("FavoriteSong");
                    }
                }
            } while (data.moveToNext());

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

}
