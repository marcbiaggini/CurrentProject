package com.rockspoon.rockandui.RecyclerTools;

import com.rockspoon.rockandui.RecyclerTools.HolderWrapper;

abstract public class ExpandableBaseHolderWrapper extends HolderWrapper {

    protected HolderWrapper headerWrapper;

    public ExpandableBaseHolderWrapper(HolderWrapper headerWrapper) {
        this.headerWrapper = headerWrapper;
    }

    public HolderWrapper getHeaderView() {
        return headerWrapper;
    }
}