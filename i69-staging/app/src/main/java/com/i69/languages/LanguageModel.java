package com.i69.languages;

import androidx.annotation.Keep;

@Keep
public class LanguageModel {


    private String languageName, languageCode, supportedLangCode, languageMeaning;
    private int flag,id;
    private boolean isSupported;


    public LanguageModel(int id,String supportedLangCode, String languageName, String languageCode, int flag, String languageMeaning) {
        this.supportedLangCode = supportedLangCode;
        this.languageName = languageName;
        this.languageCode = languageCode;
        this.flag = flag;
        this.languageMeaning = languageMeaning;
        this.id=id;
    }

    public LanguageModel(int id,String supportedLangCode, String languageName, String languageCode, int flag) {
        this.supportedLangCode = supportedLangCode;
        this.languageName = languageName;
        this.languageCode = languageCode;
        this.flag = flag;
        this.id=id;
    }


    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSupported() {
        return isSupported;
    }

    public void setSupported(boolean supported) {
        isSupported = supported;
    }

    public String getSupportedLangCode() {
        return supportedLangCode;
    }

    public void setSupportedLangCode(String supportedLangCode) {
        this.supportedLangCode = supportedLangCode;
    }

    public String getLanguageMeaning() {
        return languageMeaning;
    }

    public void setLanguageMeaning(String languageMeaning) {
        this.languageMeaning = languageMeaning;
    }
}
