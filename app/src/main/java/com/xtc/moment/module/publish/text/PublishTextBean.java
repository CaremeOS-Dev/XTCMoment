package com.xtc.moment.module.publish.text;

import com.xtc.moment.db.bean.DbTemplate;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.bean.PoiBean;

/** Data carried between the text publish pages. */
public class PublishTextBean {

    private int type = -1;
    private FriendsVisibleBean friendsVisibleBean;
    private DbTemplate dbTemplate;
    private String text;
    private PoiBean poiBean;

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public FriendsVisibleBean getFriendsVisibleBean() {
        return this.friendsVisibleBean;
    }

    public void setFriendsVisibleBean(FriendsVisibleBean friendsVisibleBean) {
        this.friendsVisibleBean = friendsVisibleBean;
    }

    public DbTemplate getDbTemplate() {
        return this.dbTemplate;
    }

    public void setDbTemplate(DbTemplate dbTemplate) {
        this.dbTemplate = dbTemplate;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PoiBean getPoiBean() {
        return this.poiBean;
    }

    public void setPoiBean(PoiBean poiBean) {
        this.poiBean = poiBean;
    }

    @Override
    public String toString() {
        return "PublishTextBean{type=" + this.type + ", friendsVisibleBean=" + this.friendsVisibleBean
                + ", dbTemplate=" + this.dbTemplate + ", text='" + this.text + "', poiBean=" + this.poiBean + '}';
    }
}