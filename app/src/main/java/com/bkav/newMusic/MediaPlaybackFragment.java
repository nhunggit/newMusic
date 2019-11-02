package com.bkav.newMusic;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;

public class MediaPlaybackFragment extends Fragment {
    MediaPlaybackService myService;
    TextView nameSong;
    TextView nameArtist;
    ImageView potoMusic;
    ImageView potoMusic2;
    SeekBar seekBar;
    TextView timeCurrent;
    TextView timeFinish;
    ImageView like;
    ImageView diskLike;
    ImageView play;
    ImageView next;
    ImageView previous;
    ImageView repeat;
    ImageView shuffle;
    ImageView listmusic;
    ArrayList<Song> song=new ArrayList<>();
    BaseListSongFrament baseListSongFrament;
    String name="ok";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.baihat, container, false);
        listmusic=(ImageView) view.findViewById(R.id.listMusic);
        seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        potoMusic2 = (ImageView) view.findViewById(R.id.imgBackGround);
        nameSong = (TextView) view.findViewById(R.id.namesong);
        nameArtist = (TextView) view.findViewById(R.id.nameArtist);
        timeCurrent = (TextView) view.findViewById(R.id.starttime);
        timeFinish = (TextView) view.findViewById(R.id.finishTime);
        potoMusic = (ImageView) view.findViewById(R.id.disk);
        like = (ImageView) view.findViewById(R.id.like);
        diskLike = (ImageView) view.findViewById(R.id.dislike);
        play = (ImageView) view.findViewById(R.id.Play);
        next = (ImageView) view.findViewById(R.id.next);
        repeat = (ImageView) view.findViewById(R.id.repeat);
        shuffle = (ImageView) view.findViewById(R.id.shuffle);
        previous = (ImageView) view.findViewById(R.id.previous);

        listmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        if (myService != null) {
            seekBar.setMax(myService.getDurationSong());
                updateUI();
        }
        shuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (myService.isShuffleSong()) {
                    myService.setShuffleSong(false);
                    shuffle.setBackgroundResource(R.drawable.ic_shuffle_black_50dp);
                } else {
                    myService.setShuffleSong(true);
                    shuffle.setBackgroundResource(R.drawable.ic_shuffle_yellow_24dp);
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (myService != null) {
                    try {
                        myService.nextSong();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateUI();
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myService != null) {
                    try {
                        myService.previousSong();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateUI();
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myService.getMediaPlayer().seekTo(seekBar.getProgress());
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ok", "onClick: "+myService.getLoopSong());
                if (myService.getLoopSong() == 0) {
                    myService.setLoopSong(-1);
                    repeat.setBackgroundResource(R.drawable.ic_repeat_yellow_24dp);
                } else {
                    if (myService.getLoopSong() == 1) {
                        myService.setLoopSong(0);
                        repeat.setBackgroundResource(R.drawable.ic_repeat_white_24dp);
                    } else {
                        myService.setLoopSong(1);
                        repeat.setBackgroundResource(R.drawable.ic_repeat_one_yellow_24dp);
                    }
                }
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myService.isPlaying()) {
                    myService.pauseSong();
                }
                    else {
                        try {
                            myService.playSong(myService.getListsong().get(myService.getMinIndex()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                updateUI();
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(FavoriteSongsProvider.FAVORITE,2);
                getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI,values,FavoriteSongsProvider.ID_PROVIDER +"= "+myService.getMinIndex(),null);
                Toast.makeText(getContext(),  "like song //"+myService.getNameSong(), Toast.LENGTH_SHORT).show();
            }
        });
        diskLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(FavoriteSongsProvider.FAVORITE,1);
                getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI,values,FavoriteSongsProvider.ID_PROVIDER +"= "+myService.getMinIndex(),null);
                Toast.makeText(getContext(),  "dislike song //"+myService.getNameSong(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private Bitmap getAlbumn(String path) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(path);
        byte[] data = metadataRetriever.getEmbeddedPicture();
        return data == null ? null : BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public void updateUI() {
        if (myService != null && seekBar != null) {
            if (myService.isMusicPlay()) {
                updateTime();
                seekBar.setMax(myService.getDurationSong());
                nameSong.setText(myService.getNameSong());
                nameArtist.setText(myService.getNameArtist());
                Bitmap bitmap = getAlbumn(myService.getPotoMusic());
                potoMusic.setImageBitmap(bitmap);
                potoMusic2.setImageBitmap(bitmap);
                timeFinish.setText(myService.getDuration());
                if (myService.isPlaying()) {
                    play.setImageResource(R.drawable.ic_pause_circle_filled_black_50dp);

                } else {
                    play.setImageResource(R.drawable.ic_play_circle_filled_black_50dp);
                }
                if (myService.isShuffleSong()) {
                    shuffle.setBackgroundResource(R.drawable.ic_shuffle_yellow_24dp);
                } else
                   shuffle.setBackgroundResource(R.drawable.ic_shuffle_black_50dp);

                if (myService.getLoopSong() == 0) {
                    repeat.setBackgroundResource(R.drawable.ic_repeat_white_24dp);
                } else {
                    if (myService.getLoopSong() == -1) {
                        repeat.setBackgroundResource(R.drawable.ic_repeat_yellow_24dp);
                    } else
                        repeat.setBackgroundResource(R.drawable.ic_repeat_one_yellow_24dp);
                }
            }
        }
    }


    public void updateTime() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
                timeCurrent.setText(formatTime.format(myService.getCurrentTime()));
                seekBar.setProgress(myService.getCurrentTime());
                myService.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        try {
                            myService.onCompletionSong();
                            updateUI();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    public void setMyService(MediaPlaybackService service) {
        this.myService = service;
        updateUI();
    }

}

