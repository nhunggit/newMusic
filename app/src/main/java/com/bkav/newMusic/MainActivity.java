package com.bkav.newMusic;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    MediaPlaybackService myService;
    boolean mBound=false;
    Fragment mAllSongFragment;
    Fragment mMediaPlayBackFragment;
    private DrawerLayout mDrawerLayout;
    private IConnectActivityAndBaseSong iConnectActivityAndBaseSong;

    public void setiConnectActivityAndBaseSong(IConnectActivityAndBaseSong iConnectActivityAndBaseSong) {
        this.iConnectActivityAndBaseSong = iConnectActivityAndBaseSong;
    }

    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlaybackService.LocalBinder binder=(MediaPlaybackService.LocalBinder) service;
            myService=binder.getService();
            Log.d("BKAV DucLQ", " Bkav DucLQ bind service myService "+ myService);
            iConnectActivityAndBaseSong.connectActivityAndBaseSong();
            ((MediaPlaybackFragment)mMediaPlayBackFragment).setMyService(myService);
            mBound=true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound=false;
        }
    };
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (isMyServiceRunning(MediaPlaybackService.class)) {
//            connectService();
//        } else {
//            startService();
//            connectService();
//        }
//    }
//
//    public void connectService() {
//        Intent it = new Intent(MainActivity.this, MediaPlaybackService.class);
//        bindService(it, mConnection, 0);
//    }
//
//    public void startService() {
//        Intent it = new Intent(MainActivity.this, MediaPlaybackService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startService(it);
//        }
//    }
//
//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unbindService(mConnection);
//    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        boolean ispotraist=getResources().getBoolean(R.bool.ispotraist);
       //boolean n=getResources().getBoolean(R.bool.nhung);
        mAllSongFragment = new AllSongsFragment();
        mMediaPlayBackFragment = new MediaPlaybackFragment();
        Intent intent=new Intent(this, MediaPlaybackService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if(ispotraist==false) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment1, mAllSongFragment).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment2, mMediaPlayBackFragment).commit();
        }
            else
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment1, mAllSongFragment).commit();

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
//
//        if (id == R.id.nav_favorite) {
//            Toast.makeText(this, "favorite", Toast.LENGTH_SHORT).show();
//            mFavoriteSongsFragment = new FavoriteSongsFragment((ArrayList<Song>) mMusicService.getmListAllSong());
//            getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mFavoriteSongsFragment).commit();
//
//        } else if (id == R.id.nav_playlist) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.framentContent, mAllSongsFragment).commit();
//        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    interface IConnectActivityAndBaseSong {
        void connectActivityAndBaseSong();
    }


}
