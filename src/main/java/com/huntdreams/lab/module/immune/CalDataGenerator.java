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
    private String agStaticsOutFile = docPath + "/immune/immune_ag.txt";

    /* 区域统计数据 */
    private Map<String, Integer> areaStatics = new HashMap<String, Integer>();

    /* A/G 统计数据 */
    private Map<String, Integer> agStatics = new HashMap<String, Integer>();

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

    /**
     * 获得A/G的统计数据
     *
     * @return
     */
    private Map<String, Integer> getAgStatics() {
        Workbook readWb = null;
        try {
            // 直接从本地文件创建Workbook
            InputStream readInputStream = new FileInputStream(baseFile);
            readWb = Workbook.getWorkbook(readInputStream);
            Integer sheetIndex = 1;
            // 获取读Sheet表
            Sheet readSheet = readWb.getSheet(sheetIndex);
            Integer factorCol = getFactorCol(readSheet, "A/G");//因子所在的列
            // 获取Sheet表中所包含的总列数
            int rsColumns = readSheet.getColumns();
            // 获取Sheet表中所包含的总行数
            int rsRows = readSheet.getRows();
            // 统计次数
            for (int row = 1; row < rsRows; row++) {
                String str = readSheet.getCell(factorCol, row).getContents();
                Integer rawVal = agStatics.get(str) == null ? 0 : agStatics.get(str);
                agStatics.put(str, rawVal + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWb.close();
        }
        return agStatics;
    }

    /**
     * 将A/G统计结果写入文件
     *
     */
    private void writeAGStatics() {
        agStatics = getAgStatics();
        writeFactorStatics(agStatics, agStaticsOutFile, "AG", "Count", false);
    }

    /**
     * 计算AG超出的值
     */
    private void calAGFreqnenct() {
        agStatics = getAgStatics();
        calFrequency(agStatics, 10);
    }

    /**
     * 计算超过某个值的频次
     *
     * @param factor
     * @param threshold
     */
    private void calFrequency(Map<String, Integer> factor, Integer threshold) {
        Iterator iter = factor.entrySet().iterator();
        Integer facCnt = 0;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String entryKey = (String) entry.getKey();
            Integer entryValue = (Integer) entry.getValue();
            if (entryValue >= threshold)
                facCnt += 1;
        }
        System.out.println("> " + threshold + " = " + facCnt);
    }

    /**
     * 将因子统计结果写入文件
     *
     * @param factor 因子
     * @param key 因子名
     * @param value 因子值
     */
    private void writeFactorStatics(Map<String, Integer> factor, String fileName,
                                    String key, String value, boolean showHeader) {
        List<Map.Entry<String, Integer>> facInfo =
                new ArrayList<Map.Entry<String, Integer>>(factor.entrySet());
        //排序
        Collections.sort(facInfo, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                // return (o2.getValue() - o1.getValue());
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        try {
            FileWriter writer = new FileWriter(fileName);
            if (showHeader)
                writer.write(key + "\t" + value + "\n");
            for (Map.Entry<String, Integer> entry : facInfo) {
                String entryKey = entry.getKey();
                Integer entryValue = entry.getValue();
                if (entryValue > 0 && !entryKey.equals("") && !entryKey.equals("NA"))
                    writer.write(entryKey + "\t" + entryValue + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得某一个因子所在的列
     *
     * @param factor
     * @return
     */
    private Integer getFactorCol(Sheet sheet, String factor) {
        Integer colIndex = 0;
        // 获取Sheet表中所包含的总列数
        int rsColumns = sheet.getColumns();
        // 获取Sheet表中所包含的总行数
        int rsRows = sheet.getRows();
        for (int col = 0; col < rsColumns; col++) {
            String str = sheet.getCell(col, 0).getContents();
            if (str.contains(factor)) {
                colIndex = col;
                return colIndex;
            }
        }
        return colIndex;
    }

    public static void main(String[] args) {
        CalDataGenerator calDataGenerator = new CalDataGenerator();
        calDataGenerator.calAGFreqnenct();
    }
}