/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.data;

import com.panayotis.jupidator.file.FileAdd;
import com.panayotis.jupidator.file.FileRm;
import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.file.FileChmod;
import com.panayotis.jupidator.file.FileChown;
import com.panayotis.jupidator.file.FileExec;
import com.panayotis.jupidator.file.FileWait;
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
    private boolean ignore_version; // true, if this version is too old and should be ignored
    private StringBuffer descbuffer;    // Temporary buffer to store descriptions
    private ApplicationInfo appinfo;    // Remember information about the current running application
    private FileExec lastSeenExecElement = null;    // Use this trick to store arguments in an exec element, instead of launcher. If it is null, they are stored in the launcher.

    public UpdaterXMLHandler(ApplicationInfo appinfo) { // We are interested only for version "current_version" onwards
        elements = new UpdaterAppElements();
        ignore_version = false;
        this.appinfo = appinfo;
        arch = new Arch("any", "", ""); // Default arch is selected by default
    }

    public void startElement(String uri, String localName, String qName, Attributes attr) {
        if (qName.equals("architect")) {
            lastarch = new Arch(attr.getValue("tag"), attr.getValue("os"), attr.getValue("arch"));
            if (lastarch.isCurrent())
                arch = lastarch;
        } else if (qName.equals("launcher")) {
            lastarch.setExec(attr.getValue("exec"));
        } else if (qName.equals("argument")) {
            if (lastSeenExecElement == null)
                lastarch.addArgument(attr.getValue("value"), appinfo);
            else
                lastSeenExecElement.addArgument(attr.getValue("value"), appinfo);
        } else if (qName.equals("version")) {
            int release_last = 0;
            try {
                release_last = Integer.parseInt(attr.getValue("release"));
            } catch (NumberFormatException ex) {
            }
            String version_last = attr.getValue("version");
            elements.updateVersion(release_last, version_last);
            ignore_version = (appinfo == null) ? false : release_last <= appinfo.getRelease();
        } else if (qName.equals("description")) {
            descbuffer = new StringBuffer();
        } else if (qName.equals("arch")) {
            if (ignore_version)
                return;
            if (arch.isTag(attr.getValue("name"))) {  // Found current architecture
                current = new Version();
            }
        } else if (qName.equals("file")) {
            if (shouldIgnore(attr.getValue("forceinstall")))
                return;
            FileAdd f = new FileAdd(attr.getValue("name"), attr.getValue("sourcedir"),
                    attr.getValue("destdir"), attr.getValue("size"),
                    attr.getValue("compress"), elements, appinfo);
            if (TextUtils.isTrue(attr.getValue("ifexists")) && (!f.exists()))
                return;
            current.put(f);
        } else if (qName.equals("rm")) {
            if (shouldIgnore(attr.getValue("forceinstall")))
                return;
            current.put(new FileRm(attr.getValue("file"), elements, appinfo));
        } else if (qName.equals("chmod")) {
            if (shouldIgnore(attr.getValue("forceinstall")))
                return;
            current.put(new FileChmod(attr.getValue("file"), attr.getValue("attr"),
                    attr.getValue("recursive"), elements, appinfo));
        } else if (qName.equals("chown")) {
            if (shouldIgnore(attr.getValue("forceinstall")))
                return;
            current.put(new FileChown(attr.getValue("file"), attr.getValue("attr"),
                    attr.getValue("recursive"), elements, appinfo));
        } else if (qName.equals("exec")) {
            if (shouldIgnore(attr.getValue("forceinstall")))
                return;
            lastSeenExecElement = new FileExec(attr.getValue("executable"), attr.getValue("input"), elements, appinfo);
            current.put(lastSeenExecElement);
        } else if (qName.equals("gui")) {
            if (shouldIgnore(null))
                return;
            appinfo.setGraphicalDeployer(true);
        } else if (qName.equals("wait")) {
            if (shouldIgnore(null))
                return;
            current.put(new FileWait(attr.getValue("msecs"), elements, appinfo));
        } else if (qName.equals("updatelist")) {
            elements.setBaseURL(attr.getValue("baseurl"));
            elements.setAppName(attr.getValue("application"));
            elements.setIconpath(attr.getValue("icon"));
        }
    }

    private boolean shouldIgnore(String force) {
        if (ignore_version)
            return true;
        if (current == null)
            return true;
        if (appinfo == null)
            return true;
        if (!appinfo.isDistributionBased())
            return false;
        return !TextUtils.isTrue(force);
    }

    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("arch")) {
            if (latest == null) {
                latest = current;
            } else {
                latest.merge(current);
            }
            current = null;
        } else if (qName.equals("version")) {
            ignore_version = false;
        } else if (qName.equals("description")) {
            if (ignore_version)
                return;
            elements.addLogItem(elements.getLastVersion(), descbuffer.toString());
        } else if (qName.equals("exec")) {
            lastSeenExecElement = null; // Forget it, we don't need it any more
        }
    }

//    public void endDocument() {
//    }
    public void characters(char[] ch, int start, int length) {
        if (ignore_version)
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
