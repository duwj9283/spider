package com.wjdu.res.dto;

public class ImageDTO {
    private String remoteSrc;

    private String localScr;

    private String remoteShortSrc;

    public String getRemoteShortSrc() {
        return remoteShortSrc;
    }

    public void setRemoteShortSrc(String remoteShortSrc) {
        this.remoteShortSrc = remoteShortSrc;
    }

    public String getRemoteSrc() {
        return remoteSrc;
    }

    public void setRemoteSrc(String remoteSrc) {
        this.remoteSrc = remoteSrc;
    }

    public String getLocalScr() {
        return localScr;
    }

    public void setLocalScr(String localScr) {
        this.localScr = localScr;
    }
}
