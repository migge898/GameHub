package com.mioai.gamehub.utils;

import java.util.UUID;

public class UIDGenerator
{
    /**
     * Generates a dashless UUID
     * @return
     */
    public static String randomUID()
    {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
