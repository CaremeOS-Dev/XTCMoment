package com.xtc.web.core.data.req;

/** H5 请求打开第三方应用的参数。 */
public class ReqJsStartApp {

    /** 跳转后是否展示新的 loading UI。 */
    public interface ShowNewUIType {
        int DISMISS = 0;
        int SHOW = 1;
    }

    private String category;
    private int flag;
    private int intentFlag;
    private int isForResult;
    private String packageName;
    private String targetActivity;
    private String uri;
    private int ignoreDisplay = 0;
    private int isShowNewUI = 0;

    public int getIsShowNewUI() {
        return this.isShowNewUI;
    }

    public void setIsShowNewUI(int isShowNewUI) {
        this.isShowNewUI = isShowNewUI;
    }

    public int getIgnoreDisplay() {
        return this.ignoreDisplay;
    }

    public void setIgnoreDisplay(int ignoreDisplay) {
        this.ignoreDisplay = ignoreDisplay;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getIsForResult() {
        return this.isForResult;
    }

    public void setIsForResult(int isForResult) {
        this.isForResult = isForResult;
    }

    public int getIntentFlag() {
        return this.intentFlag;
    }

    public void setIntentFlag(int intentFlag) {
        this.intentFlag = intentFlag;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTargetActivity() {
        return this.targetActivity;
    }

    public void setTargetActivity(String targetActivity) {
        this.targetActivity = targetActivity;
    }

    @Override
    public String toString() {
        return "ReqJsStartApp{packageName='" + this.packageName + "', uri='" + this.uri + "', flag=" + this.flag
                + ", intentFlag=" + this.intentFlag + ", category='" + this.category + "', isForResult="
                + this.isForResult + ", targetActivity='" + this.targetActivity + "'}";
    }
}