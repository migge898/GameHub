package com.mioai.gamehub.utils;

import android.util.Log;

import com.google.android.gms.tasks.Task;

public class HelperClass
{
    public static void logErrorMessage(Object currentClass, String errorMessage)
    {
        Log.e(currentClass.getClass().getSimpleName(), errorMessage);
    }

    public static void logErrorMessage(Object currentClass, Task task)
    {
        Log.e(currentClass.getClass().getSimpleName(), task.getException().getMessage());
    }

}
