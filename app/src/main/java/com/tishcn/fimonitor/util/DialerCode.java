package com.tishcn.fimonitor.util;

/**
 * Created by leona on 7/10/2016.
 */
public class DialerCode {

    private int _id;
    private String code;
    private String title;
    private String subTitle;

    public DialerCode(int _id, String code, String title, String subTitle){
        this._id = _id;
        this.code = code;
        this.title = title;
        this.subTitle = subTitle;
    }

    public int getId(){
        return _id;
    }

    public void setId(int id){
        this._id = id;
    }

    public String getCode(){
        return code;
    }

    public void setCode(String code){
        this.code = code;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getSubTitle(){
        return subTitle;
    }

    public void setSubTitle(String subTitle){
        this.subTitle = subTitle;
    }

}
