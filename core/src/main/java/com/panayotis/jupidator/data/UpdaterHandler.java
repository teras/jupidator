package com.panayotis.jupidator.data;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdaterException;

public interface UpdaterHandler {

    void populate(String data, Version prop, ApplicationInfo appinfo) throws UpdaterException;
}
