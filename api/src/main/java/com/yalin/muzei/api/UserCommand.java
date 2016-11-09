package com.yalin.muzei.api;

import android.text.TextUtils;

/**
 * YaLin
 * 2016/11/9.
 */

public class UserCommand {
    private int mId;
    private String mTitle;

    public UserCommand(int id) {
        mId = id;
    }

    public UserCommand(int id, String title) {
        mId = id;
        mTitle = title;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String serialize() {
        return Integer.toString(mId) + (TextUtils.isEmpty(mTitle) ? "" : (":" + mTitle));
    }

    public static UserCommand deserialize(String s) {
        int id = -1;
        if (TextUtils.isEmpty(s)) {
            return new UserCommand(id, null);
        }

        String[] arr = s.split(":", 2);
        try {
            id = Integer.parseInt(arr[0]);
        } catch (NumberFormatException ignored) {
        }

        String title = null;
        if (arr.length > 1) {
            title = arr[1];
        }

        return new UserCommand(id, title);
    }
}
