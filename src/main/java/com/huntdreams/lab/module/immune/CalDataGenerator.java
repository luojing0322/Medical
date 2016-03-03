package com.huntdreams.lab.module.immune;

import com.huntdreams.lab.common.BaseProcessor;
import jxl.Sheet;
import jxl.Workbook;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * CalDataGenerator 计数类
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 3/3/16 1:39 PM.
 */
public class CalDataGenerator extends BaseProcessor {

    private String baseFile = docPath + "/immune/immune.xls";
    private String areaStaticsOutFile = docPath + "/immune/immune_area.txt";

    /* 区域统计数据 */
    private Map<String, Integer> areaStatics = new HashMap<String, Integer>();

    /**
     * 初始化地区统计信息
     */
    private void initAreas() {
        areaStatics.put("北京", 0);
        areaStatics.put("上海", 0);
        areaStatics.put("天津", 0);
        areaStatics.put("重庆", 0);
        areaStatics.put("黑龙江", 0);
        areaStatics.put("吉林", 0);
        areaStatics.put("辽宁", 0);
        areaStatics.put("江苏", 0);
        areaStatics.put("山东", 0);
        areaStatics.put("安徽", 0);
        areaStatics.put("河北", 0);
        areaStatics.put("河南", 0);
        areaStatics.put("湖北", 0);
        areaStatics.put("湖南", 0);
        areaStatics.put("江西", 0);
        areaStatics.put("陕西", 0);
        areaStatics.put("山西", 0);
        areaStatics.put("四川", 0);
        areaStatics.put("青海", 0);
        areaStatics.put("海南", 0);
        areaStatics.put("广东", 0);
        areaStatics.put("贵州", 0);
        areaStatics.put("浙江", 0);
        areaStatics.put("福建", 0);
        areaStatics.put("台湾", 0);
        areaStatics.put("甘肃", 0);
        areaStatics.put("云南", 0);
        areaStatics.put("内蒙", 0);
        areaStatics.put("宁夏", 0);
        areaStatics.put("新疆", 0);
        areaStatics.put("西藏", 0);
        areaStatics.put("广西", 0);
        areaStatics.put("香港", 0);
        areaStatics.put("澳门", 0);
    }

    /**
     * 统计地区出现次数
     *
     * @param areaStr 表格中的地区
     */
    private void countAreas(String areaStr) {
        Iterator iter = areaStatics.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String area = (String) entry.getKey();
            Integer areaCnt = (Integer) entry.getValue();
            if (areaStr.contains(area)) {
                areaStatics.put(area, areaCnt + 1);
                break;
            }
        }
    }

    /**
     * 将数字换算成比例
     */
    private Map<String, Double> countAreasRatio() {
        Iterator iter = areaStatics.entrySet().iterator();
        Integer allPeople = 0;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String area = (String) entry.getKey();
            Integer areaCnt = (Integer) entry.getValue();
            allPeople += areaCnt;
        }
        iter = areaStatics.entrySet().iterator();
        // 换算成比例
        Map<String, Double> areaStaticsRatio = new HashMap<String, Double>();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String area = (String) entry.getKey();
            Integer areaCnt = (Integer) entry.getValue();
            areaStaticsRatio.put(area, 100 * (1.0 * areaCnt / allPeople));
        }
        return areaStaticsRatio;
    }

    /**
     * 获得地区的数据
     *
     * @return
     */
    private Map<String, Double> getAreaStatics() {
        // 初始化
        this.initAreas();
        Map<String, Double> areaStaticsRatio = new HashMap<String, Double>();
        // 统计地区出现次数
        Workbook readWb = null;
        try {
            // 直接从本地文件创建Workbook
            InputStream readInputStream = new FileInputStream(baseFile);
            readWb = Workbook.getWorkbook(readInputStream);
            Integer sheetIndex = 1;
            Integer areaCol = 17;//地区所在的列
            // 获取读Sheet表
            Sheet readSheet = readWb.getSheet(sheetIndex);
            String sheetName = readSheet.getName();
            // 获取Sheet表中所包含的总列数
            int rsColumns = readSheet.getColumns();
            // 获取Sheet表中所包含的总行数
            int rsRows = readSheet.getRows();
            // 统计地区次数
            for (int row = 1; row < rsRows; row++) {
                String areaStr = readSheet.getCell(areaCol, row).getContents();
                this.countAreas(areaStr);
            }
            // 换算成比例
            areaStaticsRatio = countAreasRatio();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWb.close();
        }
        return areaStaticsRatio;
    }

    /**
     * 生成区域统计数据
     */
    private void geneAreaStatics() {
        Map<String, Double> areaStaticsRatio = getAreaStatics();
        List<Map.Entry<String, Double>> areaInfo =
                new ArrayList<Map.Entry<String, Double>>(areaStaticsRatio.entrySet());
        //排序
        Collections.sort(areaInfo, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return (o2.getValue() - o1.getValue()) >= 0 ? 1 : -1;
            }
        });

        try {
            FileWriter writer = new FileWriter(areaStaticsOutFile);
            writer.write("area" + "\t" + "areaCnt" + "\n");
            for (Map.Entry<String, Double> entry : areaInfo) {
                String area = entry.getKey();
                Double areaCnt = entry.getValue();
                if (areaCnt > 0)
                    writer.write(area + "\t" + areaCnt + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CalDataGenerator calDataGenerator = new CalDataGenerator();
        calDataGenerator.geneAreaStatics();
    }
}