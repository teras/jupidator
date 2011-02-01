/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements.mirror;


import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.data.TextUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 *
 * @author teras
 */
public class Mirror {

    private static final String COREURL = "URL";
    //
    private final String parser;
    private final ApplicationInfo appinfo;
    private final String url;

    public Mirror(ApplicationInfo appinfo, String URL) {
        this("${URL}/${FILEPATH}/${FILENAME}${FILECOMPR}", appinfo, URL);
    }

    public Mirror(String parser, ApplicationInfo appinfo, String URL) {
        this.parser = parser;
        this.appinfo = appinfo;
        this.url = URL;
    }

    public URL getURL(Map<String, String> elements) throws MalformedURLException {
        try {
            elements.put(COREURL, url);
            String request = appinfo.applyVariables(TextUtils.applyVariables(elements, parser));
            return new URL(request);
        } finally {
            elements.remove(COREURL);
        }
    }
}
