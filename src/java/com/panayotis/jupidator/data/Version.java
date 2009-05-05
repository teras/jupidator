/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.data;

import com.panayotis.jupidator.elements.JupidatorElement;
import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdaterException;
import com.panayotis.jupidator.elements.ExecutionTime;
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

    private LinkedHashMap<String, JupidatorElement> elements = new LinkedHashMap<String, JupidatorElement>();
    private UpdaterAppElements appel;
    private UpdaterProperties appprop;
    private Arch arch;
    private boolean graphical_gui;

    boolean list_from_any_tag = false;  // This is used by arch to distinguish versions which were produced from "any" tags

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

        JupidatorElement fother, fthis, fnew;
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

    private void sort() {
        JupidatorElement element;
        ExecutionTime time;
        LinkedHashMap<String, JupidatorElement> before = new LinkedHashMap<String, JupidatorElement>();
        LinkedHashMap<String, JupidatorElement> mid = new LinkedHashMap<String, JupidatorElement>();
        LinkedHashMap<String, JupidatorElement> after = new LinkedHashMap<String, JupidatorElement>();
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
        elements = new LinkedHashMap<String, JupidatorElement>();
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

    public JupidatorElement get(String key) {
        return elements.get(key);
    }

    public void put(JupidatorElement element) {
        elements.put(element.getHash(), element);
    }
}
