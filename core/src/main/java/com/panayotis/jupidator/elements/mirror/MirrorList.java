/*
 *
 * This file is part of Jupidator.
 *
 * Jupidator is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 *
 * Jupidator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jupidator; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package com.panayotis.jupidator.elements.mirror;

import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.elements.FileUtils;
import com.panayotis.jupidator.gui.BufferListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.panayotis.jupidator.i18n.I18N._t;

public class MirrorList {

    private final ArrayList<Mirror> mirrors = new ArrayList<Mirror>();

    public String downloadFile(MirroredFile file, File download_location, BufferListener watcher, UpdatedApplication app) {
        String reason = "";
        for (Mirror mirror : mirrors) {
            watcher.freezeSize();

            /* Create URL */
            URL url = null;
            try {
                url = mirror.getURL(file.getElements());
            } catch (MalformedURLException e) {
                continue;
            }
            app.receiveMessage(_t("Request URL {0}", url.toString()));
            /* Download file */
            String status = download(url, download_location, watcher);
            /* Check download status */
            if (status != null)
                reason = status;
            else if (file.getRemoteSize() >= 0 && download_location.length() != file.getRemoteSize())
                reason = "Wrong size, required " + file.getRemoteSize() + ", found " + download_location.length();
            else if (file.shouldFetchFile(download_location))
                reason = "Security match failed";
            else {
                app.receiveMessage(_t("File {0} sucessfully downloaded", download_location.getAbsolutePath()));
                return null;
            }
            watcher.rollbackSize();
        }
        return _t("Unable to download file " + file.getFile() + " : " + reason);
    }

    public void addMirror(Mirror mirror) {
        int location = mirrors.size() - 1;
        if (location < 0)
            location = 0;
        mirrors.add(location, mirror);
    }

    private static String download(URL from, File to, BufferListener watcher) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) from.openConnection();
            connection.setUseCaches(true);
            connection.connect();
//            System.out.println(connection.getResponseCode());
            return FileUtils.copyFile(from.openStream(), new FileOutputStream(to), watcher, true);
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }
}
