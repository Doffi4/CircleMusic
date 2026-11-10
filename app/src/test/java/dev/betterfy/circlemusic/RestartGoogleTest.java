package dev.betterfy.circlemusic;
import org.junit.Test;
import static org.junit.Assert.*;
public class RestartGoogleTest {
    @Test public void reportsWhetherTheCommandSucceeded() throws Exception {
        String java = System.getProperty("java.home") + "/bin/java";
        assertTrue(RestartGoogle.waitFor(new ProcessBuilder(java, "-version").redirectErrorStream(true).start()));
        assertFalse(RestartGoogle.waitFor(new ProcessBuilder(java, "-not-a-real-option").redirectErrorStream(true).start()));
    }
}
