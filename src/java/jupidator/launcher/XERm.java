/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

import java.io.File;
import static jupidator.launcher.JupidatorDeployer.debug;

/**
 *
 * @author teras
 */
public class XERm extends XFileModElement {

    public XERm(String target) {
        super(target);
    }

    public void perform() {
        debug("Removing file " + target);
        debug("  Deleting file " + target);
        if (!rmTree(new File(target)))
            debug("*ERROR* Unable to delete file " + target);
    }
}
