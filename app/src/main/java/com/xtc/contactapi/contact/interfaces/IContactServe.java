package com.xtc.contactapi.contact.interfaces;

import com.xtc.contactapi.base.BaseResponse;
import com.xtc.contactapi.contact.bean.ContactBean;

import java.util.List;

import rx.Observable;

/**
 * 联系人服务接口，提供联系人查询、更新与变化监听能力。
 */
public interface IContactServe {

    /** 查询全部联系人。 */
    Observable<BaseResponse> getAllContacts();

    /** 按 contactServerId 查询联系人。 */
    Observable<BaseResponse> getContactByServerId(String serverId);

    /** 按 friendWatchId 或 mobileId 查询联系人。 */
    Observable<BaseResponse> getContactByWatchIdOrMobileId(String id);

    /** 按姓名查询联系人。 */
    Observable<BaseResponse> getContactsByName(String name);

    /** 按 openID 查询联系人。 */
    Observable<BaseResponse> getContactByOpenId(String openId);

    /** 按 contactServerId 查询联系人（服务端 id）。 */
    Observable<BaseResponse> getContactByContactServerId(String contactServerId);

    /** 判断指定 openID 是否为好友。 */
    Observable<BaseResponse> isFriendByOpenId(String openId);

    /** 按 friendWatchId 查询无短号联系人。 */
    Observable<BaseResponse> getContactWithoutShortNumberByWatchId(String watchId);

    /** 按 mobileId 查询无短号联系人。 */
    Observable<BaseResponse> getContactWithoutShortNumberByMobileId(String mobileId);

    /** 按 friendWatchId 查询有短号联系人。 */
    Observable<BaseResponse> getContactWithShortNumberByWatchId(String watchId);

    /** 按 mobileId 查询有短号联系人。 */
    Observable<BaseResponse> getContactWithShortNumberByMobileId(String mobileId);

    /** 注册联系人变化监听。 */
    void registerContactChangeListener(ContactChangeListener listener);

    /** 注册联系人变化监听并指定回调线程。 */
    void registerContactChangeListener(ContactChangeListener listener, int convertCode);

    /** 注销联系人变化监听。 */
    void unregisterContactChangeListener(ContactChangeListener listener);

    /** 更新联系人的视频未接来电数。 */
    boolean updateVideoChatMissedCallCount(ContactBean contactBean);

    /** 获取联系人列表（contactType &lt;= 1）。 */
    List<ContactBean> getContactsByType();

    /** 按角色查询联系人。 */
    List<ContactBean> getContactsByRole(String role);

    /** 全量替换联系人（旧方式）。 */
    @Deprecated
    boolean replaceAll(List<ContactBean> contactList);

    /** 全量替换联系人（JSON 方式）。 */
    boolean replaceAllForJson(List<ContactBean> contactList);
}