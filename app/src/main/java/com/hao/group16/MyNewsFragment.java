package com.hao.group16;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyNewsFragment extends Fragment {
    public static final String MYNEWS_FRAGMENT_DATA = "MyNewsFragmentData";
    private static final String TAG = MyNewsFragment.class.getName();
    private ArrayList<NewsItem> data = new ArrayList<>();
    private retrofit2.Callback<List<NewsItem>> muserPostListCallback = null;
    private NewsItemAdapter newsItemAdapter = null;
    private ListView listView = null;
    private LinearLayout loggedin = null, notloggedin = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_news, container, false);
        listView = (ListView)view.findViewById(R.id.my_post_list_listView);
        loggedin = view.findViewById(R.id.my_post_logged_in);
        notloggedin = view.findViewById(R.id.my_post_not_logged_in);

        muserPostListCallback = new Callback<List<NewsItem>>() {
            @Override
            public void onResponse(Call<List<NewsItem>> call, Response<List<NewsItem>> response) {
                if (response.code() != RestHelper.HTTP_CODE_OK) {
                    Log.e(TAG, response.message());
                    return;
                }
                data = new ArrayList<>(response.body());
                refreshView();
            }

            @Override
            public void onFailure(Call<List<NewsItem>> call, Throwable t) {
                Log.i(TAG, "Get my post list failed");
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }
        };
        if (null == listView) {
            return view;
        }
        RestHelper.getUserPosts(MainActivity.mUserDetail.getId(), muserPostListCallback);
        NewsItemAdapter newsItemAdapter = new NewsItemAdapter(getActivity(), data);
        listView.setAdapter(newsItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= data.size()) {
                    return;
                }
                Intent webIntent = new Intent(getActivity(), WebViewActivity.class);
                webIntent.putExtra(WebViewActivity.WEB_VIEW_URI, data.get(i).getId());
                webIntent.putExtra(WebViewActivity.WEB_VIEW_DISTANCE, data.get(i).getDistance());
                startActivity(webIntent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return false;
            }
        });
        Log.e(TAG, "3-Mypost page");
        return view;
    }

    public void updateData(ArrayList<NewsItem> data) {
        this.data = data;
        if (null != newsItemAdapter) {
            newsItemAdapter.notifyDataSetChanged();
        }
    }

    private void refreshView() {
        if (null != listView) {
            NewsItemAdapter newsItemAdapter = new NewsItemAdapter(getActivity(), data);
            listView.setAdapter(newsItemAdapter);
        }
    }

    public void checkLoginStatus() {
        if (loggedin == null || notloggedin == null) {
            return;
        }
        if (MainActivity.mUserDetail.getId().compareTo("-1") == 0) {
            loggedin.setVisibility(View.GONE);
            notloggedin.setVisibility(View.VISIBLE);
        } else {
            loggedin.setVisibility(View.VISIBLE);
            notloggedin.setVisibility(View.GONE);
        }
    }

}
