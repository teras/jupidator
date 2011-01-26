/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

import com.panayotis.jupidator.data.Version;
import com.panayotis.jupidator.elements.FileUtils;
import com.panayotis.jupidator.gui.JupidatorGUI;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author teras
 */
public class DeployerParameters implements Serializable {

    ArrayList<XElement> elements = new ArrayList<XElement>();

    public void setElements(Version vers) {
        for (String key : vers.keySet())
            elements.add(vers.get(key).getExecElement());
    }

    public void setGUI(JupidatorGUI gui) {
    }

    public File storeParameters(Version vers) {
        File file = vers.getAppElements().permissionManager.getRestartObject();
        FileUtils.makeDirectory(file.getParentFile());
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(file));
            output.writeObject(elements);
        } catch (IOException ex) {
            file = null;
        } finally {
            if (output != null)
                try {
                    output.close();
                } catch (IOException ex) {
                }
        }
        return file;
    }
}
