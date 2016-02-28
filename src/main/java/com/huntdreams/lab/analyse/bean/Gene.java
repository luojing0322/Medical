package com.huntdreams.lab.analyse.bean;

import java.util.ArrayList;

/**
 * 基因实体类
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 2/27/16 10:04 PM.
 */
public class Gene {

    //基因型名,如ABCB1
    private String name;

    //该基因的位点
    private ArrayList<SNP> snpList;

    private Integer snpCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SNP> getSnpList() {
        return snpList;
    }

    public void setSnpList(ArrayList<SNP> snpList) {
        this.snpList = snpList;
    }

    public Integer getSnpCount() {
        return snpCount;
    }

    public void setSnpCount(Integer snpCount) {
        this.snpCount = snpCount;
    }
}
