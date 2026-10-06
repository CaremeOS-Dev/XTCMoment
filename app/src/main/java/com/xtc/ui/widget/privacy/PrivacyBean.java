package com.xtc.ui.widget.privacy;

import java.util.ArrayList;
import java.util.List;

/** Aggregated privacy information for one package. */
public class PrivacyBean {
    private List<String> list3 = new ArrayList<String>();
    private List<String> list2 = new ArrayList<String>();
    private List<String> list1 = new ArrayList<String>();
    private String privacyUrl = "";
    private String agreementUrl = "";
    private String disclaimerUrl = "";
    private String appName = "";
    private String firstTitle = "";
    private String secondTitle = "";
    private String packageName = "";

    public List<String> getList1() {
        return this.list1;
    }

    public void setList1(List<String> list1) {
        this.list1 = list1;
    }

    public List<String> getList2() {
        return this.list2;
    }

    public void setList2(List<String> list2) {
        this.list2 = list2;
    }

    public List<String> getList3() {
        return this.list3;
    }

    public void setList3(List<String> list3) {
        this.list3 = list3;
    }

    public String getPrivacyUrl() {
        return this.privacyUrl;
    }

    public void setPrivacyUrl(String privacyUrl) {
        this.privacyUrl = privacyUrl;
    }

    public String getAgreementUrl() {
        return this.agreementUrl;
    }

    public void setAgreementUrl(String agreementUrl) {
        this.agreementUrl = agreementUrl;
    }

    public String getDisclaimerUrl() {
        return this.disclaimerUrl;
    }

    public void setDisclaimerUrl(String disclaimerUrl) {
        this.disclaimerUrl = disclaimerUrl;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getFirstTitle() {
        return this.firstTitle;
    }

    public void setFirstTitle(String firstTitle) {
        this.firstTitle = firstTitle;
    }

    public String getSecondTitle() {
        return this.secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return "PrivacyBean{list1=" + this.list1 + ", list2=" + this.list2 + ", list3=" + this.list3 + ", privacyUrl='" + this.privacyUrl + "', agreementUrl='" + this.agreementUrl + "', disclaimerUrl='" + this.disclaimerUrl + "', appName='" + this.appName + "', firstTitle='" + this.firstTitle + "', secondTitle='" + this.secondTitle + "', packageName='" + this.packageName + "'}";
    }
}
