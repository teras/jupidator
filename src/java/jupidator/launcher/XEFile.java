/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

import static jupidator.launcher.JupidatorDeployer.debug;
import static jupidator.launcher.JupidatorDeployer.EXTENSION;

import java.io.File;

/**
 *
 * @author teras
 */
public class XEFile extends XFileModElement {

    public XEFile(String target) {
        super(target);
    }

    // TODO : handle pachages and files in other locations
    public void perform() {
        debug("Installing file " + target);
        String oldpath = target.substring(0, target.length() - EXTENSION.length());
        File oldfile = new File(oldpath);
        File newfile = new File(target);

        debug("  Deleting file " + oldfile);
        if (!rmTree(oldfile))
            debug("  *ERROR* Unable to remove old file " + oldpath);
        debug("  Renaming " + target + " to " + oldfile);
        newfile.renameTo(oldfile);
    }
}
