/*
 * Copyright (C) 2013 Jesse Prescott <BleedObsidian@gmail.com>
 *
 * ItemCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 */
package com.gmail.bleedobsidian.itemcase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This class allows you to access the ServerMods API to retrieve the latest
 * game version and downloads. (Only used internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class Updater {

    /**
     * URL to connect to.
     */
    private URL url;

    /**
     * Base API URL.
     */
    private final String URL_HOST = "https://api.curseforge.com/";

    /**
     * Query.
     */
    private final String URL_QUERY = "servermods/files?projectIds=";

    /**
     * Plugin instance.
     */
    private final Plugin plugin;

    /**
     * API Key.
     */
    private final String apiKey;

    /**
     * Thread.
     */
    private final Thread thread;

    /**
     * UpdateResult.
     */
    private UpdateResult result;

    /**
     * Version type.
     */
    private String versionType;

    /**
     * Version game version.
     */
    private String versionGameVersion;

    /**
     * Version name.
     */
    private String versionName;

    /**
     * Version link.
     */
    private String versionLink;

    /**
     * New Updater.
     *
     * @param plugin Plugin to reference version data against.
     * @param projectID ProjectID of plugin on Bukkit.
     */
    public Updater(Plugin plugin, int projectID) {
        this.plugin = plugin;
        this.apiKey = null;

        try {
            this.url = new URL(this.URL_HOST + this.URL_QUERY + projectID);
        } catch (MalformedURLException e) {
            this.result = UpdateResult.ERROR_ID;
            this.thread = null;
            return;
        }

        this.thread = new Thread(new UpdateRunnable());
        this.thread.start();
    }

    /**
     * New Updater with custom API key.
     *
     * @param plugin Plugin to reference version data against.
     * @param projectID ProjectID of plugin on Bukkit.
     * @param apiKey Custom API key to use for ServerMods API.
     */
    public Updater(Plugin plugin, int projectID, String apiKey) {
        this.plugin = plugin;
        this.apiKey = apiKey;

        try {
            this.url = new URL(this.URL_HOST + this.URL_QUERY + projectID);
        } catch (MalformedURLException e) {
            this.result = UpdateResult.ERROR_ID;
            this.thread = null;
            return;
        }

        this.thread = new Thread(new UpdateRunnable());
        this.thread.start();
    }

    /**
     * @return Result of the updater
     */
    public UpdateResult getResult() {
        this.waitForThread();
        return this.result;
    }

    /**
     * @return Release type of latest file.
     */
    public String getLatestType() {
        this.waitForThread();
        return this.versionType;
    }

    /**
     * @return Game version of latest file.
     */
    public String getLatestGameVersion() {
        this.waitForThread();
        return this.versionGameVersion;
    }

    /**
     * @return Name of latest file.
     */
    public String getLatestName() {
        this.waitForThread();
        return this.versionName;
    }

    /**
     * @return Download link to latest file.
     */
    public String getLatestFileLink() {
        this.waitForThread();
        return this.versionLink;
    }

    /**
     * Wait/hang for update process.
     */
    private void waitForThread() {
        if ((this.thread != null) && this.thread.isAlive()) {
            try {
                this.thread.join();
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /**
     * Query ServerMods API for project variables.
     *
     * @return If successful or not.
     */
    private boolean query() {
        try {
            final URLConnection con = this.url.openConnection();
            con.setConnectTimeout(5000);

            if (this.apiKey != null) {
                con.addRequestProperty("X-API-Key", this.apiKey);
            }

            con.addRequestProperty("User-Agent", this.plugin.getName()
                    + " Updater");

            con.setDoOutput(true);

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            final String response = reader.readLine();

            final JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.size() == 0) {
                this.result = UpdateResult.ERROR_ID;
                return false;
            }

            this.versionName = (String) ((JSONObject) array
                    .get(array.size() - 1)).get("name");
            this.versionLink = (String) ((JSONObject) array
                    .get(array.size() - 1)).get("downloadUrl");
            this.versionType = (String) ((JSONObject) array
                    .get(array.size() - 1)).get("releaseType");
            this.versionGameVersion = (String) ((JSONObject) array.get(array
                    .size() - 1)).get("gameVersion");

            return true;
        } catch (IOException e) {
            if (e.getMessage().contains("HTTP response code: 403")) {
                this.result = UpdateResult.ERROR_APIKEY;
            } else {
                this.result = UpdateResult.ERROR_SERVER;
            }

            return false;
        }

    }

    /**
     * Check for new version.
     *
     * @param title Title of latest file.
     * @return If new version is available or not.
     */
    private boolean versionCheck(String title) {
        final String version = this.plugin.getDescription().getVersion();
        final String remoteVersion = title.split(" V")[1];

        if (version.equalsIgnoreCase(remoteVersion)) {
            this.result = UpdateResult.NO_UPDATE;
            return false;
        } else {
            this.result = UpdateResult.UPDATE_AVAILABLE;
            return true;
        }
    }

    /**
     * Update thread.
     */
    public class UpdateRunnable implements Runnable {

        public void run() {
            if (Updater.this.url != null) {
                if (Updater.this.query()) {
                    Updater.this.versionCheck(Updater.this.versionName);
                }
            }
        }
    }

    /**
     * An enum contain all possible Update Results.
     */
    public enum UpdateResult {

        /**
         * An update is available.
         */
        UPDATE_AVAILABLE,
        /**
         * No update was found.
         */
        NO_UPDATE,
        /**
         * Failed to contact server.
         */
        ERROR_SERVER,
        /**
         * No version data was returned when checking for an update.
         */
        ERROR_VERSION,
        /**
         * The id provided is invalid and doesn't exist.
         */
        ERROR_ID,
        /**
         * The API key provided is invalid.
         */
        ERROR_APIKEY
    }
}
