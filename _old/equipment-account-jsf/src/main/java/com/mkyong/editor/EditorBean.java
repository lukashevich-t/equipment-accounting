package com.mkyong.editor;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = "editor")
public class EditorBean {

    private String value = "This editor is provided by PrimeFaces";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @PostConstruct
    public void created() {
        System.out.println("EditorBean created");
    }
}
