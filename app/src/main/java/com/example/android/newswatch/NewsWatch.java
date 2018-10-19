package com.example.android.newswatch;

public class NewsWatch {

    //url of the news article
    private String mWebAddress;

    //details of the news article
    private String mSectionDetails;

    //title of the news article
    private String mSectionTitle;

    //published time of the news article
    private String mPublishDate;

    //author of the news article
    private String mAuthor;

    public NewsWatch(String publishDate, String sectionTitle, String sectionDetails, String author,String webAddress){
        mWebAddress = webAddress;
        mSectionDetails = sectionDetails;
        mSectionTitle = sectionTitle;
        mAuthor = author;
        mPublishDate = publishDate;
    }

    public String getWebAddress(){
        return mWebAddress;
    }
    public String getSectionDetails(){
        return mSectionDetails;
    }
    public String getSectionTitle(){
        return mSectionTitle;
    }
    public String getPublishDate(){
        return mPublishDate;
    }
    public String getAuthor(){
        return mAuthor;
    }
}
