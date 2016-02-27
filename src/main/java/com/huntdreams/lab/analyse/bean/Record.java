package com.huntdreams.lab.analyse.bean;

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

    private Boolean ill;//是否患病
    private SEX sex;//性别
    private ArrayList<Gene> genes;//基因

    public Boolean getIll() {
        return ill;
    }

    public void setIll(Boolean ill) {
        this.ill = ill;
    }

    public SEX getSex() {
        return sex;
    }

    public void setSex(SEX sex) {
        this.sex = sex;
    }

    public ArrayList<Gene> getGenes() {
        return genes;
    }

    public void setGenes(ArrayList<Gene> genes) {
        this.genes = genes;
    }
}