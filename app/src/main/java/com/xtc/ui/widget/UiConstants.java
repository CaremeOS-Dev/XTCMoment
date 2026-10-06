package com.xtc.ui.widget;

/** Shared colour/dimension/icon tables for the widget library. */
public class UiConstants {

    /** Gradient colour pairs, referenced by resource id. */
    public static class Color {
        public static final int[] RED = {R.color.color_ff6029, R.color.color_ff3535};
        public static final int[] GREED = {R.color.color_55dd7b, R.color.color_0ab158};
        public static final int[] GRAY = {R.color.color_373737, R.color.color_333333};
        public static final int[] GRAY2 = {R.color.color_555555, R.color.color_444444};
        public static final int[] WHITE = {R.color.color_fafafa, R.color.color_eeeeee};
        public static final int[] YELLOW = {R.color.color_ffa200, R.color.color_ff9500};
        public static final int[] PURPLE = {R.color.color_E66195fc, R.color.color_E65c45fe};
        public static final int[] BLUE = {R.color.color_2bdbff, R.color.color_1794fa};
        public static final int[] MASK = {R.color.color_4d000000, R.color.color_4d000000};
        public static final int[] GRAY_NO_GRADIENT = {R.color.color_2f2f2f, R.color.color_2f2f2f};
        public static final int[] GRAY_NO_GRADIENT2 = {R.color.color_2a2b2c, R.color.color_2a2b2c};
    }

    /** Default sizes in dp. */
    public static class Dimen {
        public static final int DEFAULT_BIG_ROUND_RADIUS = 20;
        public static final int DEFAULT_CANCEL_ROUND_RADIUS = 16;
        public static final int DEFAULT_HEIGHT = 40;
        public static final int DEFAULT_RADIUS = 30;
        public static final int DEFAULT_SMALL_ROUND_RADIUS = 4;
        public static final int DEFAULT_STROKE_WIDTH = 1;
        public static final int DEFAULT_WIDTH = 150;
        public static final int DEFAULT_WIDTH_THREE = 79;
        public static final int DEFAULT_WIDTH_TWO = 160;
        public static final int INVALID_VALUE = -1;
    }

    /** Gradient orientation kinds. */
    public static class Gradient {
        public static final int LINEAR = 0;
        public static final int RADIAL = 1;
        public static final int SWEEP = 2;
    }

    /** Built-in icon ids. */
    public static class Icon {
        public static final int CANCEL = 0;
        public static final int CANCEL_FOCUS = 25;
        public static final int CHECK_SELECT = 9;
        public static final int COLLECTION = 16;
        public static final int CONTINUE_DOWNLOAD = 21;
        public static final int DELETE = 2;
        public static final int DISCONNECT = 24;
        public static final int MULTIPLE_NOT_SELECT = 7;
        public static final int MULTIPLE_SELECT = 8;
        public static final int NETFLOW_DOWNLOAD = 22;
        public static final int PHONE_REFUSE = 4;
        public static final int PHONE_TAKE = 3;
        public static final int PHONE_TAKE_GREEN = 20;
        public static final int PLAY = 11;
        public static final int REMARK = 18;
        public static final int RESTART = 15;
        public static final int RIGHT_ARROW = 10;
        public static final int SHUT_DOWN = 14;
        public static final int SINGLE_NOT_SELECT = 5;
        public static final int SINGLE_SELECT = 6;
        public static final int SMALL_VIDEO = 13;
        public static final int SURE = 1;
        public static final int UNBIND = 26;
        public static final int UNCOLLECTED = 17;
        public static final int VIDEO_COMMUNICATION = 12;
        public static final int VOICE_COMMUNICATION = 19;
        public static final int WIFI_DOWNLOAD = 23;
    }

    /** Drawable shape kinds. */
    public static class Shape {
        public static final int LINE = 2;
        public static final int OVAL = 1;
        public static final int RECTANGLE = 0;
        public static final int RING = 3;
    }
}
