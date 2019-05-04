package com.hao.group16;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import okhttp3.Interceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener
{

    private final static String TAG = MainActivity.class.getName();

    private static final String[] mPermissionList = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.INTERNET
    };

    public static final int PERMISSIONS_REQUEST = 100;
    public static final int REQUEST_ADD_NEWS = 101;

    //public static String mUserId = "5b937dfd106afa17dcffd125";
    private ArrayList<CategoryItem> mCategoryItems = null;
    public static UserItem mUserDetail = new UserItem();


    private TextView txt_topbar;
    private RadioGroup rg_tab_bar;
    private RadioButton rb_newsList;
    private RadioButton rb_bookmarkList;
    private RadioButton rb_myNewsPost;
    private RadioButton rb_setting;
    private ViewPager vpager;


    // GPS location data
    private LocationManager mLocationManager;
    private LocationListener mListener;
    public static Location mLastLocation;


    private MainPagerAdapter mAdapter;
    private NewsListFragment newsListFragment;
    public BookmarkFragment bookmarkFragment;
    public MyNewsFragment myNewsFragment;
    public static UserFragment userFragment;

    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;
    public static final int PAGE_FOUR = 3;


    public static SharedPrefsCookiePersistor sharedPrefsCookiePersistor = null;
    public static Drawable defaultNewsPic = null;
    // network callback objects
    private Callback<List<BookmarkItem>> mBookmarkListCallback = null;
    private Callback<UserItem> mUserCallback = null;
    //private Callback<UserItem> mUserDatailCallback = null;
    //private Callback<List<CategoryItem>> mCategoryCallback = null;


    private void getUserPermission() {
        ActivityCompat.requestPermissions(this,
                mPermissionList,
                PERMISSIONS_REQUEST);
    }


    private void initNetworkCallback() {

        mBookmarkListCallback = new Callback<List<BookmarkItem>>() {
            @Override
            public void onResponse(Call<List<BookmarkItem>> call, Response<List<BookmarkItem>> response) {
                if (response.code() != RestHelper.HTTP_CODE_OK) {
                    return;
                }
                Log.i(TAG, "bookmark list arrived, size:" + response.body().size());
                bookmarkFragment.updateData(new ArrayList<BookmarkItem>(response.body()));
                //mAdapter.getItem(PAGE_TWO).setArguments(bundle);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<BookmarkItem>> call, Throwable t) {
                Log.i(TAG, "Get bookmark list failed");
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        };

        mUserCallback = new retrofit2.Callback<UserItem>() {
            @Override
            public void onResponse(Call<UserItem> call, Response<UserItem> response) {
                if (response.code() != RestHelper.HTTP_CODE_OK) {
                    return;
                }
                MainActivity.mUserDetail = response.body();
                UserItem item = MainActivity.mUserDetail;
                if (item.getId().compareTo("-1") != 0) {
                    userFragment.checkLoginStatus();
                }
            }

            @Override
            public void onFailure(Call<UserItem> call, Throwable t) {
                Log.i(TAG, "Get user detail failed");
                Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        };
        /*mCategoryCallback = new Callback<List<CategoryItem>>() {
            @Override
            public void onResponse(Call<List<CategoryItem>> call, Response<List<CategoryItem>> response) {
                mCategoryItems = new ArrayList<>(response.body());
            }

            @Override
            public void onFailure(Call<List<CategoryItem>> call, Throwable t) {

            }
        };*/
    }

    private void initUserInterface() {


        txt_topbar = (TextView) findViewById(R.id.txt_topbar);
        rg_tab_bar = (RadioGroup) findViewById(R.id.rg_tab_bar);
        rb_newsList = (RadioButton) findViewById(R.id.rb_news_list);
        rb_bookmarkList = (RadioButton) findViewById(R.id.rb_bookmark_list);
        rb_myNewsPost = (RadioButton) findViewById(R.id.rb_my_post_list);
        rb_setting = (RadioButton) findViewById(R.id.rb_setting);
        rg_tab_bar.setOnCheckedChangeListener(this);

        defaultNewsPic = getDrawable(R.drawable.news_default);
        vpager = (ViewPager) findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.addOnPageChangeListener(this);
//        vpager.setOffscreenPageLimit(2);
        vpager.setCurrentItem(PAGE_ONE);
        newsListFragment = (NewsListFragment) mAdapter.getItem(0);
        bookmarkFragment = (BookmarkFragment) mAdapter.getItem(1);
        myNewsFragment = (MyNewsFragment) mAdapter.getItem(2);
        userFragment = (UserFragment) mAdapter.getItem(3);
        //userFragment.checkLoginStatus();
        rb_newsList.setChecked(true);

    }

    /*private void requestCategoryList() {
        RestHelper.getAllCategories(mCategoryCallback);
    }
*/
    public void onBtnAdd(View view) {
        Intent intent = new Intent(this, EditNewsActivity.class);
        startActivityForResult(intent, REQUEST_ADD_NEWS);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_ADD_NEWS == requestCode) {
            if (RESULT_OK == resultCode) {

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mAdapter = new MainPagerAdapter(getSupportFragmentManager());

        getUserPermission();
        initNetworkCallback();
        initUserInterface();
        initLocationServices();
        startListeningGps();


        sharedPrefsCookiePersistor = new SharedPrefsCookiePersistor(getApplicationContext());
        RestHelper.cookieJar = new PersistentCookieJar(new SetCookieCache(), sharedPrefsCookiePersistor);
        RestHelper.isUIReady = true;
        RestHelper.getUserDetail(mUserCallback);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_news_list:
                vpager.setCurrentItem(PAGE_ONE);
                //RestHelper.getListByCategory("5b99fac2c5fda55c3852ef22", mCategoryListCallback);
                newsListFragment.refreshView();
                break;
            case R.id.rb_bookmark_list:
                vpager.setCurrentItem(PAGE_TWO);
                //RestHelper.getBookmarkList(mUserId, mBookmarkListCallback);
                bookmarkFragment.refreshView();
                break;
            case R.id.rb_my_post_list:
                vpager.setCurrentItem(PAGE_THREE);
                break;
            case R.id.rb_setting:
                vpager.setCurrentItem(PAGE_FOUR);
                break;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case PAGE_ONE:
                    rb_newsList.setChecked(true);
                    startListeningGps();
                    break;
                case PAGE_TWO:
                    rb_bookmarkList.setChecked(true);
                    stopListeningGps();
                    //checkIsLogin();
                    bookmarkFragment.checkLoginStatus();
                    break;
                case PAGE_THREE:
                    rb_myNewsPost.setChecked(true);
                    stopListeningGps();
                    myNewsFragment.checkLoginStatus();
                    //checkIsLogin();
                    break;
                case PAGE_FOUR:
                    userFragment.checkLoginStatus();
                    stopListeningGps();
                    rb_setting.setChecked(true);
                    break;
            }
        }
    }

    public void removeAllCookies() {
        sharedPrefsCookiePersistor.clear();
    }

    private void checkIsLogin() {
        if (MainActivity.mUserDetail.getId().compareTo("-1") == 0) {
            rb_setting.setChecked(true);
            vpager.setCurrentItem(PAGE_FOUR);
        }
    }

    private void initLocationServices() {
        //getLocationPermission();
        //mTextView=(TextView) findViewById(R.id.tvDescription);
        mLocationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mListener = new LocationListener() {

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        updateCurrentLocation(mLocationManager.getLastKnownLocation(provider));
                    }
                }

                @Override
                public void onProviderDisabled(String provider) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        updateCurrentLocation(mLocationManager.getLastKnownLocation(provider));
                    }
                }

                @Override
                public void onLocationChanged(Location location) {
                    updateCurrentLocation(location);

                    Log.i(TAG, String.format("#######Map:lat:%.7f,long:%.7f,time:%s", location.getLatitude(),
                            location.getLongitude(), location.getTime()/1000));
                }
            };
            //startListeningGps();
        }
    }

    private void startListeningGps() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            checkIfGpsIsOpen();
            if (null == mListener) {
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, mListener);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, mListener);
            Log.i(TAG, "Started listening location signal.");

        }
    }

    private void stopListeningGps() {
        if (mLocationManager != null && mListener != null) {
            mLocationManager.removeUpdates(mListener);
            Log.i(TAG, "Stopped listening location signal.");
        }
    }

    private void checkIfGpsIsOpen() {

        if (mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.gps_prompt))
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                }).create().show();
    }

    private void updateCurrentLocation(Location newLocation){
        if(null != newLocation){
            mLastLocation = newLocation;
        }
    }
}
