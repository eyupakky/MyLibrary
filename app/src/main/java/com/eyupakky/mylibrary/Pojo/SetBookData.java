package com.eyupakky.mylibrary.Pojo;

import java.util.List;

/**
 * Created by eyupakkaya on 10.03.2018.
 */

public class SetBookData {
    private String bookName;
    private List<String> bookAuthorName;
    private String bookImageUri;
    private String bookExplanation;
    private String bookId;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookImageUri() {
        return bookImageUri;
    }

    public void setBookImageUri(String bookImageUri) {
        this.bookImageUri = bookImageUri;
    }

    public String getBookExplanation() {
        return bookExplanation;
    }

    public void setBookExplanation(String bookExplanation) {
        this.bookExplanation = bookExplanation;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public List<String> getBookAuthorName() {
        return bookAuthorName;
    }

    public void setBookAuthorName(List<String> bookAuthorName) {
        this.bookAuthorName = bookAuthorName;
    }
}
