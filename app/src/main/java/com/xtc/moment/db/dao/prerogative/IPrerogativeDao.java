package com.xtc.moment.db.dao.prerogative;

import java.util.List;

/**
 * 特权装扮资源 DAO 通用接口。
 */
public interface IPrerogativeDao<T> {

    List<T> queryDataForAll();

    T queryDbForPrerogativeId(int prerogativeId);

    boolean updateLocalPrerogativeById(T item);

    boolean insertList(List<T> list);

    int deleteDataForAll();
}