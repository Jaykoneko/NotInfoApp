package com.github.barteksc.pdfviewer.link;

import com.github.barteksc.pdfviewer.model.LinkTapEvent;

public interface LinkHandler {
    void handleLinkEvent(LinkTapEvent linkTapEvent);
}
