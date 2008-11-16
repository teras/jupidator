/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.list;

import com.panayotis.jupidator.file.FileAdd;
import com.panayotis.jupidator.file.FileRm;
import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.html.UpdaterHTMLCreator;
import com.panayotis.jupidator.html.DefaultHTMLCreator;
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
    private UpdaterHTMLCreator display; // Store version updated
    private ApplicationInfo appinfo;    // Remember information about the current running application

    public UpdaterXMLHandler(ApplicationInfo appinfo) { // We are interested only for version "current_version" onwards
        elements = new UpdaterAppElements();
        ignore_version = false;
        display = new DefaultHTMLCreator();
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
            lastarch.addArgument(attr.getValue("value"));
        } else if (qName.equals("version")) {
            int release_last = Integer.parseInt(attr.getValue("id"));
            String version_last = attr.getValue("release");
            elements.updateVersion(release_last, version_last);
            ignore_version = release_last <= appinfo.getRelease();
        } else if (qName.equals("description")) {
            descbuffer = new StringBuffer();
        } else if (qName.equals("arch")) {
            if (ignore_version)
                return;
            if (arch.isTag(attr.getValue("name"))) {  // Found current architecture
                appinfo.setBaseFile(attr.getValue("basefile"));
                current = new Version();
            }
        } else if (qName.equals("file")) {
            if (shouldIgnore(attr.getValue("forceinstall")))
                return;
            FileAdd f = new FileAdd(attr.getValue("name"), attr.getValue("sourcedir"), attr.getValue("destdir"), elements, appinfo);
            current.put(f.getHash(), f);
        } else if (qName.equals("rm")) {
            if (shouldIgnore(attr.getValue("forceinstall")))
                return;
            FileRm f = new FileRm(attr.getValue("name"), attr.getValue("destdir"), elements, appinfo);
            current.put(f.getHash(), f);
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

        if (!appinfo.isDistributionBased())
            return false;
        if (force != null) {
            force = force.toLowerCase().trim();
            if (force.equals("true") || force.equals("yes") || force.equals("1"))
                return false;
        }
        return true;
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
            display.addInfo(elements.getLastVersion(), descbuffer.toString());
        }
    }

    public void endDocument() {
        elements.setHTML(display.getHTML());
    }

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
