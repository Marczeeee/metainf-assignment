package hu.metainf.jiracsvuploader.test;

import org.junit.Test;

import hu.metainf.jiracsvuploader.AppMain;

public class TestCSVReaderApp {
    @Test
    public void testCSVReaderApp() {
        final String[] args = new String[4];
        args[0] = "-t";
        args[1] = "8";
        args[2] = "-f";
        args[3] = "target\\test-classes\\miketest.csv";
        AppMain.main(args);
    }
}
