/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.data;

import com.panayotis.jupidator.file.FileElement;
import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdaterException;
import com.panayotis.jupidator.data.UpdaterProperties;
import com.panayotis.jupidator.file.ExecutionTime;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author teras
 */
public class Version {

    private LinkedHashMap<String, FileElement> elements = new LinkedHashMap<String, FileElement>();
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
            v.sort();
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

        FileElement fother, fthis, fnew;
        for (String tag : other.elements.keySet()) {
            fother = other.elements.get(tag);
            fthis = elements.get(tag);
            if (fthis == null) {
                elements.put(tag, fother);
            } else {
                fnew = fthis.getNewestRelease(fother);
                elements.put(tag, fnew);
            }
        }
    }

    public Arch getArch() {
        return arch;
    }

    public void setArch(Arch arch) {
        this.arch = arch;
    }

    private void sort() {
        FileElement element;
        ExecutionTime time;
        LinkedHashMap<String, FileElement> before = new LinkedHashMap<String, FileElement>();
        LinkedHashMap<String, FileElement> mid = new LinkedHashMap<String, FileElement>();
        LinkedHashMap<String, FileElement> after = new LinkedHashMap<String, FileElement>();
        for (String key : elements.keySet()) {
            element = elements.get(key);
            time = element.getExectime();
            if (time.equals(ExecutionTime.BEFORE))
                before.put(key, element);
            else if (time.equals(ExecutionTime.AFTER))
                after.put(key, element);
            else
                mid.put(key, element);
        }
        elements = new LinkedHashMap<String, FileElement>();
        elements.putAll(before);
        elements.putAll(mid);
        elements.putAll(after);
    }

    public int size() {
        return elements.size();
    }

    public Set<String> keySet() {
        return elements.keySet();
    }

    public FileElement get(String key) {
        return elements.get(key);
    }

    public void put(String key, FileElement element) {
        elements.put(key, element);
    }
}
