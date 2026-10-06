package com.xtc.virtualselfapi.constants;

/** Constants of the virtual-self API. */
public interface Constants {

    long DEFAULT_INIT_DELAY_TIME = 2000;
    String LOG = "Virtual_Self_Api_";
    String SOURCE_THEIR_TYPE_CLOTHES = "above";
    String SOURCE_THEIR_TYPE_HEAD = "head";
    String SOURCE_THEIR_TYPE_PANTS = "down";
    String SOURCE_THEIR_TYPE_SHOE = "foot";

    /** Animation names. */
    interface AnimConstant {
        int DELAY_START_ANIM_TIME = 1000;
        String VISUAL_I_AM_COMING_ANIMATION = "an_2_wolaile";
        String VISUAL_SAY_HI_ANIMATION = "an_1_hi";
    }

    /** States of the treasure box. */
    interface BoxStatus {
        int HELP_OPEN = 2;
        int LOSE = -1;
        int NORMAL = 0;
        int NOT_EXIST = 99;
        int OPEN = 1;
        int ROB = -2;
    }

    interface CardCostume {
        int EMPTY = 0;
    }

    interface CostumeType {
        int CARD_TYPE = 1;
        int NORMAL_TYPE = 0;
    }

    /** Default skin names. */
    interface DEFAULT_SKIN {
        String DEFAULT_BOY_SKIN = "xiandai_nan_xuesheng";
        String DEFAULT_GIRL_SKIN = "xiandai_nv_xuesheng";
    }

    interface DefaultCostume {
        int BACKGROUND = 201;
        int FEMALE = 10;
        int MALE = 9;
    }

    interface FriendViewStatus {
        int BEAR_SKILL = 10;
        int COUNT_DOWN = 5;
        int DANGER = 1;
        int OTHER = 6;
        int PROTECT = 2;
        int ROB = 3;
        int ROB_ALREADY = 4;
    }

    interface Gender {
        int BOY = 0;
        int GIRL = 1;
        int NONE = -1;
    }

    interface Layer {
        int BG = 0;
        int BOTTOM_ONE = 1;
        int BOTTOM_TWO = 2;
        int SUIT = 3;
        int TOP_ONE = 5;
        int TOP_TWO = 4;
    }

    interface ModuleSwitch {
        int MODULE_SWITCH_COLLECT_CARD = 2645;
    }

    interface SelfViewStatus {
        int BEAR_SKILL = 10;
        int COUNT_DOWN = 2;
        int DANGER = 1;
        int FIND = 4;
        int OPEN = 3;
    }

    interface ShowDefaultScale {
        int DEFAULT_HEIGHT = 360;
        float DEFAULT_SCALE = 0.8f;
        int DEFAULT_WIDTH = 320;
    }

    interface SpKey {
        String CUSTOM_UPDATE_TIME = "custom_update_time";
        String DYNAMICS_SCALE = "dynamics_scale";
        String VERSION = "version1";
    }

    interface Type {
        int AUREOLE = 3;
        int BACKGROUND = 4;
        int DANGER = 5;
        int DECORATE = 2;
        int PET = 1;
        int SUIT = 0;
    }

    interface showPositionCenter {
        int X = 160;
        int Y = 25;
        float scale = 0.8f;
    }
}