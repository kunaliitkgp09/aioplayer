package com.aioplayer.manager;

import android.accounts.Account;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.aioplayer.dao.AudioSong;
import com.aioplayer.dao.PlayListItem;
import com.aioplayer.dao.PodCastItem;
import com.aioplayer.dao.PodCastItemDownload;
import com.aioplayer.dao.RssFeedItem;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.List;

/**
 * Created by akankshadhanda on 01/08/17.
 */

public class DbManager {
    private Context context;
    private DBHelper dbHelper ;
    private Dao RssFeeddoaValue;
    private Dao podcastItemdoaValue;
    private Dao playListItemDao;
    private Dao podcastitemDoaNormal;
    public DbManager(Context context)
    {
       this.context=context;
        this.dbHelper = new DBHelper(context);
        try {
            this.RssFeeddoaValue=dbHelper.getDao(RssFeedItem.class);
            this.podcastItemdoaValue=dbHelper.getDao(PodCastItemDownload.class);
            this.playListItemDao=dbHelper.getDao(PlayListItem.class);
            this.podcastitemDoaNormal=dbHelper.getDao(PodCastItem.class);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
    public void saveRssObject(RssFeedItem rssFeedItem)
    {
        try {
            RssFeeddoaValue.createIfNotExists(rssFeedItem);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }
    public void saveRssObjects(List<RssFeedItem> rssFeedItems)
    {
        try {
            for(RssFeedItem podCastItem:rssFeedItems) {
                RssFeeddoaValue.createIfNotExists(podCastItem);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

    public void removeRssObject(RssFeedItem rssFeedItems)
    {
        try {

            RssFeeddoaValue.delete(rssFeedItems);

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }
    public List<RssFeedItem> getAllRssObjects()
    {

        try {
            return RssFeeddoaValue.queryForAll();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

         return null;
    }


    public void savePodcastItemObject(PodCastItemDownload podCastItemDownload)
    {
        try {
            podcastItemdoaValue.createIfNotExists(podCastItemDownload);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }
    public void savePodCastItemObjects(List<PodCastItemDownload> podCastItemDownloads)
    {
        try {
            for(PodCastItemDownload podCastItem:podCastItemDownloads) {
                podcastItemdoaValue.createIfNotExists(podCastItem);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

    public void savePodCastItemNormalObjects(List<PodCastItem> podCastItemDownloads)
    {
        try {
            for(PodCastItem podCastItem:podCastItemDownloads) {
                podcastitemDoaNormal.createIfNotExists(podCastItem);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

    public void savePodCastItemNormalObject(PodCastItem podCastItemDownload)
    {
        try {
            podcastitemDoaNormal.createIfNotExists(podCastItemDownload);

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }
    public void removeObject(PodCastItem podCastItemDownload)
    {
        try {

            podcastitemDoaNormal.delete(podCastItemDownload);

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

    public List<PodCastItem> fetchPodCastItemsNormal(String podcasturl)
    {
        try {
            QueryBuilder<PodCastItem, String> queryBuilder = podcastitemDoaNormal.queryBuilder();
            Where<PodCastItem, String> where = queryBuilder.where();
            SelectArg selectArg = new SelectArg();
            where.eq("podcastUrl", selectArg);
            PreparedQuery<PodCastItem> preparedQuery = queryBuilder.prepare();
            selectArg.setValue(podcasturl);
            return podcastitemDoaNormal.query(preparedQuery);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void removeObject(PodCastItemDownload podCastItemDownload)
    {
        try {

            podcastItemdoaValue.delete(podCastItemDownload);

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }
    public List<PodCastItem> getAllPodCastNormalItemObjects()
    {

        try {
            return podcastitemDoaNormal.queryForAll();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<PodCastItemDownload> getAllPodCastItemObjects()
    {

        try {
            return podcastItemdoaValue.queryForAll();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public boolean podCastItemExits(PodCastItemDownload podCastItemDownload)
    {

        try {
            return podcastItemdoaValue.idExists(podCastItemDownload.getFeedUrl());
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public PodCastItemDownload findPodCastItemwithId(String id)
    {

        try {
            return (PodCastItemDownload) podcastItemdoaValue.queryForId(id);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;

    }
    public void updatePodCastItem(PodCastItemDownload podCastItemDownload)
    {

        try {
             podcastItemdoaValue.update(podCastItemDownload);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }
    public void removeObject(PlayListItem removePlayListItem)
    {


        try {
            this.playListItemDao.delete(removePlayListItem);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }


    public List<PlayListItem> getAllPlayListItems()
    {

        try {
            return playListItemDao.queryForAll();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public boolean playListItemExits(PlayListItem playListItem)
    {

        try {
            return playListItemDao.idExists(playListItem.getPlayListName());
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public PlayListItem findPlayListItemwithId(String id)
    {

        try {
            return (PlayListItem) playListItemDao.queryForId(id);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;

    }
    public void updatePlayList(PlayListItem podCastItemDownload)
    {

        try {
           int number= playListItemDao.update(podCastItemDownload);
            if(number==0)
            {
                playListItemDao.createIfNotExists(podCastItemDownload);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

    public void savePlayList(PlayListItem playListItem)
    {

        try {
            playListItemDao.createIfNotExists(playListItem);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }


  class DBHelper extends OrmLiteSqliteOpenHelper {

        // Fields

        public static final String DB_NAME = "student_manager.db";
        private static final int DB_VERSION = 1;

        // Public methods

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            getWritableDatabase();
        }

        @Override
        public void onCreate(SQLiteDatabase db, ConnectionSource cs) {
            try {

                // Create Table with given table name with columnName
                TableUtils.createTable(cs, RssFeedItem.class);
                TableUtils.createTable(cs, PodCastItemDownload.class);
                TableUtils.createTable(cs, PlayListItem.class);
                TableUtils.createTable(cs, AudioSong.class);
                TableUtils.createTable(cs, PodCastItem.class);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) {

        }
    }
}
