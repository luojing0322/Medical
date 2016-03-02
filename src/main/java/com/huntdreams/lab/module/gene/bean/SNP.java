package com.huntdreams.lab.module.gene.bean;

/**
 * SNP 位点
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 2/27/16 10:07 PM.
 */
public class SNP {
    //位点名,如rs2178658
    private String name;

    //位点的值,如C C
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
