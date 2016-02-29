package com.huntdreams.lab.module.immune.bean;

import java.util.List;

/**
 * Factor
 * 影响病情的因子
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 2/29/16 2:31 PM.
 */
public class Factor {

    private String name;//因子名称
    private List<String> factorVals;//值

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFactorVals() {
        return factorVals;
    }

    public void setFactorVals(List<String> factorVals) {
        this.factorVals = factorVals;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        for (String factorVal : factorVals) {
            builder.append(factorVal + ",");
        }

        return "Factor{" +
                "name='" + name + '\'' +
                ", factorVals=" + builder.toString() +
                '}';
    }
}