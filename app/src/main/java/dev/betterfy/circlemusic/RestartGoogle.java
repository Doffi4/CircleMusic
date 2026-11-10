package dev.betterfy.circlemusic;
final class RestartGoogle {
    static boolean waitFor(Process process) throws InterruptedException { return process.waitFor() == 0; }
}
