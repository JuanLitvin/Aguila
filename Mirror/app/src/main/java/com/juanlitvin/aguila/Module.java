package com.juanlitvin.aguila;

import android.os.Bundle;

public class Module {
    private MirrorModule module;
    private int fragmentId;
    private Bundle extras;

    private Module() {

    }

    public Module(MirrorModule module, int fragmentId, Bundle extras) {
        this.module = module;
        this.fragmentId = fragmentId;
        this.extras = extras;
    }

    public MirrorModule getModule() {
        return module;
    }

    public int getFragmentId() {
        return fragmentId;
    }

    public Bundle getExtras() {
        return extras;
    }

    public boolean hasExtras() {
        if (extras == null) return false;
        return extras.size() > 0;
    }

}
