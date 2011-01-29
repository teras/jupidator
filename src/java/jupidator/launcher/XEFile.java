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
public class XEFile extends XFileModElement {

    private final String source;

    public XEFile(String source, String target) {
        super(target);
        this.source = source;
    }

    public void perform() {
        File input = new File(source);
        File output = new File(target);
        if (input.isDirectory()) {
            Visuals.info("Installing package " + target);
            for (File entry : input.listFiles())
                if (!safeMv(entry, output))
                    Visuals.error("Unable to install " + entry.getPath() + " to " + target);
            input.delete();
        } else {
            Visuals.info("Installing file " + target);
            if (!safeMv(input, output))
                Visuals.error("Unable to install " + source + " to " + target);
        }
    }
}
