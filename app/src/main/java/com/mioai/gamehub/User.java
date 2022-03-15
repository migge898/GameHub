package com.mioai.gamehub;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class User implements Serializable
{
    private String uid;
    private String age;
    private String email;
    private String username;

    private boolean isNew;
    private boolean isCreated;
    private boolean isAuthenticated;

    public User()
    {

    }
    public User(String uid, String username, String email)
    {
        this(uid, username, email, "");
    }

    public User(String uid, String username, String email, String age)
    {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.age = age;
    }

    public String getAge()
    {
        return age;
    }

    public String getEmail()
    {
        return email;
    }

    @Exclude
    public boolean isNew()
    {
        return isNew;
    }

    @Exclude
    public void setNew(boolean aNew)
    {
        isNew = aNew;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    @Exclude
    public boolean isCreated()
    {
        return isCreated;
    }

    @Exclude
    public void setCreated(boolean created)
    {
        isCreated = created;
    }

    @Exclude
    public boolean isAuthenticated()
    {
        return isAuthenticated;
    }

    @Exclude
    public void setAuthenticated(boolean authenticated)
    {
        isAuthenticated = authenticated;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }
}
