package com.wjdu.res.domain;

import java.io.Serializable;

public class Record implements Serializable{
    private static final long serialVersionUID = -2626635142432033454L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
}
