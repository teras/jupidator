/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

import static jupidator.launcher.JupidatorDeployer.debug;

import java.io.File;

/**
 *
 * @author teras
 */
public class XEFile extends XFileModElement {

    private final String source;

    public XEFile(String target, String source) {
        super(target);
        this.source = source;
    }

    // TODO : handle pachages and files in other locations
    public void perform() {
        File input = new File(source);
        File output = new File(target);
        if (input.isDirectory()) {
            debug("Installing package " + target);
            for (File entry : input.listFiles())
                if (!safeCopy(entry, output))
                    debug("  *Error* Unable to install " + entry.getPath() + " to " + target);
        } else {
            debug("Installing file " + target);
            if (!safeCopy(input, output))
                debug("  *Error* Unable to install " + source + " to " + target);
        }
    }
}
