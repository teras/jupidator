/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.loglist.creators;

import com.panayotis.jupidator.loglist.LogItem;
import com.panayotis.jupidator.loglist.LogList;

import static com.panayotis.jupidator.i18n.I18N._;
import static com.panayotis.jupidator.data.TextUtils.NL;

/**
 *
 * @author teras
 */
public class TextCreator {

    public static String getList(LogList list) {
        StringBuffer data;
        data = new StringBuffer();
        data.append(list.getReleaseInfo()).append(NL);
        data.append(_("List of changes:")).append(NL);
        for (LogItem item : list) {
            data.append(" * ");
            data.append(item.getVersion());
            data.append(" * ").append(NL);
            data.append(item.getInfo());
            data.append(NL);
        }
        return data.toString();
    }
}
