package com.xtc.moment.net.bean;

import java.util.List;

/**
 * Batch request for avatars configured on the self-hosted server.
 *
 * <p>The watch normally takes names and avatars from the contact provider.
 * This request only overlays server avatars on those provider records, so a
 * provider-only friend can still use an avatar uploaded in the dashboard.</p>
 */
public class GetFriendAvatarBean {

    private List<String> watchIds;

    public List<String> getWatchIds() {
        return this.watchIds;
    }

    public void setWatchIds(List<String> watchIds) {
        this.watchIds = watchIds;
    }
}
