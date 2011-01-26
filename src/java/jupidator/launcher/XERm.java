/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

import java.io.File;

/**
 *
 * @author teras
 */
public class XERm extends XFileModElement {

    public XERm(String target) {
        super(target);
    }

    public void perform() {
        Debug.info("Removing file " + target);
        if (!safeDelete(new File(target)))
            Debug.error("  Unable to delete file " + target);
    }
}
