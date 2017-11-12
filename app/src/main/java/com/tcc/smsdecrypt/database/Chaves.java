package com.tcc.smsdecrypt.database;

import android.provider.BaseColumns;

/**
 * Created by claudinei on 21/09/17.
 */

public final class Chaves {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Chaves() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "chave";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_TOKEN = "token";
        public static final String COLUMN_REFRESH_TOKEN = "refresh_token";


    }
}

