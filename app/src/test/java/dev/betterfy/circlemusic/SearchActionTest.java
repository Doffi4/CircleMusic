package dev.betterfy.circlemusic;
import org.junit.Test;
import static org.junit.Assert.*;
public class SearchActionTest {
    @Test public void acceptsOnlySupportedSearchActionsAndKeepsExistingSettings() {
        assertEquals("android.intent.action.SEARCH", SearchAction.validated(null));
        assertEquals("android.intent.action.SEARCH", SearchAction.validated("unexpected"));
        assertEquals("com.google.android.gms.actions.SEARCH_ACTION", SearchAction.validated("com.google.android.gms.actions.SEARCH_ACTION"));
    }
}
