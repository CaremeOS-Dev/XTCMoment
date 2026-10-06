package com.xtc.virtualselfapi.utils;

import com.esotericsoftware.spine.attachments.SkeletonAttachment;
import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.game.engine.bean.SlotAttachmentBean;
import com.xtc.game.engine.bean.SpineCodeLoadBean;
import com.xtc.game.engine.support.SkeletonHelper;
import com.xtc.virtualselfapi.bean.ResourceAttachmentBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 装扮资源附件与插槽附件之间的转换工具。
 */
public class CharacterDataConvertUtil {

    public static ResourceAttachmentBean convertResourceAttachment(SlotAttachmentBean slotAttachment) {
        ResourceAttachmentBean resourceAttachment = new ResourceAttachmentBean();
        resourceAttachment.setDressBelongSuit(slotAttachment.getSuitName());
        resourceAttachment.setDressAnnexName(slotAttachment.getAttachment().getName());
        resourceAttachment.setDressAnnexType(slotAttachment.getSlotName());
        resourceAttachment.setDressAnnexSlot(slotAttachment.getSlotIndex() + "");
        return resourceAttachment;
    }

    public static SlotAttachmentBean convertSlotAttachment(ResourceAttachmentBean resourceAttachment) {
        SlotAttachmentBean slotAttachment = new SlotAttachmentBean();
        slotAttachment.setAttachment(new SkeletonAttachment(resourceAttachment.getDressAnnexName()));
        slotAttachment.setSlotIndex(Integer.parseInt(resourceAttachment.getDressAnnexSlot()));
        slotAttachment.setSlotName(resourceAttachment.getDressAnnexType());
        slotAttachment.setSuitName(resourceAttachment.getDressBelongSuit());
        return slotAttachment;
    }

    public static SlotAttachmentBean convertSlotAttachment(ResourceAttachmentBean resourceAttachment, SpineCodeLoadBean loadBean) {
        return SkeletonHelper.findSlotAttachment(loadBean, resourceAttachment.getDressBelongSuit(), resourceAttachment.getDressAnnexName());
    }

    public static List<SlotAttachmentBean> convertSlotAttachment(List<ResourceAttachmentBean> resourceAttachments) {
        List<SlotAttachmentBean> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(resourceAttachments)) {
            return result;
        }
        for (ResourceAttachmentBean resourceAttachment : resourceAttachments) {
            result.add(convertSlotAttachment(resourceAttachment));
        }
        return result;
    }

    public static List<SlotAttachmentBean> convertSlotAttachment(List<ResourceAttachmentBean> resourceAttachments, SpineCodeLoadBean loadBean) {
        List<SlotAttachmentBean> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(resourceAttachments)) {
            return result;
        }
        for (ResourceAttachmentBean resourceAttachment : resourceAttachments) {
            result.add(convertSlotAttachment(resourceAttachment, loadBean));
        }
        return result;
    }
}