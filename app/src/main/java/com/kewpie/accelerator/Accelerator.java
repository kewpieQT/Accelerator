package com.kewpie.accelerator;

public class Accelerator {
    static{
        System.loadLibrary("alloc-lib");
    }
    public native void setSaveDataDirectory(String dir);

    public native void dumpAllocationDataInLog();

    public native void startAllocationTracker();

    public native void stopAllocationTracker();

    public native int initForArt(int apiLevel, int allocRecordMax);
}
