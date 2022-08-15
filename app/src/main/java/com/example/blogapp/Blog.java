package com.example.blogapp;

public class Blog {

    private String Title;
    private String Description;
    private String PostImageLink;
    private String username;

    public Blog(){

    }

    public Blog(String title, String description, String postImageLink) {
        this.Title = title;
        this.Description = description;
        this.PostImageLink = postImageLink;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPostImageLink() {
        return PostImageLink;
    }

    public void setPostImageLink(String postImageLink) {
        PostImageLink = postImageLink;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
