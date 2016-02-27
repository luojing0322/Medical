package com.huntdreams.lab;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

/**
 * JXLFilterZero
 * Filter 00 with max value in every column
 *
 * @author tyee.noprom@qq.com
 * @time 2/18/16 12:14 PM.
 */
public class JXLFilterZero extends Processor {

    private final String baseFile = docPath + "/in_filter_00.xls";
    private final String outFile = docPath + "/out_filter_00.xls";
    private final Integer MAX_ROW = 233;

    // 不扫描的列
    private ArrayList<Integer> omitCol = new ArrayList<Integer>();

    // 不扫描的行
    private ArrayList<Integer> omitRow = new ArrayList<Integer>();


    /**
     * 初始化成员变量
     */
    private void init() {
        omitCol.add(1);
        omitCol.add(2);
        omitCol.add(3);
        omitCol.add(4);
        omitCol.add(5);
        omitCol.add(6);
        omitRow.add(0);
        omitRow.add(1);
        logger.debug(baseFile);
    }

    private void run() {
        Workbook readWb = null;
        try {
            //构建Workbook对象, 只读Workbook对象
            //直接从本地文件创建Workbook
            InputStream readInputStream = new FileInputStream(baseFile);
            readWb = Workbook.getWorkbook(readInputStream);
            //Sheet的下标是从0开始
            //利用已经创建的Excel工作薄,创建新的可写入的Excel工作薄
            WritableWorkbook wwb = Workbook.createWorkbook(new FileOutputStream(new File(outFile)));
            Sheet[] readSheetList = readWb.getSheets();
            for (int sheetIndex = 2, sheetSize = readSheetList.length;
                 sheetIndex < sheetSize; sheetIndex++) {
                //获取读Sheet表
                Sheet readwbSheet = readWb.getSheet(sheetIndex);
                String sheetName = readwbSheet.getName();
                //创建工作表
                WritableSheet ws = wwb.createSheet(sheetName, sheetIndex - 2);
                //获取Sheet表中所包含的总列数
                int rsColumns = readwbSheet.getColumns();
                //获取Sheet表中所包含的总行数
                //int rsRows = readwbSheet.getRows();
                int rsRows = readwbSheet.getRows();
                logger.debug("read sheet : " + sheetName + " -> Columns = " + rsColumns + ", Rows = " + rsRows);

                //获取指定单元格的对象引用
                for (int i = 0; i < rsRows; i++) {
                    for (int j = 0; j < rsColumns; j++) {
                        Cell cell = readwbSheet.getCell(j, i);
                        String value = cell.getContents();
                        if (value.equals("")) {
                            logger.debug("value is null");
                        }
                        if (value.equals("0 0")) {
                            value = getMostFrequentGen(readwbSheet, j);
                            logger.debug("00 --> " + value);
                        }
                        //写入Excel
                        Label label = new Label(j, i, value);
                        ws.addCell(label);
                    }
                }
                logger.debug("ws.getColumns() = " + ws.getColumns());
                logger.debug("ws.getRows() = " + ws.getRows());
            }

            //写入Excel对象
            wwb.write();
            wwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWb.close();
        }
    }

    /**
     * 获得某列基因出现次数最多的基因
     *
     * @param sheet sheet
     * @param col   列数
     * @return
     */
    private String getMostFrequentGen(Sheet sheet, int col) {
        // 每列某个基因出现的次数
        Map<String, Integer> frqMap = new HashMap<String, Integer>();
        int rsCols = sheet.getColumns();
        int rsRows = sheet.getRows();
        // 统计次数
        for (int row = 0; row < rsRows; row++) {
            Cell cell = sheet.getCell(col, row);
            String key = cell.getContents();
            if (key.equals("")) {
                logger.debug("key in getMostFrequentGen : " + key);
            }
            Integer oldVal = frqMap.get(key);
            if (oldVal == null) {
                oldVal = 0;
            }
            frqMap.put(key, oldVal + 1);
        }
        // 找出最大者
        int max = -1;
        String result = "";
        Iterator iter = frqMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            if (key.equals("")) {
                logger.debug(key + "is null");
            }
            Integer value = (Integer) entry.getValue();
            if (value > max) {
                max = value;
                result = key;
            }
        }
        return result.equals("") ? "xxx" : result;
    }

    public static void main(String[] args) {
        JXLFilterZero JXLFilterZero = new JXLFilterZero();
        // JXLFilterZero.init();
        JXLFilterZero.run();
    }
}
