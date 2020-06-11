package com.example.android.popularmovies.model;

public class Review {
    private String author;
    private String review;


    public Review(){
    }
/*
    public Review(String writer, String content){
        author = writer;
        review = content;
    }*/

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String writer) {
        author = writer;
    }

    public String getContent() {
        return review;
    }

    public void setContent(String content) {
        review = content;
    }
}
