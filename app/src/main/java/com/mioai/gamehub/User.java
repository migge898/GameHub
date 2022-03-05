package com.mioai.gamehub;

import java.io.Serializable;

public class User implements Serializable
{
    private String uid;
    private String fullName;
    private String age;
    private String email;

    private boolean isNew;
    private boolean isCreated;
    private boolean isAuthenticated;

    public User(String uid, String fullName, String email)
    {
        this(uid, fullName, email, "");
    }

    public User(String uid, String fullName, String email, String age)
    {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.age = age;
    }

    public String getFullName()
    {
        return fullName;
    }

    public String getAge()
    {
        return age;
    }

    public String getEmail()
    {
        return email;
    }

    public boolean isNew()
    {
        return isNew;
    }

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

    public boolean isCreated()
    {
        return isCreated;
    }

    public void setCreated(boolean created)
    {
        isCreated = created;
    }

    public boolean isAuthenticated()
    {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated)
    {
        isAuthenticated = authenticated;
    }
}
