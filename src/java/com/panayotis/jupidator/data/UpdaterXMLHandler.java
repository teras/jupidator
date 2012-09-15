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

package com.panayotis.jupidator.data;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.elements.ElementChmod;
import com.panayotis.jupidator.elements.ElementChown;
import com.panayotis.jupidator.elements.ElementExec;
import com.panayotis.jupidator.elements.ElementFile;
import com.panayotis.jupidator.elements.ElementKill;
import com.panayotis.jupidator.elements.ElementRm;
import com.panayotis.jupidator.elements.ElementWait;
import com.panayotis.jupidator.elements.mirror.Mirror;
import com.panayotis.jupidator.elements.security.Digester;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author teras
 */
public class UpdaterXMLHandler extends DefaultHandler {

    private UpdaterAppElements elements; // Location to store various application elements, needed in GUI
    private Arch arch;  // The stored architecture of the running system - null if unknown
    private Arch lastarch; // The last loaded arch - used to set additional parameters to this architecture
    private Version latest; // The full aggregated list of the latest files, in order to upgrade
    private Version current;    // The list of files for the current reading "version" object
    private Version current_exact; // Version of "current" made with exact arch match
    private Version current_any;   // Version of "current" made with "any" arch match
    private Version current_all;   // Version of "current" made with "all" arch match
    private boolean old_version; // true, if this version is too old and should be ignored
    private boolean visible_version; // true, if this version should be displayed to the user
    private StringBuilder descbuffer;    // Temporary buffer to store descriptions
    private ApplicationInfo appinfo;    // Remember information about the current running application
    private ElementExec lastSeenExecElement = null;    // Use this trick to store arguments in an exec element, instead of launcher. If it is null, they are stored in the launcher.
    private ElementFile lastFileElement = null;   // Remember last Add element, to add digesters later on

    public UpdaterXMLHandler(ApplicationInfo appinfo) { // We are interested only for version "current_version" onwards
        elements = new UpdaterAppElements();
        old_version = false;
        this.appinfo = appinfo;
        arch = new Arch(); // "Any" arch is selected by default
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attr) {
        if (qName.equals("architect")) {
            lastarch = arch.getArchitect(attr.getValue("tag"), attr.getValue("os"), attr.getValue("arch"));
            if (lastarch != null)
                arch = lastarch;
        } else if (qName.equals("launcher")) {
            if (lastarch != null)
                lastarch.setExec(attr.getValue("exec"), appinfo);
        } else if (qName.equals("argument"))
            if (lastSeenExecElement == null) {
                if (lastarch != null)
                    lastarch.addArgument(attr.getValue("value"), appinfo);
            } else
                lastSeenExecElement.addArgument(attr.getValue("value"), appinfo);
        else if (qName.equals("version")) {
            int release_last = TextUtils.getInt(attr.getValue("release"), 0);
            String version_last = attr.getValue("version");
            elements.updateVersion(release_last, version_last);
            old_version = (appinfo == null) ? false : release_last <= appinfo.getRelease();
            visible_version = (appinfo == null) ? true : release_last > appinfo.getIgnoreRelease();
        } else if (qName.equals("description"))
            descbuffer = new StringBuilder();
        else if (qName.equals("arch"))
            current = getNewVersion(attr.getValue("name"), attr.getValue("gui"));
        else if (qName.equals("file")) {
            if (shouldIgnore(attr.getValue("forceinstall")))
                return;
            lastFileElement = new ElementFile(attr.getValue("name"), attr.getValue("sourcedir"),
                    attr.getValue("destdir"), attr.getValue("size"),
                    attr.getValue("compress"), elements, appinfo);
            if (TextUtils.isTrue(attr.getValue("ifexists")) && (!lastFileElement.exists())) {
                lastFileElement = null;
                return;
            }
            current.put(lastFileElement);
        } else if (qName.equals("rm")) {
            if (shouldIgnore(attr.getValue("forceinstall")))
                return;
            current.put(new ElementRm(attr.getValue("file"), elements, appinfo));
        } else if (qName.equals("chmod")) {
            if (shouldIgnore(attr.getValue("forceinstall")))
                return;
            current.put(new ElementChmod(attr.getValue("file"), attr.getValue("attr"),
                    attr.getValue("recursive"), elements, appinfo));
        } else if (qName.equals("chown")) {
            if (shouldIgnore(attr.getValue("forceinstall")))
                return;
            current.put(new ElementChown(attr.getValue("file"), attr.getValue("attr"),
                    attr.getValue("recursive"), elements, appinfo));
        } else if (qName.equals("exec")) {
            if (shouldIgnore(attr.getValue("forceinstall")))
                return;
            lastSeenExecElement = new ElementExec(attr.getValue("executable"), attr.getValue("input"), attr.getValue("time"), elements, appinfo);
            current.put(lastSeenExecElement);
        } else if (qName.equals("wait")) {
            if (shouldIgnore(attr.getValue("forceinstall")))
                return;
            current.put(new ElementWait(attr.getValue("msecs"), attr.getValue("time"), elements, appinfo));
        } else if (qName.equals("kill")) {
            if (shouldIgnore(attr.getValue("forceinstall")))
                return;
            current.put(new ElementKill(attr.getValue("process"), attr.getValue("signal"), elements, appinfo));
        } else if (qName.equals("md5")) {
            if (lastFileElement == null)
                return;
            Digester d = Digester.getDigester("MD5");
            d.setHash(attr.getValue("value"));
            lastFileElement.addDigester(d);
        } else if (qName.equals("sha1")) {
            if (lastFileElement == null)
                return;
            Digester d = Digester.getDigester("SHA1");
            d.setHash(attr.getValue("value"));
            lastFileElement.addDigester(d);
        } else if (qName.equals("sha2")) {
            if (lastFileElement == null)
                return;
            String type = attr.getValue("type");
            if (type == null)
                type = "256";
            Digester d = Digester.getDigester("SHA-" + attr.getValue(type));
            d.setHash(attr.getValue("value"));
            lastFileElement.addDigester(d);
        } else if (qName.equals("mirror"))
            elements.getMirrors().addMirror(new Mirror(attr.getValue("constructor"), appinfo, attr.getValue("url")));
        else if (qName.equals("updatelist")) {
            String baseurl = attr.getValue("baseurl");
            elements.setBaseURL(baseurl);
            elements.getMirrors().addMirror(new Mirror(appinfo, baseurl));
            elements.setAppName(attr.getValue("application"));
            elements.setIconpath(attr.getValue("icon"));
            elements.setJupidatorVersion(attr.getValue("jupidator"));
        }
    }

    private boolean shouldIgnore(String force) {
        if (old_version)
            return true;
        if (current == null)
            return true;
        if (appinfo == null)
            return true;
        if (!appinfo.isDistributionBased())
            return false;
        return !TextUtils.isTrue(force);
    }

    private Version getNewVersion(String name, String gui) {
        if (old_version)
            return null;
        Version vers = arch.getVersion(name); // If current architecture was found, return a new Version object
        if (vers != null) { // Check if this version is valid
            vers.setGraphicalDeployer(TextUtils.isTrue(gui)); // check if GUI is required
            vers.setVisible(visible_version);    // Mark if this version should be visible or not
        }
        return vers;
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("arch")) {
            if (current == null)
                return;
            switch (current.tag_type) {
                case Version.ANYTAG:
                    current_any = current;
                    break;
                case Version.ALLTAGS:
                    current_all = current;
                    break;
                default:
                    current_exact = current;
                    break;
            }
            current = null;
        } else if (qName.equals("version")) {
            Version working = current_any;
            if (current_exact != null)
                working = current_exact;
            if (working == null)
                working = current_all;
            else
                working.merge(current_all);

            current_any = current_exact = current_all = null;
            if (working == null) // No arch tag - assuming "any"
                working = getNewVersion(null, null);
            if (working != null)
                if (latest == null) {
                    latest = working;
                    latest.tag_type = Version.UNKNOWN;
                } else
                    latest.merge(working);
            old_version = false;
        } else if (qName.equals("description")) {
            if (old_version)
                return;
            elements.addLogItem(elements.getLastVersion(), descbuffer.toString());
        } else if (qName.equals("exec"))
            lastSeenExecElement = null;
        else if (qName.equals("file"))
            lastFileElement = null;
    }

//    public void endDocument() {
//    }
    @Override
    public void characters(char[] ch, int start, int length) {
        if (old_version)
            return;
        if (descbuffer == null)
            return;
        String info = new String(ch, start, length).trim();
        if (info.equals(""))
            return;
        descbuffer.append(info);
    }

    UpdaterAppElements getAppElements() {
        return elements;
    }

    Version getVersion() {
        // Make sure that we never return null.
        // note: latest is null if no updates were found at all
        Version v = latest;
        if (v == null)
            v = new Version();
        v.setArch(arch);
        return v;
    }
}
