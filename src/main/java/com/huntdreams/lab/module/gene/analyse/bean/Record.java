package com.huntdreams.lab.module.gene.analyse.bean;

import java.util.ArrayList;

/**
 * Record:每一行记录
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 2/27/16 10:10 PM.
 */
public class Record {

    private enum SEX {
        MALE, FEMALE
    }

    private boolean ill;//是否患病
    private SEX sex;//性别
    private ArrayList<Gene> geneList;//基因

    public Boolean getIll() {
        return ill;
    }

    public void setIll(boolean ill) {
        this.ill = ill;
    }

    public SEX getSex() {
        return sex;
    }

    public void setSex(SEX sex) {
        this.sex = sex;
    }

    public ArrayList<Gene> getGeneList() {
        return geneList;
    }

    public void setGeneList(ArrayList<Gene> geneList) {
        this.geneList = geneList;
    }
}