package com.bkav.newMusic;

import android.content.ContentValues;
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
    private MediaPlaybackService mMediaPlaybackService;
    private TextView mNameSong;
    private TextView mNameArtist;
    private ImageView mPotoMusic;
    private ImageView potoMusic2;
    private SeekBar mSeekbar;
    private TextView mTimeCurrent;
    private TextView mTimeFinish;
    private ImageView mLike;
    private ImageView mDiskLike;
    private ImageView mPlay;
    private ImageView mNext;
    private ImageView mPrevious;
    private ImageView mRepeat;
    private ImageView mShuffle;
    private ImageView mListMusic;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.baihat, container, false);
        mListMusic =view.findViewById(R.id.listMusic);
        mSeekbar = view.findViewById(R.id.seekbar);
        potoMusic2 = view.findViewById(R.id.imgBackGround);
        mNameSong =  view.findViewById(R.id.namesong);
        mNameArtist =view.findViewById(R.id.nameArtist);
        mTimeCurrent =  view.findViewById(R.id.starttime);
        mTimeFinish =  view.findViewById(R.id.finishTime);
        mPotoMusic = view.findViewById(R.id.disk);
        mLike =  view.findViewById(R.id.like);
        mDiskLike =view.findViewById(R.id.dislike);
        mPlay =  view.findViewById(R.id.Play);
        mNext =  view.findViewById(R.id.next);
        mRepeat = view.findViewById(R.id.repeat);
        mShuffle =  view.findViewById(R.id.shuffle);
        mPrevious = view.findViewById(R.id.previous);

        mListMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        if (mMediaPlaybackService != null) {
            mSeekbar.setMax(mMediaPlaybackService.getDurationSong());
             updateUI();
        }
        mShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mMediaPlaybackService.isShuffleSong()) {
                    mMediaPlaybackService.setShuffleSong(false);
                    mShuffle.setBackgroundResource(R.drawable.ic_shuffle_black_50dp);
                } else {
                    mMediaPlaybackService.setShuffleSong(true);
                    mShuffle.setBackgroundResource(R.drawable.ic_shuffle_yellow_24dp);
                }
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mMediaPlaybackService != null) {
                    try {
                        mMediaPlaybackService.nextSong();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateUI();
                }
            }
        });
        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlaybackService != null) {
                    try {
                        mMediaPlaybackService.previousSong();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateUI();
                }
            }
        });
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlaybackService.getmMediaPlayer().seekTo(seekBar.getProgress());
            }
        });
        mRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ok", "onClick: "+ mMediaPlaybackService.getLoopSong());
                if (mMediaPlaybackService.getLoopSong() == 0) {
                    mMediaPlaybackService.setLoopSong(-1);
                    mRepeat.setBackgroundResource(R.drawable.ic_repeat_yellow_24dp);
                } else {
                    if (mMediaPlaybackService.getLoopSong() == 1) {
                        mMediaPlaybackService.setLoopSong(0);
                        mRepeat.setBackgroundResource(R.drawable.ic_repeat_white_24dp);
                    } else {
                        mMediaPlaybackService.setLoopSong(1);
                        mRepeat.setBackgroundResource(R.drawable.ic_repeat_one_yellow_24dp);
                    }
                }
            }
        });
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlaybackService.isPlaying()) {
                    mMediaPlaybackService.pauseSong();
                }
                    else {
                        try {
                            mMediaPlaybackService.playSong(mMediaPlaybackService.getListsong().get(mMediaPlaybackService.getMinIndex()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                updateUI();
            }
        });
        mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(FavoriteSongsProvider.FAVORITE,2);
                getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI,values,FavoriteSongsProvider.ID_PROVIDER +"= "+ mMediaPlaybackService.getMinIndex(),null);
                Toast.makeText(getContext(),  "like song //"+ mMediaPlaybackService.getNameSong(), Toast.LENGTH_SHORT).show();
            }
        });
        mDiskLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(FavoriteSongsProvider.FAVORITE,1);
                getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI,values,FavoriteSongsProvider.ID_PROVIDER +"= "+ mMediaPlaybackService.getMinIndex(),null);
                Toast.makeText(getContext(),  "dislike song //"+ mMediaPlaybackService.getNameSong(), Toast.LENGTH_SHORT).show();
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
        if (mMediaPlaybackService != null && mSeekbar != null) {
            if (mMediaPlaybackService.isMusicPlay()) {
                updateTime();
                mSeekbar.setMax(mMediaPlaybackService.getDurationSong());
                mNameSong.setText(mMediaPlaybackService.getNameSong());
                mNameArtist.setText(mMediaPlaybackService.getNameArtist());
                Bitmap bitmap = getAlbumn(mMediaPlaybackService.getPotoMusic());
                mPotoMusic.setImageBitmap(bitmap);
                potoMusic2.setImageBitmap(bitmap);
                mTimeFinish.setText(mMediaPlaybackService.getDuration());
                if (mMediaPlaybackService.isPlaying()) {
                    mPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_50dp);

                } else {
                    mPlay.setImageResource(R.drawable.ic_play_circle_filled_black_50dp);
                }
                if (mMediaPlaybackService.isShuffleSong()) {
                    mShuffle.setBackgroundResource(R.drawable.ic_shuffle_yellow_24dp);
                } else
                   mShuffle.setBackgroundResource(R.drawable.ic_shuffle_black_50dp);

                if (mMediaPlaybackService.getLoopSong() == 0) {
                    mRepeat.setBackgroundResource(R.drawable.ic_repeat_white_24dp);
                } else {
                    if (mMediaPlaybackService.getLoopSong() == -1) {
                        mRepeat.setBackgroundResource(R.drawable.ic_repeat_yellow_24dp);
                    } else
                        mRepeat.setBackgroundResource(R.drawable.ic_repeat_one_yellow_24dp);
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
                mTimeCurrent.setText(formatTime.format(mMediaPlaybackService.getCurrentTime()));
                mSeekbar.setProgress(mMediaPlaybackService.getCurrentTime());
                mMediaPlaybackService.getmMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        try {
                            mMediaPlaybackService.onCompletionSong();
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

    public void setmMediaPlaybackService(MediaPlaybackService service) {
        this.mMediaPlaybackService = service;
        updateUI();
    }

}

