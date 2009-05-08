/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.loglist.creators;

import com.panayotis.jupidator.loglist.LogItem;
import com.panayotis.jupidator.loglist.LogList;
import static com.panayotis.jupidator.i18n.I18N._;

/**
 *
 * @author teras
 */
public class HTMLCreator {

    public static String getList(LogList list) {
        StringBuffer data;
        data = new StringBuffer();
        data.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
        data.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n");
        data.append("<head>\n");
        data.append("<title></title>\n");
        data.append("<style type=\"text/css\">\n");
        data.append(".version { padding:4px 4px 4px 4px; margin: 5px 10px 5px 10px; background: #d2e6d2; font-weight: bold; }\n");
        data.append(".info { margin: 0px 10px 16px 12px; }\n");
        data.append("</style>\n");
        data.append("</head>\n");
        data.append("<body>\n");

        data.append("<div class=\"releaseinfo\">");
        data.append(list.getReleaseInfo());
        data.append("</div>\n");
        
        for (LogItem item : list) {
            data.append("<div class=\"version\">");
            data.append(_("Version"));
            data.append(": ").append(item.getVersion());
            data.append("</div>\n<div class=\"info\">");
            data.append(item.getInfo());
            data.append("</div>\n");
        }

        data.append("</body>\n</html>\n");
        return data.toString();
    }
}
