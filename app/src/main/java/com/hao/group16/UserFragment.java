package com.hao.group16;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
    private static final String TAG = UserFragment.class.getName();

    private LinearLayout layout_loggedin = null;
    private LinearLayout layout_notLogin = null;
    private TextView text_userName = null, text_email = null;
    private Button btnLogout = null;
    private MainActivity mainActivity = null;

    private Callback<UserItem> callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        layout_loggedin = view.findViewById(R.id.loggedIn);
        layout_notLogin = view.findViewById(R.id.notLogin);
        text_email = view.findViewById(R.id.tv_email);
        text_userName = view.findViewById(R.id.tv_username);
        btnLogout = view.findViewById(R.id.user_btn_logout);
        mainActivity = (MainActivity)getActivity();


        //实现页面跳转
        Button button1 = view.findViewById(R.id.btn_login);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), UserLoginActivity.class);
                startActivity(intent);

            }
        });
        Button button2 = view.findViewById(R.id.btn_register);
        button2.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Intent intent = new Intent();
                                           intent.setClass(getActivity(), UserRegisterActivity.class);
                                           startActivity(intent);

                                       }
                                   }
        );
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.sharedPrefsCookiePersistor.clear();
                MainActivity.sharedPrefsCookiePersistor.removeAll(MainActivity.sharedPrefsCookiePersistor.loadAll());
                MainActivity.sharedPrefsCookiePersistor = new SharedPrefsCookiePersistor(mainActivity.getApplicationContext());
                RestHelper.cookieJar = new PersistentCookieJar(new SetCookieCache(), MainActivity.sharedPrefsCookiePersistor);
                MainActivity.mUserDetail = new UserItem();
                Toast.makeText(getContext(), "You have logged out.", Toast.LENGTH_LONG).show();
                checkLoginStatus();
            }
        });
        callback = new retrofit2.Callback<UserItem>() {
            @Override
            public void onResponse(Call<UserItem> call, Response<UserItem> response) {
                if (response.code() != RestHelper.HTTP_CODE_OK) {
                    return;
                }
                MainActivity.mUserDetail = response.body();
                UserItem item = MainActivity.mUserDetail;
                if (item.getId().compareTo("-1") == 0) {
                    return;
                }
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.bookmarkFragment.checkLoginStatus();
                mainActivity.myNewsFragment.checkLoginStatus();
                checkLoginStatus();
            }

            @Override
            public void onFailure(Call<UserItem> call, Throwable t) {
                Log.i(TAG, "Get user detail failed");
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }
        };
        Log.e(TAG, "4-Userfragment");
        checkLoginStatus();
        return view;
    }



    public void checkLoginStatus() {
        if (MainActivity.mUserDetail.getId().compareTo("-1") == 0) {
            if (null != layout_loggedin && null != layout_notLogin) {
                layout_loggedin.setVisibility(View.GONE);
                layout_notLogin.setVisibility(View.VISIBLE);
                text_userName.setText(R.string.info_not_logged_in);
                text_email.setText(R.string.info_login_req);
            }
            if (null != callback) {
                RestHelper.getUserDetail(callback);
            }
        } else {
            if (null != layout_loggedin && null != layout_notLogin) {
                layout_loggedin.setVisibility(View.VISIBLE);
                layout_notLogin.setVisibility(View.GONE);
                text_userName.setText("Welcome " + MainActivity.mUserDetail.getUserName() + "!");
                text_email.setText("Email:" + MainActivity.mUserDetail.getEmail());
            }
        }

    }
}
