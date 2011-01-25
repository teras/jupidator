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
public abstract class XFileModElement extends XTargetElement {

    public XFileModElement(String target) {
        super(target);
    }

    protected static boolean rmTree(File f) {
        if (!f.exists())
            return true;
        if (f.isDirectory()) {
            File dir[] = f.listFiles();
            for (int i = 0; i < dir.length; i++)
                if (!rmTree(dir[i]))
                    return false;
        }
        return f.delete();
    }
}
