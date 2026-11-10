package dev.betterfy.circlemusic;

import org.junit.Test;
import static org.junit.Assert.*;

public class GoogleHookTargetTest {
    @Test public void keepsBothVerifiedGoogleMappings() {
        GoogleHookTarget current = GoogleHookTarget.forVersion("17.70.11.ve.arm64");
        assertEquals("fztx", current.launcher);
        assertEquals("dupd", current.recognition);
        GoogleHookTarget previous = GoogleHookTarget.forVersion("17.73.15");
        assertEquals("gcxw", previous.launcher);
        assertEquals("dwzy", previous.recognition);
    }

    @Test public void unknownBuildsMustKeepGoogleResults() {
        assertNull(GoogleHookTarget.forVersion(null));
        assertNull(GoogleHookTarget.forVersion("17.70.110"));
        assertNull(GoogleHookTarget.forVersion("17.73.16.ve.arm64"));
        assertNull(GoogleHookTarget.forVersion("18.0.0"));
    }
}
