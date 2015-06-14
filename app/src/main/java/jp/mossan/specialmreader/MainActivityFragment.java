package jp.mossan.specialmreader;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.net.Uri;
import android.widget.SimpleAdapter;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.mossan.specialmreader.rss.Feed;
import jp.mossan.specialmreader.rss.FeedItem;
import jp.mossan.specialmreader.rss.FeedReader;

import static android.content.Intent.ACTION_VIEW;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static final String RSS_URL = "http://www.rssmix.com/u/8138928/rss.xml";

    public static final String RSS_TITLE = "text";

    public static final String RSS_SITE_NAME = "site_name";

    public static final String RSS_UPDATE = "site_update";

    public static Map<String,String> SITE_NAME_MAP;

    static {
        SITE_NAME_MAP = new HashMap<String,String>();
        SITE_NAME_MAP.put("dqnplus","痛いニュース");
        SITE_NAME_MAP.put("news23vip","VIPPERな俺");
        SITE_NAME_MAP.put("ko_jo","2cコピペ情報局");
    }

    Handler guiThreadHandler;

    ListView mMainListView;

    SimpleAdapter mMaindapter;

    ArrayList<FeedItem> mFeedItems;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        guiThreadHandler = new Handler();
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        mMainListView = (ListView) getActivity().findViewById(R.id.mainListView);

        // 通信処理開始
        new Thread(new Runnable() {
            @Override
            public void run() {

                final List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();

                try {
                    URL url = new URL(RSS_URL);
                    Feed feed = FeedReader.read(true, url);

                    mFeedItems = feed.getItems();
                    for (FeedItem feedItem : mFeedItems) {
                        Log.w("Atom & RSS Reader", feedItem.getTitle());
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(RSS_TITLE, feedItem.getTitle());
                        map.put(RSS_SITE_NAME, createSiteName(feedItem));
                        map.put(RSS_UPDATE, feedItem.getDate());
                        listData.add(map);
                    }
                    if (mFeedItems == null) {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

                // リストへ反映
                guiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMaindapter = new SimpleAdapter(getActivity(), listData, R.layout.main_list_content,
                                new String[]{RSS_TITLE, RSS_SITE_NAME, RSS_UPDATE}, new int[]{R.id.text, R.id.site_name, R.id.site_update});

                        mMainListView.setAdapter(mMaindapter);
                        mMainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                FeedItem item = mFeedItems.get((int) id);
                                try {
                                    Uri uri = Uri.parse(item.getLinks().get(0).getLink());
                                    Intent intent = new Intent(ACTION_VIEW, uri);
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                });


            }
        }).start();
    }


    public String createSiteName(FeedItem item) {
        String guid = item.getGuid();
        if (guid.startsWith("http://alfalfalfa.com/")) {
            return "アルファルファモザイク";
        }

        return SITE_NAME_MAP.get(item.getAuthor());

    }

}