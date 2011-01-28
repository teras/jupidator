/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

import com.panayotis.jupidator.elements.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author teras
 */
public class DeployerParameters implements Serializable {

    private List<XElement> elements = new ArrayList<XElement>();
    private List<String> relaunch = new ArrayList<String>();
    private boolean headless = true;

    public void setElements(List<XElement> elements) {
        this.elements = elements;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public void setRelaunchCommand(List<String> relaunch) {
        if (relaunch == null)
            relaunch = new ArrayList<String>();
        this.relaunch = relaunch;
    }

    public List<XElement> getElements() {
        return elements;
    }

    public boolean isHeadless() {
        return headless;
    }

    public List<String> getRelaunchCommand() {
        return relaunch;
    }

    public boolean storeParameters(File out) {
        if (out == null)
            return false;
        FileUtils.makeDirectory(out.getParentFile());
        ObjectOutputStream output = null;
        boolean status = false;
        try {
            output = new ObjectOutputStream(new FileOutputStream(out));
            output.writeObject(this);
            status = true;
        } catch (IOException ex) {
        } finally {
            if (output != null)
                try {
                    output.close();
                } catch (IOException ex) {
                    status = false;
                }
        }
        return status;
    }
}
