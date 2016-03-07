package com.huntdreams.lab.common;

import jxl.Sheet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.pattern.LineSeparatorPatternConverter;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base BaseProcessor
 *
 * @author tyee.noprom@qq.com
 * @time 2/18/16 12:31 PM.
 */
public class BaseProcessor {

    // logger
    protected Logger logger = Logger.getLogger(getClass().getName());

    // docpath
    protected String docPath;

    // init
    public BaseProcessor() {
        // 初始化路径信息
        File classPath = new File(this.getClass().getResource("/").getPath());
        File targetPath = new File(classPath.getParent());
        File docPath = new File(targetPath.getParent());
        this.docPath = docPath.getAbsolutePath() + "/doc";
    }

    public static boolean isDouble(String str) {
        if (StringUtils.contains("<", str)) {
            return false;
        }
        if (StringUtils.startsWith(".", str)) {
            return false;
        }
        // 处理有几个小数点的情况
        String[] dots = str.split("\\.");
        if (dots.length >= 3) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException ex) {
        }
        return false;
    }


    /**
     * 获得某一个因子所在的列
     *
     * @param factor 因子
     * @return
     */
    public static Integer getFactorCol(Sheet sheet, String factor) {
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

    /**
     * 获得某一个指标的值
     *
     * @param sheet   sheet
     * @param colName colName
     * @return
     */
    public static ArrayList<Double> getColVals(Sheet sheet, String colName) {
        ArrayList<Double> list = new ArrayList<Double>();
        // 获取Sheet表中所包含的总列数
        int rsColumns = sheet.getColumns();
        // 获取Sheet表中所包含的总行数
        int rsRows = sheet.getRows();
        int col = getFactorCol(sheet, colName);
        for (int row = 1; row < rsRows; row++) {
            String str = sheet.getCell(col, row).getContents();
            str = str.replace('<', ' ').replace('>', ' ').trim();
            if (filterStr(str)) {
                Double val = Double.parseDouble(str);
                list.add(val);
            }
        }
        return list;
    }

    /**
     * 获得某一个索引列之后的headers
     *
     * @param sheet
     * @param start
     * @param end
     * @return
     */
    public ArrayList<String> getSheetHeaders(Sheet sheet, Integer start, Integer end) {
        ArrayList<String> list = new ArrayList<String>();
        // 获取Sheet表中所包含的总列数
        int rsColumns = sheet.getColumns();
        // 获取Sheet表中所包含的总行数
        int rsRows = sheet.getRows();
        for (int col = start; col < end; col++) {
            String str = sheet.getCell(col, 0).getContents();
            list.add(str);
        }
        return list;
    }

    /**
     * 打印某列指标的值
     *
     * @param list
     */
    public static void printColVals(ArrayList<Double> list) {
        for (Double val : list) {
            System.out.println(val);
        }
    }

    /**
     * 过滤字符串
     *
     * @param str
     * @return
     */
    public static boolean filterStr(String str) {
        str = str.replace('<', ' ').replace('>', ' ');
        str = str.trim();
        if (isChineseChar(str))
            return false;
        if (str.equals("") || str.equals("NA") || str.contains("+") || str.contains("-"))
            return false;
        return true;
    }

    /**
     * Double 转为 String
     *
     * @param val
     * @return
     */
    public static String double2String(Double val) {
        DecimalFormat format = new DecimalFormat("#.00");
        String s = format.format(val);
        return s;
    }

    /**
     * 是否是中文
     *
     * @param str
     * @return
     */
    public static boolean isChineseChar(String str) {
        boolean temp = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            temp = true;
        }
        return temp;
    }
}