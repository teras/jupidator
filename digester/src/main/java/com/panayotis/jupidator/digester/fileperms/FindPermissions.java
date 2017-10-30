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
package com.panayotis.jupidator.digester.fileperms;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import static java.nio.file.attribute.PosixFilePermission.*;

/**
 *
 * @author teras
 */
public class FindPermissions {

    public static final String getPerms(File file) {
        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(file.toPath(), LinkOption.NOFOLLOW_LINKS);
            char owner = (char) ('0' + (perms.contains(OWNER_READ) ? 4 : 0) + (perms.contains(OWNER_WRITE) ? 2 : 0) + (perms.contains(OWNER_EXECUTE) ? 1 : 0));
            char group = (char) ('0' + (perms.contains(GROUP_READ) ? 4 : 0) + (perms.contains(GROUP_WRITE) ? 2 : 0) + (perms.contains(GROUP_EXECUTE) ? 1 : 0));
            char others = (char) ('0' + (perms.contains(OTHERS_READ) ? 4 : 0) + (perms.contains(OTHERS_WRITE) ? 2 : 0) + (perms.contains(OTHERS_EXECUTE) ? 1 : 0));
            return owner + "" + group + "" + others;
        } catch (Exception ex) {
            return null;
        }
    }
}
