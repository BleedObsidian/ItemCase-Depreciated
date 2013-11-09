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

import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gmail.bleedobsidian.itemcase.logger.PluginLogger;

/**
 * This class allows you to check for updates on dev.bukkit.
 * 
 * @author Jesse Prescott
 */
public class Update {
    private final static String PLUGIN_URL = "http://dev.bukkit.org/bukkit-plugins/areaprotect/files.rss";

    /**
     * Get latest version according to bukkit.
     * 
     * @return Latest Version
     */
    public static String getLatestVersion() {
	try {
	    URL url = new URL(PLUGIN_URL);
	    Document document = DocumentBuilderFactory.newInstance()
		    .newDocumentBuilder()
		    .parse(url.openConnection().getInputStream());
	    document.getDocumentElement().normalize();

	    NodeList nodes = document.getElementsByTagName("item");
	    Node firstNode = nodes.item(0);

	    if (firstNode.getNodeType() == 1) {
		Element firstElement = (Element) firstNode;
		NodeList firstElementTagName = firstElement
			.getElementsByTagName("title");
		Element firstNameElement = (Element) firstElementTagName
			.item(0);
		NodeList firstNodes = firstNameElement.getChildNodes();

		return firstNodes.item(0).getNodeValue()
			.replace("Showcase V", "");
	    } else {
		throw new Exception("Failed to get first node.");
	    }
	} catch (Exception e) {
	    PluginLogger.warning("Failed to check for update. "
		    + e.getMessage());
	    return null;
	}
    }

    /**
     * Check if a new version is available.
     * 
     * @param plugin
     *            - JavaPlugin.
     * @return If new version is available.
     */
    public static boolean isNewVersionAvailable(JavaPlugin plugin) {
	String currentVersion = plugin.getDescription().getVersion();
	String version = Update.getLatestVersion();

	return currentVersion.equalsIgnoreCase(version) ? false : true;
    }
}
