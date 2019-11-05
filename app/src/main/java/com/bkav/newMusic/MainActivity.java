package com.bkav.newMusic;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener, ICallbackFromService{

    protected MediaPlaybackService mMediaPlaybackService;
    private boolean mBound=false;
    private AllSongsFragment mAllSongFragment;
    private MediaPlaybackFragment mMediaPlayBackFragment;
    private  boolean mStatus=false;
    private Fragment mFavoriteSongsFragment;
    private DrawerLayout mDrawerLayout;
    private IConnectActivityAndBaseSong iConnectActivityAndBaseSong;
    boolean ispotraist = true;
    MediaPlaybackService mediaPlaybackService;

    public void setiConnectActivityAndBaseSong(IConnectActivityAndBaseSong iConnectActivityAndBaseSong) {
        this.iConnectActivityAndBaseSong = iConnectActivityAndBaseSong;
    }

    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlaybackService.LocalBinder binder=(MediaPlaybackService.LocalBinder) service;
           mMediaPlaybackService =binder.getService();
            Log.d("BKAV DucLQ", " Bkav DucLQ bind service myService "+ mMediaPlaybackService);
            iConnectActivityAndBaseSong.connectActivityAndBaseSong();
             mMediaPlaybackService.setICallbackFromService(getICallback());
            (mMediaPlayBackFragment).setmMediaPlaybackService(mMediaPlaybackService);
            mBound=true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound=false;
        }

    };

    public ICallbackFromService getICallback(){
        return this;
    }




@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == 1) {
        if (grantResults.length == 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Permision Write File is Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Permision Write File is Denied", Toast.LENGTH_SHORT).show();
        }
    } else {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(MainActivity.this, "Permission isn't granted ", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        ispotraist=getResources().getBoolean(R.bool.ispotraist);
        mAllSongFragment = new AllSongsFragment();
        mMediaPlayBackFragment = new MediaPlaybackFragment();
        mFavoriteSongsFragment = new FavoriteSongFament();
        Intent intent=new Intent(this, MediaPlaybackService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d("okko", "onCreate: ogd"+ispotraist);
        if(!ispotraist) {
          //  if(mMediaPlayBackFragment!=null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment1, mAllSongFragment).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment2, mMediaPlayBackFragment).commit();
//            }else
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment1, mAllSongFragment).commit();
          //  }
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment1, mAllSongFragment).commit();
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(mediaPlaybackService!=null) {
//            mediaPlaybackService.setICallbackFromService(getICallback());
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mediaPlaybackService=mMediaPlaybackService;
//    }


    @Override
    protected void onStart() {
        super.onStart();
       // mMediaPlayBackFragment.updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        MenuItem search=menu.findItem(R.id.app_bar_search);
      //  SearchView searchView=(SearchView)search.getActionView();
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_favorite) {
            Toast.makeText(this, "favorite", Toast.LENGTH_SHORT).show();
            mStatus=true;
             mFavoriteSongsFragment = new FavoriteSongFament( mMediaPlaybackService.getListsong(), mMediaPlaybackService);
             getSupportFragmentManager().beginTransaction().replace(R.id.fragment1, mFavoriteSongsFragment).commit();
            mDrawerLayout= findViewById(R.id.drawer_layout);
            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_playlist) {
            mStatus=false;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment1, mAllSongFragment).commit();
            mDrawerLayout= findViewById(R.id.drawer_layout);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void updateUI() {
       Log.d("", "Bkav DucLQ update UI");
       if(mAllSongFragment != null){
           mAllSongFragment.updateUI();
       }
       mMediaPlayBackFragment.updateUI();
    }

    interface IConnectActivityAndBaseSong {
        void connectActivityAndBaseSong();
    }


}
