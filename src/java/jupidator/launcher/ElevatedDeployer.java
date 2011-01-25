/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.data.Version;
import com.panayotis.jupidator.elements.FileUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author teras
 */
public class ElevatedDeployer {

    public static void main(String[] command) {
        try {
            ObjectInputStream in = new ObjectInputStream(System.in);
            Version vers = (Version) in.readObject();
            System.out.println("Result is: "
                    + doDeploy(vers, new UpdatedApplication()  {

                public boolean requestRestart() {
                    return false;
                }

                public void receiveMessage(String message) {
                    System.out.println("O " + message);
                }
            }));
        } catch (Exception ex) {
            System.out.println("E " + ex.getMessage());
        }
    }

    public static String deploy(Version vers, UpdatedApplication application) {
//        if (!vers.getAppElements().permissionManager.isRequiredPrivileges())
//            return doDeploy(vers, application);

        String[] args = {FileUtils.JAVABIN, "-cp", System.getProperty("java.class.path"), "com.panayotis.jupidator.launcher.ElevatedDeployer"};
        Commander com = new Commander(args);
        com.setOutListener(new Closure<String>()  {

            public void exec(String data) {
                System.out.println(data);
            }
        });
        com.exec();
        com.sendData(getObjectAsBytes(vers));
        com.waitFor();
        return null;

    }

    private static String doDeploy(Version vers, UpdatedApplication application) {
        for (String key : vers.keySet()) {
            String result = vers.get(key).prepare(application);
            if (result != null)
                return result;
        }
        return null;
    }

    private static byte[] getObjectAsBytes(Object o) {
        byte[] result = null;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(bytes);
            stream.writeObject(o);
            result = bytes.toByteArray();
        } catch (IOException ex) {
        }
        return result;
    }
}
