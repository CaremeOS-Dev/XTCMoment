package com.xtc.game.engine.support;

import android.util.Log;

import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.Skin;
import com.esotericsoftware.spine.attachments.Attachment;
import com.xtc.game.engine.bean.SkinKey;
import com.xtc.game.engine.bean.SlotAttachmentBean;
import com.xtc.game.engine.bean.SpineCodeLoadBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 骨骼辅助工具：按套装名与附件名检索插槽附件数据。
 */
public class SkeletonHelper {

    private static final String TAG = SkeletonHelper.class.getSimpleName();

    public static Skin findSkin(Skeleton skeleton, String skinName) {
        try {
            return skeleton.getData().findSkin(skinName);
        } catch (Exception e) {
            Log.w(TAG, "skinName：" + skinName + " not find with resource", e);
            return null;
        }
    }

    public static SlotAttachmentBean findSlotAttachment(SpineCodeLoadBean loadBean, String suitName, String attachmentName) {
        List<SkinKey> skinKeys = loadBean.getSkinKeys();
        HashMap<SkinKey, List<Attachment>> skinPartMap = loadBean.getSkinPartMap();
        for (SkinKey skinKey : skinKeys) {
            String keySuitName = skinKey.suitName;
            if (keySuitName.equals(suitName)) {
                List<Attachment> attachments = skinPartMap.get(skinKey);
                if (attachments != null && attachments.size() > 0) {
                    Attachment attachment = attachments.get(0);
                    if (attachment.getName().equals(attachmentName)) {
                        return new SlotAttachmentBean(keySuitName, skinKey.slotIndex, skinKey.attachmentName, attachment);
                    }
                }
            }
        }
        return null;
    }

    public static List<SlotAttachmentBean> findSlotAttachments(SpineCodeLoadBean loadBean, String suitName) {
        List<SlotAttachmentBean> result = new ArrayList<>();
        List<SkinKey> skinKeys = loadBean.getSkinKeys();
        HashMap<SkinKey, List<Attachment>> skinPartMap = loadBean.getSkinPartMap();
        for (SkinKey skinKey : skinKeys) {
            String keySuitName = skinKey.suitName;
            if (keySuitName.equals(suitName)) {
                List<Attachment> attachments = skinPartMap.get(skinKey);
                if (attachments != null && attachments.size() > 0) {
                    result.add(new SlotAttachmentBean(keySuitName, skinKey.slotIndex, skinKey.attachmentName, attachments.get(0)));
                }
            }
        }
        return result;
    }

    public static List<SlotAttachmentBean> findSlotAttachments(SpineCodeLoadBean loadBean, String suitName, String slotName) {
        List<SlotAttachmentBean> result = new ArrayList<>();
        List<SkinKey> skinKeys = loadBean.getSkinKeys();
        HashMap<SkinKey, List<Attachment>> skinPartMap = loadBean.getSkinPartMap();
        for (SkinKey skinKey : skinKeys) {
            String keySuitName = skinKey.suitName;
            if (keySuitName.equals(suitName) && skinKey.attachmentName.contains(slotName)) {
                List<Attachment> attachments = skinPartMap.get(skinKey);
                if (attachments != null && attachments.size() > 0) {
                    result.add(new SlotAttachmentBean(keySuitName, skinKey.slotIndex, skinKey.attachmentName, attachments.get(0)));
                }
            }
        }
        return result;
    }
}