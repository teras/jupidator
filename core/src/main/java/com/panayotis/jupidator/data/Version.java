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
import com.panayotis.jupidator.UpdaterException;
import com.panayotis.jupidator.elements.ElementFile;
import com.panayotis.jupidator.elements.ElementRm;
import com.panayotis.jupidator.elements.FileUtils;
import com.panayotis.jupidator.elements.JupidatorElement;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.xml.sax.SAXException;

public class Version implements Serializable {

    private Map<String, JupidatorElement> elements = new LinkedHashMap<String, JupidatorElement>();
    private UpdaterAppElements appel;
    private UpdaterProperties appprop;
    private Arch arch = Arch.defaultArch();
    private boolean graphical_gui;
    private boolean asSnapshot;

    public static Version loadVersion(String xmlurl, ApplicationInfo appinfo) throws UpdaterException {
        return loadVersion(xmlurl, appinfo, true);
    }

    public static Version loadVersion(String xmlurl, ApplicationInfo appinfo, boolean tryBzFirst) throws UpdaterException {
        return loadVersion(xmlurl, appinfo, tryBzFirst, false);
    }

    public static Version loadVersion(String xmlurl, ApplicationInfo appinfo, boolean tryBzFirst, boolean ignorePostpone) throws UpdaterException {
        InputStream is = null;
        try {
            UpdaterProperties prop = new UpdaterProperties(appinfo, ignorePostpone);
            if (!ignorePostpone && prop.isTooSoon()) {
                Version v = new Version();
                v.appel = new UpdaterAppElements();
                return v;
            }
            is = initInputStream(xmlurl);
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            UpdaterXMLHandler handler = new UpdaterXMLHandler(appinfo);
            parser.parse(is, handler);
            Version v = handler.getVersion();
            v.appel = handler.getAppElements();
            v.appprop = prop;
            v.update(appinfo.getApplicationHome());
            v.sort();
            return v;
        } catch (SAXException ex) {
            throw new UpdaterException(ex.getMessage());
        } catch (IOException ex) {
            throw new UpdaterException(ex.getClass().getName() + ": " + ex.getMessage());
        } catch (ParserConfigurationException ex) {
            throw new UpdaterException(ex.getMessage());
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException ex) {
                }
        }
    }

    public void replaceArch(Arch arch) {
        if (arch != null)
            this.arch = arch;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("[Version").append('\n');
        for (String tag : elements.keySet()) {
            b.append("  ");
            b.append(elements.get(tag).toString());
            b.append('\n');
        }
        b.append("]");
        return b.toString();
    }

    public UpdaterAppElements getAppElements() {
        return appel;
    }

    public UpdaterProperties getUpdaterProperties() {
        return appprop;
    }

    void merge(Version other) {
        if (other == null)
            return;

        if (other.asSnapshot) {
            this.asSnapshot = true;
            elements.clear();
        }

        JupidatorElement fother, fthis, fnew;
        for (String tag : other.elements.keySet()) {
            fother = other.elements.get(tag);
            fthis = elements.get(tag);
            if (fthis == null)
                elements.put(tag, fother);
            else {
                fnew = fthis.getNewestRelease(fother);
                elements.put(tag, fnew);
            }
        }
        graphical_gui |= other.graphical_gui;
    }

    public Arch getArch() {
        return arch;
    }

    public void setArch(Arch arch) {
        this.arch = arch;
    }

    void setGraphicalDeployer(boolean graphical_gui) {
        this.graphical_gui = graphical_gui;
    }

    public boolean isGraphicalDeployer() {
        return graphical_gui;
    }

    public void setAsSnapshot() {
        this.asSnapshot = true;
    }

    private void sort() {
        JupidatorElement element;
        LinkedHashMap<String, JupidatorElement> before = new LinkedHashMap<String, JupidatorElement>();
        LinkedHashMap<String, JupidatorElement> mid = new LinkedHashMap<String, JupidatorElement>();
        LinkedHashMap<String, JupidatorElement> after = new LinkedHashMap<String, JupidatorElement>();
        for (String key : elements.keySet()) {
            element = elements.get(key);
            switch (element.getExectime()) {
                case BEFORE:
                    before.put(key, element);
                    break;
                case AFTER:
                    after.put(key, element);
                    break;
                default:
                    mid.put(key, element);
                    break;
            }
        }
        elements = new LinkedHashMap<String, JupidatorElement>();
        elements.putAll(before);
        elements.putAll(mid);
        elements.putAll(after);
    }

    private void update(String appHome) {
        Collection<String> currentFiles = asSnapshot ? FileUtils.collectFilenames(appHome) : null;
        Iterator<Map.Entry<String, JupidatorElement>> it = elements.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, JupidatorElement> next = it.next();
            if (currentFiles != null)
                currentFiles.remove(next.getValue().getDestinationFile());
            if (next.getValue() instanceof ElementFile) {
                if (!((ElementFile) next.getValue()).shouldUpdateFile())
                    it.remove();
                else // From now on it should be updated
                if (currentFiles != null)
                    for (String sup : ((ElementFile) next.getValue()).supportFiles())
                        currentFiles.remove(sup);
            } else if (next.getValue() instanceof ElementRm)
                if (!new File(next.getValue().getDestinationFile()).exists())
                    it.remove();
        }
        if (currentFiles != null)
            for (String toRemove : currentFiles)
                put(new ElementRm(new File(toRemove), appel));
    }

    public Iterable<JupidatorElement> values() {
        return elements.values();
    }

    public void put(JupidatorElement element) {
        elements.put(element.getHash(), element);
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    private static InputStream initInputStream(String xmlurl) throws UpdaterException {
        String safeURL = xmlurl.toLowerCase();
        boolean isBz = false;
        if (safeURL.endsWith(".bz2") || safeURL.endsWith(".bzip2") || safeURL.endsWith(".bz"))
            isBz = true;
        else {
            InputStream is = null;
            try {
                is = new URL(xmlurl + ".bz2").openStream();
                if (is.read() == 'B' && is.read() == 'Z') {
                    xmlurl += ".bz2";
                    isBz = true;
                }
            } catch (IOException ex) {
            } finally {
                if (is != null)
                    try {
                        is.close();
                    } catch (IOException ex) {
                    }
            }
        }

        try {
            if (isBz) {
                InputStream is = new URL(xmlurl).openStream();
                is.read(); //B
                is.read(); //Z
                return new CBZip2InputStream(is);
            } else
                return new URL(xmlurl).openStream();
        } catch (IOException ex) {
            throw new UpdaterException("Unable to load jupidator data from URL " + xmlurl);
        }
    }

}
