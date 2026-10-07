package com.opensource.svgaplayer;

/**
 * SVGAPlayer 以本地 jar 形式引入，AGP 不会为其生成 R 类，
 * 这里补齐资源常量（数值取自本工程合并后的资源表，与 SVGAImageView 读取顺序一致）。
 */
public final class R {

    private R() {
    }

    public static final class styleable {
        public static final int SVGAImageView_antiAlias = 0;
        public static final int SVGAImageView_autoPlay = 1;
        public static final int SVGAImageView_clearsAfterStop = 2;
        public static final int SVGAImageView_fillMode = 3;
        public static final int SVGAImageView_loopCount = 4;
        public static final int SVGAImageView_source = 5;

        public static final int[] SVGAImageView = com.xtc.moment.R.styleable.SVGAImageView;

        private styleable() {
        }
    }
}
