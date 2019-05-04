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

public class BookmarkFragment extends Fragment{
    //public static final String BOOKMARK_FRAGMENT_DATA = "BookmarkFragmentData";
    private static final String TAG = BookmarkFragment.class.getName();

    private ArrayList<BookmarkItem> data = new ArrayList<>();
    private Callback<List<BookmarkItem>> callback = null;
    private BookmarkItemAdapter bookmarkItemAdapter = null;
    private ListView listView = null;
    private LinearLayout loggedin = null, notloggedin = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark_list, container, false);
        loggedin = view.findViewById(R.id.bookmark_logged_in);
        notloggedin = view.findViewById(R.id.bookmark_not_logged_in);

        listView = (ListView)view.findViewById(R.id.bookmarkList_listView);
        if (null == listView) {
            return view;
        }
        bookmarkItemAdapter = new BookmarkItemAdapter(getActivity(), data);
        listView.setAdapter(bookmarkItemAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                //RestHelper.deleteBookmark();
                return false;
            }
        });
        callback = new Callback<List<BookmarkItem>>() {
            @Override
            public void onResponse(Call<List<BookmarkItem>> call, Response<List<BookmarkItem>> response) {
                if (response.code() != RestHelper.HTTP_CODE_OK) {
                    return;
                }
                Log.i(TAG, "bookmark list arrived, size:" + response.body().size());
                data = new ArrayList<BookmarkItem>(response.body());
                refreshView();
            }

            @Override
            public void onFailure(Call<List<BookmarkItem>> call, Throwable t) {
                Log.i(TAG, "Get bookmark list failed");
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }
        };
        RestHelper.getBookmarkList(MainActivity.mUserDetail.getId(), callback);
        bookmarkItemAdapter = new BookmarkItemAdapter(getActivity(), data);
        listView.setAdapter(bookmarkItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= data.size()) {
                    return;
                }
                Intent webIntent = new Intent(getActivity(), WebViewActivity.class);
                webIntent.putExtra(WebViewActivity.WEB_VIEW_URI, data.get(i).getNewsId());
                //webIntent.putExtra(WebViewActivity.WEB_VIEW_DISTANCE, "null");
                startActivity(webIntent);
            }
        });
        Log.e(TAG, "2-Bookmark page");
        return view;
    }

    public void updateData(ArrayList<BookmarkItem> data) {
        this.data = data;
        refreshView();
    }

    public void refreshView() {
        Log.i(TAG, "bookmark list refresh");
        if (null != listView) {
            bookmarkItemAdapter = new BookmarkItemAdapter(getActivity(), data);
            listView.setAdapter(bookmarkItemAdapter);
        }
        if (null != bookmarkItemAdapter) {
            bookmarkItemAdapter.notifyDataSetChanged();
        }
    }


    public void checkLoginStatus() {
        if (loggedin == null || notloggedin == null) {
            return;
        }
        if (MainActivity.mUserDetail.getId().compareTo("-1") == 0) {
            loggedin.setVisibility(View.GONE);
            notloggedin.setVisibility(View.VISIBLE);
            RestHelper.getBookmarkList(MainActivity.mUserDetail.getId(), callback);
        } else {
            loggedin.setVisibility(View.VISIBLE);
            notloggedin.setVisibility(View.GONE);
        }
    }
}
