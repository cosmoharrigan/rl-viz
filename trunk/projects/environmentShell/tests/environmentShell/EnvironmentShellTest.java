/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package environmentShell;
import org.junit.Test;
import static org.junit.Assert.*;
import rlVizLib.rlVizCore;
import environmentShell.EnvironmentShell;
/**
 *
 * @author btanner
 */
public class EnvironmentShellTest {

    @Test
    public void testEnvShellVersion(){
       assertEquals(rlVizCore.getSpecVersion(),rlVizLib.rlVizCore.getRLVizLinkVersionOfClass(EnvironmentShell.class));
    }
}