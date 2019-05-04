package com.hao.group16;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BookmarkItemAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<BookmarkItem> data;
    private static LruCache<String, BitmapDrawable> mImageCache;
    private ListView listView;
    public BookmarkItemAdapter(Context context, ArrayList<BookmarkItem> data) {
        this.context = context;
        this.data = data;
        int maxCache = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxCache / 8;
        if (null == mImageCache) {
            mImageCache = new LruCache<String, BitmapDrawable>(cacheSize) {
                @Override
                protected int sizeOf(String key, BitmapDrawable value) {
                    try {
                        return value.getBitmap().getByteCount();
                    } catch (Exception e) {
                        return 0;
                    }

                }
            };
        }

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder;
        listView = (ListView)parent;
        if (null == convertView) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.news_item_layout, null);
            myHolder = new MyHolder();
            convertView.setTag(myHolder);
        } else {
            myHolder = (MyHolder) convertView.getTag();
        }
        myHolder.t_date = convertView.findViewById(R.id.newsItem_date);
        myHolder.t_description = convertView.findViewById(R.id.newsItem_desc);
        myHolder.t_distance = convertView.findViewById(R.id.newsItem_location);
        myHolder.t_title = convertView.findViewById(R.id.newsItem_title);
        myHolder.t_pageView = convertView.findViewById(R.id.newsItem_view);
        myHolder.t_vote = convertView.findViewById(R.id.newsItem_vote);
        myHolder.t_image = convertView.findViewById(R.id.newsItem_photo);
        myHolder.t_img_location = convertView.findViewById(R.id.news_item_img_location);
        myHolder.t_distance = convertView.findViewById(R.id.newsItem_location);


        myHolder.t_img_location.setVisibility(View.GONE);
        myHolder.t_distance.setVisibility(View.GONE);


        BookmarkItem bookmarkItem = data.get(position);
        Locale locale = Locale.getDefault();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", locale);
        myHolder.t_date.setText(bookmarkItem.getDate().split("T")[0]);
        myHolder.t_title.setText(bookmarkItem.getTitle());
        myHolder.t_description.setText(bookmarkItem.getDescription());
        myHolder.t_pageView.setText(String.format(locale, "%d", bookmarkItem.getPageView()));
        myHolder.t_vote.setText(String.format(locale, "%d", bookmarkItem.getVote()));
        //myHolder.t_image.
        String uri;
        if (bookmarkItem.getPictures().size() > 0) {
            uri = String.format("%simg/%s", RestAPI.baseURI, bookmarkItem.getPictures().get(0));
            myHolder.t_image.setTag(uri);
            if (mImageCache.get(uri) != null) {
                myHolder.t_image.setImageDrawable(mImageCache.get(uri));
            } else {
                ImageTask it = new ImageTask();
                it.execute(uri);
            }
        }
        return convertView;
    }


    class MyHolder {
        ImageView t_image, t_img_location;
        TextView t_date, t_title, t_description, t_pageView,
                t_vote, t_distance;
    }


    class ImageTask extends AsyncTask<String, Void, BitmapDrawable> {

        private String imageUrl;

        @Override
        protected BitmapDrawable doInBackground(String... params) {
            imageUrl = params[0];
            Bitmap bitmap = downloadImage();
            BitmapDrawable db = new BitmapDrawable(listView.getResources(),
                    bitmap);

            if (mImageCache.get(imageUrl) == null) {
                mImageCache.put(imageUrl, db);
            }
            return db;
        }

        @Override
        protected void onPostExecute(BitmapDrawable result) {

            ImageView iv = (ImageView) listView.findViewWithTag(imageUrl);
            if (iv != null && result != null) {
                iv.setImageDrawable(result);
            }
        }


        private Bitmap downloadImage() {
            HttpURLConnection con = null;
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(10 * 1000);
                bitmap = BitmapFactory.decodeStream(con.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }

            return bitmap;
        }

    }
}
