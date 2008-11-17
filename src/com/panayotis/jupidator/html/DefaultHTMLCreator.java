/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.html;
import static com.panayotis.jupidator.i18n.I18N._;

/**
 *
 * @author teras
 */
public class DefaultHTMLCreator implements UpdaterHTMLCreator {

    private StringBuffer data;

    public DefaultHTMLCreator() {
        data = new StringBuffer();
        data.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
        data.append("<html>\n");
        data.append("<head>\n");
        data.append("<title></title>\n");
        data.append("<style type=\"text/css\">\n");
        data.append(".version { padding:4px 4px 4px 4px; margin: 5px 10px 5px 10px; background: #d2e6d2; font-weight: bold; }\n");
        data.append(".info { margin: 0px 10px 16px 12px; }\n");
        data.append("</style>\n");
        data.append("</head>\n");
        data.append("<body>\n");
    }

    public void addInfo(String lastrelease, String information) {
        data.append("<div class=\"version\">");
        data.append(_("Version"));
        data.append(": ").append(lastrelease);
        data.append("</div>\n<div class=\"info\">");
        data.append(information);
        data.append("</div>\n");
    }

    public String getHTML() {
        return data.toString() + "</body>\n</html>\n";
    }
    
}
