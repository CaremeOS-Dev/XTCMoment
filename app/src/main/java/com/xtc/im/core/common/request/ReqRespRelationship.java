package com.xtc.im.core.common.request;

import com.xtc.im.core.common.request.entity.AccountRequestEntity;
import com.xtc.im.core.common.request.entity.AllSetRequestEntity;
import com.xtc.im.core.common.request.entity.ClearMsgRequestEntity;
import com.xtc.im.core.common.request.entity.EncryptSetRequestEntity;
import com.xtc.im.core.common.request.entity.HeartBeatRequestEntity;
import com.xtc.im.core.common.request.entity.LoginRequestEntity;
import com.xtc.im.core.common.request.entity.MessageMergeRequestEntity;
import com.xtc.im.core.common.request.entity.MessageRequestEntity;
import com.xtc.im.core.common.request.entity.ModeRequestEntity;
import com.xtc.im.core.common.request.entity.PublicKeyRequestEntity;
import com.xtc.im.core.common.request.entity.PushResponseAckRequestEntity;
import com.xtc.im.core.common.request.entity.ReadAckRequestEntity;
import com.xtc.im.core.common.request.entity.RegistRequestEntity;
import com.xtc.im.core.common.request.entity.SingleMessageRequestEntity;
import com.xtc.im.core.common.request.entity.SyncFinishAckRequestEntity;
import com.xtc.im.core.common.request.entity.SyncRequestEntity;
import com.xtc.im.core.common.request.entity.SyncTriggerRequestEntity;
import com.xtc.im.core.common.request.entity.TranspondRequestEntity;
import com.xtc.im.core.common.request.entity.third.AliasAndTagRequestEntity;
import com.xtc.im.core.common.request.entity.third.ThirdSyncFinAckRequestEntity;
import com.xtc.im.core.common.request.entity.third.ThirdSyncRequestEntity;
import com.xtc.im.core.common.request.entity.third.ThirdSyncTriggerRequestEntity;
import com.xtc.im.core.common.response.entity.AccountResponseEntity;
import com.xtc.im.core.common.response.entity.AllSetResponseEntity;
import com.xtc.im.core.common.response.entity.ClearMsgResponseEntity;
import com.xtc.im.core.common.response.entity.EncryptSetResponseEntity;
import com.xtc.im.core.common.response.entity.ErrorResponseEntity;
import com.xtc.im.core.common.response.entity.HeartBeatResponseEntity;
import com.xtc.im.core.common.response.entity.LoginResponseEntity;
import com.xtc.im.core.common.response.entity.MessageMergeResponseEntity;
import com.xtc.im.core.common.response.entity.MessageResponseEntity;
import com.xtc.im.core.common.response.entity.ModeResponseEntity;
import com.xtc.im.core.common.response.entity.PublicKeyResponseEntity;
import com.xtc.im.core.common.response.entity.PushResponseEntity;
import com.xtc.im.core.common.response.entity.ReadAckResponseEntity;
import com.xtc.im.core.common.response.entity.RegistResponseEntity;
import com.xtc.im.core.common.response.entity.SyncFinishResponseEntity;
import com.xtc.im.core.common.response.entity.SyncInformResponseEntity;
import com.xtc.im.core.common.response.entity.SyncResponseEntity;
import com.xtc.im.core.common.response.entity.SyncTriggerResponseEntity;
import com.xtc.im.core.common.response.entity.TranspondResponseEntity;
import com.xtc.im.core.common.response.entity.VoiceSliceResponseEntity;
import com.xtc.im.core.common.response.entity.third.AliasAndTagResponseEntity;
import com.xtc.im.core.common.response.entity.third.ThirdSyncFinResponseEntity;
import com.xtc.im.core.common.response.entity.third.ThirdSyncInformResponseEntity;
import com.xtc.im.core.common.response.entity.third.ThirdSyncResponseEntity;
import com.xtc.im.core.common.response.entity.third.ThirdSyncTriggerResponseEntity;

import java.util.HashMap;
import java.util.Map;

/** 命令字与实体类、请求与响应命令字的映射表。 */
public final class ReqRespRelationship {

    static final Map<Integer, Class<?>> COMMAND_CLASS_MAP = new HashMap<>();
    public static final Map<Integer, Integer> REQUEST_RESPONSE_COMMAND_MAP = new HashMap<>();

    static {
        COMMAND_CLASS_MAP.put(Command.REGIST_REQUEST, RegistRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.REGIST_RESPONSE, RegistResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.REGIST_REQUEST, Command.REGIST_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.LOGIN_REQUEST, LoginRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.LOGIN_RESPONSE, LoginResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.LOGIN_REQUEST, Command.LOGIN_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.ACCOUNT_REQUEST, AccountRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.ACCOUNT_RESPONSE, AccountResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.ACCOUNT_REQUEST, Command.ACCOUNT_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.HEART_BEAT_REQUEST, HeartBeatRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.HEART_BEAT_RESPONSE, HeartBeatResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.HEART_BEAT_REQUEST, Command.HEART_BEAT_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.MSG_REQUEST, MessageRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.MSG_RESPONSE, MessageResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.MSG_REQUEST, Command.MSG_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.SYNC_INFORM, SyncInformResponseEntity.class);
        COMMAND_CLASS_MAP.put(Command.SYNC_REQUEST, SyncRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.SYNC_RESPONSE, SyncResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.SYNC_REQUEST, Command.SYNC_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.SYNC_FINISH, SyncFinishResponseEntity.class);
        COMMAND_CLASS_MAP.put(Command.SYNC_FINISH_ACK, SyncFinishAckRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.PUSH_RESPONSE_ACK, PushResponseAckRequestEntity.class);

        COMMAND_CLASS_MAP.put(Command.READ_ACK_REQUEST, ReadAckRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.READ_ACK_RESPONSE, ReadAckResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.READ_ACK_REQUEST, Command.READ_ACK_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.ALL_SET_REQUEST, AllSetRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.ALL_SET_RESPONSE, AllSetResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.ALL_SET_REQUEST, Command.ALL_SET_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.TRANSPOND_REQUEST, TranspondRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.TRANSPOND_RESPONSE, TranspondResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.TRANSPOND_REQUEST, Command.TRANSPOND_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.PUBLICKEY_REQUEST, PublicKeyRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.PUBLICKEY_RESPONSE, PublicKeyResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.PUBLICKEY_REQUEST, Command.PUBLICKEY_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.VOICE_SLICE, VoiceSliceResponseEntity.class);

        COMMAND_CLASS_MAP.put(Command.SYNC_TRIGGER_REQUEST, SyncTriggerRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.SYNC_TRIGGER_RESPONSE, SyncTriggerResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.SYNC_TRIGGER_REQUEST, Command.SYNC_TRIGGER_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.CLEAR_MSG_REQUEST, ClearMsgRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.CLEAR_MSG_RESPONSE, ClearMsgResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.CLEAR_MSG_REQUEST, Command.CLEAR_MSG_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.ENCRYPT_WAPPER, EncryptWapper.class);
        COMMAND_CLASS_MAP.put(Command.ENCRYPT_SET_REQUEST, EncryptSetRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.ENCRYPT_SET_RESPONSE, EncryptSetResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.ENCRYPT_SET_REQUEST, Command.ENCRYPT_SET_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.PUSH_RESPONSE, PushResponseEntity.class);

        COMMAND_CLASS_MAP.put(Command.THIRD_ALIAS_AND_TAG_REQUEST, AliasAndTagRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.THIRD_ALIAS_AND_TAG_RESPONSE, AliasAndTagResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.THIRD_ALIAS_AND_TAG_REQUEST, Command.THIRD_ALIAS_AND_TAG_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.THIRD_SYNC_REQUEST, ThirdSyncRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.THIRD_SYNC_RESPONSE, ThirdSyncResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.THIRD_SYNC_REQUEST, Command.THIRD_SYNC_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.THIRD_SYNC_TRIGGER_REQUEST, ThirdSyncTriggerRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.THIRD_SYNC_TRIGGER_RESPONSE, ThirdSyncTriggerResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.THIRD_SYNC_TRIGGER_REQUEST, Command.THIRD_SYNC_TRIGGER_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.THIRD_SYNC_FIN_ACK, ThirdSyncFinAckRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.THIRD_SYNC_FIN, ThirdSyncFinResponseEntity.class);
        COMMAND_CLASS_MAP.put(Command.THIRD_SYNC_INFORM, ThirdSyncInformResponseEntity.class);

        COMMAND_CLASS_MAP.put(Command.SINGLE_MSG_REQUEST, SingleMessageRequestEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.SINGLE_MSG_REQUEST, Command.MSG_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.MSG_MERGE_REQUEST, MessageMergeRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.MSG_MERGE_RESPONSE, MessageMergeResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.MSG_MERGE_REQUEST, Command.MSG_MERGE_RESPONSE);

        COMMAND_CLASS_MAP.put(Command.ERROE_RESPONSE, ErrorResponseEntity.class);

        COMMAND_CLASS_MAP.put(Command.COMMON_BUSINESS_REQ, ModeRequestEntity.class);
        COMMAND_CLASS_MAP.put(Command.COMMON_BUSINESS_RESP, ModeResponseEntity.class);
        REQUEST_RESPONSE_COMMAND_MAP.put(Command.COMMON_BUSINESS_REQ, Command.COMMON_BUSINESS_RESP);
    }
}