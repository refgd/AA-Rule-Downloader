package com.skyolin.aaruledownloader.SQHelper;

/**
 * Created by Jack on 2016/4/22.
 */
public class ruleField {
    //private variables
    String _siteindex;
    String _sitename;
    int _version;
    String _siteurl;
    int _hasUpdate = 1;
    boolean _downloading = false;

    // Empty constructor
    public ruleField(){

    }
    // constructor
    public ruleField(String siteindex, String sitename, int version, String siteurl){
        this._siteindex = siteindex;
        this._sitename = sitename;
        this._version = version;
        this._siteurl = siteurl;
    }
    // constructor
    public ruleField(String siteindex, String sitename, int version, String siteurl, int hasup){
        this._siteindex = siteindex;
        this._sitename = sitename;
        this._version = version;
        this._siteurl = siteurl;
        this._hasUpdate = hasup;
    }

    public String getIndex(){
        return _siteindex;
    }

    public String getName(){
        return _sitename;
    }

    public int getVersion(){
        return _version;
    }

    public String getUrl(){
        return _siteurl;
    }

    public int hasUpdate(){
        return _hasUpdate;
    }

    public void setUpdate(int h){
        _hasUpdate = h;
    }

    public Boolean isDownloading(){
        return _downloading;
    }

    public void setDownloading(boolean h){
        _downloading = h;
    }
}
