package com.huntdreams.lab.filter;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * DelCol
 * 删除无用的列
 *
 * @author tyee.noprom@qq.com
 * @time 2/18/16 11:42 AM.
 */
public class DelCol {

    public static String basePath = "/Users/noprom/Documents/Dev/Java/Pro/Medical/doc";
    public static String baseFile = basePath + "/nozero.xls";
    private static Map<String, String> map = new HashMap<String, String>();
    private static ArrayList<Integer> omitCol = new ArrayList<Integer>();

    // AGCT Mapping relation
    private static void initMap() {
        omitCol.add(1);
        omitCol.add(2);
        omitCol.add(3);
        map.put("A A", "2");
        map.put("A G", "5");
        map.put("A C", "14");
        map.put("A T", "11");
        map.put("G A", "6");
        map.put("G G", "1");
        map.put("G C", "12");
        map.put("G T", "16");
        map.put("C A", "7");
        map.put("C G", "8");
        map.put("C C", "4");
        map.put("C T", "9");
        map.put("T A", "15");
//        map.put("T G", "2");
        map.put("T C", "10");
        map.put("T T", "3");
    }

    public static void main(String[] args) {
        initMap();
        Workbook readwb = null;
        try {
            //构建Workbook对象, 只读Workbook对象
            //直接从本地文件创建Workbook
            InputStream instream = new FileInputStream(baseFile);
            readwb = Workbook.getWorkbook(instream);
            //Sheet的下标是从0开始
            //利用已经创建的Excel工作薄,创建新的可写入的Excel工作薄
            WritableWorkbook wwb = Workbook.createWorkbook(new FileOutputStream(new File(basePath + "/out.xls")));
            //获取第一张Sheet表
            Sheet readsheet = readwb.getSheet(0);
            //创建第一张工作表
            WritableSheet ws = wwb.createSheet("Number", 0);
            //获取Sheet表中所包含的总列数
            int rsColumns = readsheet.getColumns();
            //获取Sheet表中所包含的总行数
            int rsRows = readsheet.getRows();
            //获取指定单元格的对象引用
            for (int i = 0; i < rsRows; i++) {
                for (int j = 0; j < rsColumns; j++) {
                    if (omitCol.contains(j) || j == rsColumns - 1) {
                        continue;
                    }
                    Cell cell = readsheet.getCell(j, i);
                    String key = cell.getContents();
                    String value = null;
                    if (i != 0 && j != 0) {
                        value = map.get(key);
                    } else {
                        value = key;
                    }
                    System.out.print("key = " + key + ", value = " + value + ";");
                    //写入Excel
                    int index = j == 0 ? j : j - 3;
                    Label label = new Label(index, i, value);
                    ws.addCell(label);
                }
                System.out.println();
            }
            //写入Excel对象
            wwb.write();
            wwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readwb.close();
        }
    }
}