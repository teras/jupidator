/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.list;

import com.panayotis.jupidator.file.FileElement;
import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdaterException;
import com.panayotis.jupidator.UpdaterProperties;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author teras
 */
public class Version extends HashMap<String, FileElement> {

    private UpdaterAppElements appel;
    private UpdaterProperties appprop;
    private Arch arch;

    public static Version loadVersion(String xml, ApplicationInfo appinfo) throws UpdaterException {
        try {
            UpdaterProperties prop = new UpdaterProperties(appinfo);
            if (prop.isTooSoon())
                return new Version();
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            UpdaterXMLHandler handler = new UpdaterXMLHandler(appinfo);
            parser.parse(xml, handler);
            Version v = handler.getVersion();
            v.appel = handler.getAppElements();
            v.appprop = prop;
            return v;
        } catch (SAXException ex) {
            throw new UpdaterException(ex.getMessage());
        } catch (IOException ex) {
            throw new UpdaterException(ex.getMessage());
        } catch (ParserConfigurationException ex) {
            throw new UpdaterException(ex.getMessage());
        }
    }

    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("[Version").append('\n');
        for (String tag : keySet()) {
            b.append("  ");
            b.append(get(tag).toString());
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

        FileElement fother, fthis, fnew;
        for (String tag : other.keySet()) {
            fother = other.get(tag);
            fthis = get(tag);
            if (fthis == null) {
                put(tag, fother);
            } else {
                fnew = fthis.getNewestRelease(fother);
                put(tag, fnew);
            }
        }
    }

    public Arch getArch() {
        return arch;
    }

    public void setArch(Arch arch) {
        this.arch = arch;
    }
}
