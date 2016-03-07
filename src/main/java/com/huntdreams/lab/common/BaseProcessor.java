package com.huntdreams.lab.common;

import jxl.Sheet;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;

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
     * @param factor
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
     * 过滤字符串
     *
     * @param str
     * @return
     */
    public static boolean filterStr(String str) {
        if (str.equals("") || str.equals("NA") || str.contains("+") || str.contains("-")
                || str.contains("<"))
            return false;
        return true;
    }
}