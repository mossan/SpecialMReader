/*
 * Copyright (C) 2011 Mats Hofman <http://matshofman.nl/contact/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.mossan.specialmreader.rss;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Feed implements Parcelable {

    public enum FEED_TYPE {UNKNOWN, RSS, ATOM}

    @Getter
    @Setter
    protected FEED_TYPE feedtype = FEED_TYPE.UNKNOWN;

    @Getter
    @Setter
    protected String title;

    @Getter
    @Setter
    protected String subtitle;

    @Getter
    @Setter
    protected String id;

    @Getter
    @Setter
    protected String description;

    @Getter
    @Setter
    private String language;

    @Getter
    protected Date updated;

    @Getter
    protected Date lastBuildDate;

    protected Date pubDate;

    @Getter
    @Setter
    protected ArrayList<FeedItem> items;

    @Getter
    @Setter
    protected ArrayList<Link> links;

    public Feed() {
        items = new ArrayList<FeedItem>();
        links = new ArrayList<Link>();
    }

    public Feed(Parcel source) {

        Bundle data = source.readBundle();
        feedtype = (FEED_TYPE) data.getSerializable("feedtype");
        title = data.getString("title");
        description = data.getString("description");
        language = data.getString("language");
        updated = (Date) data.getSerializable("updated");
        lastBuildDate = (Date) data.getSerializable("lastBuildDate");
        pubDate = (Date) data.getSerializable("pubDate");
        items = data.getParcelableArrayList("items");
        links = data.getParcelableArrayList("links");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        Bundle data = new Bundle();
        data.putSerializable("feedtype", feedtype);
        data.putString("title", title);
        data.putString("description", description);
        data.putString("language", language);
        data.putSerializable("updated", updated);
        data.putSerializable("lastBuildDate", lastBuildDate);
        data.putSerializable("pubDate", pubDate);
        data.putParcelableArrayList("items", items);
        data.putParcelableArrayList("links", links);
        dest.writeBundle(data);
    }

    public static final Creator<Feed> CREATOR = new Creator<Feed>() {
        public Feed createFromParcel(Parcel data) {
            return new Feed(data);
        }

        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void setUpdated(String updated) {
        this.updated = Utils.parseDate(updated);
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = Utils.parseDate(lastBuildDate);
    }

    public void setLastBuildDate(Date lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = Utils.parseDate(pubDate);
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public Date getDate() {
        if (updated != null) {
            return updated;
        } else if (lastBuildDate != null) {
            return lastBuildDate;
        } else {
            return pubDate;
        }
    }

    void addItem(FeedItem feedItem) {
        items.add(feedItem);
    }

    void addLink(Link feedItemLink) {
        links.add(feedItemLink);
    }

}
