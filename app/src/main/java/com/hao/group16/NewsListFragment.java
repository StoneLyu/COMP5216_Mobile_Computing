package com.hao.group16;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsListFragment extends Fragment {
    private static final String TAG = NewsListFragment.class.getName();
    //public static final String NEWS_LIST_FRAGMENT = "NewsListFragment";

    private ArrayList<NewsItem> data = new ArrayList<>();
    private NewsItemAdapter newsItemAdapter = null;


    //private ArrayList<CategoryItem> categoryItems = null;
    ListView listView = null;
    private ArrayAdapter<String> arrayAdapter = null;
    private String[] categoryList = null;
    private String[] categoryIdList = null;
    private Spinner categorySpinner = null;
    private Callback<List<NewsItem>> mCategoryListCallback = null;

    public NewsListFragment() {
    }


    private void getList(int index) {
        if (null == MainActivity.mLastLocation) {
            RestHelper.getListByCategory(categoryIdList[index], mCategoryListCallback);
        } else {
            RestHelper.getListByCategoryAndLocation(categoryIdList[index],
                    MainActivity.mLastLocation.getLatitude(),
                    MainActivity.mLastLocation.getLongitude(),
                    mCategoryListCallback);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);


        categorySpinner = view.findViewById(R.id.newsList_spinner);
        categoryList = getResources().getStringArray(R.array.category_list);
        categoryIdList = getResources().getStringArray(R.array.category_id_list);

        arrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, categoryList);
        arrayAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(arrayAdapter);
        categorySpinner.setSelection(0);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                getList(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView = (ListView)view.findViewById(R.id.newsList_listView);
        if (null == listView) {
            return view;
        }
        mCategoryListCallback = new Callback<List<NewsItem>>() {
            @Override
            public void onResponse(Call<List<NewsItem>> call, Response<List<NewsItem>> response) {
                if (response.code() != RestHelper.HTTP_CODE_OK) {
                    Log.e(TAG, response.message());
                    return;
                }
                Log.i(TAG, "news list arrived, size:" + response.body().size());
                data = new ArrayList<>(response.body());
                refreshView();
                //newsItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<NewsItem>> call, Throwable t) {
                Log.i(TAG, "Get category list failed");
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }
        };
        //getList(0);
        newsItemAdapter = new NewsItemAdapter(getActivity(), data);
        listView.setAdapter(newsItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > data.size() - 1) {
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
        Log.w(TAG, "1-NewsList page");
        return view;
    }


    public void refreshView() {
        Log.i(TAG, "news list page refreshed");
        newsItemAdapter = new NewsItemAdapter(getActivity(), data);
        if (null != listView) {
            listView.setAdapter(newsItemAdapter);
        }
        if (null != newsItemAdapter) {
            newsItemAdapter.notifyDataSetChanged();
        }
    }

}
