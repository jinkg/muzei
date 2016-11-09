package com.yalin.muzei.api.internal;

import android.os.Bundle;

import com.yalin.muzei.api.Artwork;
import com.yalin.muzei.api.UserCommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * YaLin
 * 2016/11/9.
 */
public class SourceState {
    private Artwork mCurrentArtwork;
    private String mDescription;
    private boolean mWantsNetworkAvailable;
    private final ArrayList<UserCommand> mUserCommands = new ArrayList<>();

    public Artwork getCurrentArtwork() {
        return mCurrentArtwork;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean getWantsNetworkAvailable() {
        return mWantsNetworkAvailable;
    }

    public int getNumUserCommands() {
        return mUserCommands.size();
    }

    public UserCommand getUserCommandAt(int index) {
        return mUserCommands.get(index);
    }

    public void setCurrentArtwork(Artwork artwork) {
        mCurrentArtwork = artwork;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setWantsNetworkAvailable(boolean wantsNetworkAvailable) {
        mWantsNetworkAvailable = wantsNetworkAvailable;
    }

    public void setUserCommands(int... userCommands) {
        mUserCommands.clear();
        if (userCommands != null) {
            mUserCommands.ensureCapacity(userCommands.length);
            for (int command : userCommands) {
                mUserCommands.add(new UserCommand(command));
            }
        }
    }

    public void setUserCommands(UserCommand... userCommands) {
        mUserCommands.clear();
        if (userCommands != null) {
            mUserCommands.ensureCapacity(userCommands.length);
            Collections.addAll(mUserCommands, userCommands);
        }
    }

    public void setUserCommands(List<UserCommand> userCommands) {
        mUserCommands.clear();
        if (userCommands != null) {
            mUserCommands.addAll(userCommands);
        }
    }

    public static final String KEY_CURRENT_ARTWORK = "currentArtwork";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_WANTS_NETWORK_AVAILABLE = "wantsNetworkAvailable";
    public static final String KEY_USER_COMMANDS = "userCommands";

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        if (mCurrentArtwork != null) {
            bundle.putBundle(KEY_CURRENT_ARTWORK, mCurrentArtwork.toBundle());
        }
        bundle.putString(KEY_DESCRIPTION, mDescription);
        bundle.putBoolean(KEY_WANTS_NETWORK_AVAILABLE, mWantsNetworkAvailable);
        String[] commandsSerialized = new String[mUserCommands.size()];
        for (int i = 0; i < commandsSerialized.length; i++) {
            commandsSerialized[i] = mUserCommands.get(i).serialize();
        }
        bundle.putStringArray(KEY_USER_COMMANDS, commandsSerialized);
        return bundle;
    }

    public static SourceState fromBundle(Bundle bundle) {
        SourceState state = new SourceState();
        Bundle artworkBundle = bundle.getBundle(KEY_CURRENT_ARTWORK);
        if (artworkBundle != null) {
            state.mCurrentArtwork = Artwork.fromBundle(artworkBundle);
        }
        state.mDescription = bundle.getString(KEY_DESCRIPTION);
        state.mWantsNetworkAvailable = bundle.getBoolean(KEY_WANTS_NETWORK_AVAILABLE);
        String[] commandsSerialized = bundle.getStringArray(KEY_USER_COMMANDS);
        if (commandsSerialized != null && commandsSerialized.length > 0) {
            state.mUserCommands.ensureCapacity(commandsSerialized.length);
            for (String s : commandsSerialized) {
                state.mUserCommands.add(UserCommand.deserialize(s));
            }
        }
        return state;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (mCurrentArtwork != null) {
            jsonObject.put(KEY_CURRENT_ARTWORK, mCurrentArtwork.toJson());
        }
        jsonObject.put(KEY_DESCRIPTION, mDescription);
        jsonObject.put(KEY_WANTS_NETWORK_AVAILABLE, mWantsNetworkAvailable);
        JSONArray commandsSerialized = new JSONArray();
        for (UserCommand command : mUserCommands) {
            commandsSerialized.put(command.serialize());
        }
        jsonObject.put(KEY_USER_COMMANDS, commandsSerialized);
        return jsonObject;
    }

    public void readJson(JSONObject jsonObject) throws JSONException {
        JSONObject artworkJsonObject = jsonObject.optJSONObject(KEY_CURRENT_ARTWORK);
        if (artworkJsonObject != null) {
            mCurrentArtwork = Artwork.fromJson(artworkJsonObject);
        }
        mDescription = jsonObject.optString(KEY_DESCRIPTION);
        mWantsNetworkAvailable = jsonObject.optBoolean(KEY_WANTS_NETWORK_AVAILABLE);
        JSONArray commandsSerialized = jsonObject.optJSONArray(KEY_USER_COMMANDS);
        mUserCommands.clear();
        if (commandsSerialized != null && commandsSerialized.length() > 0) {
            int length = commandsSerialized.length();
            mUserCommands.ensureCapacity(length);
            for (int i = 0; i < length; i++) {
                mUserCommands.add(UserCommand.deserialize(commandsSerialized.optString(i)));
            }
        }
    }

    public static SourceState fromJson(JSONObject jsonObject) throws JSONException {
        SourceState state = new SourceState();
        state.readJson(jsonObject);
        return state;
    }
}
