package com.huntdreams.lab.module.immune;

import com.huntdreams.lab.common.BaseProcessor;
import com.huntdreams.lab.module.immune.bean.Factor;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.Document;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * BoxPlotGenerator
 * 生成boxplot所需要的数据
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 2/29/16 2:18 PM.
 */
public class BoxPlotGenerator extends BaseProcessor {

    private String baseFile = docPath + "/immune/immune.xls";
    private String outBoxPlotFile = docPath + "/immune/out_immune.xls";
    private String outRegressionFile = docPath + "/immune/out_reg_immune.xls";
    private static boolean DEBUG = false;

    /**
     * 获得影响因子
     *
     * @param sheet sheet
     * @return
     */
    private List<Factor> getFactors(Sheet sheet) {
        String header[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
                "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL", "AM", "AN",
                "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ",
                "BA", "BB", "BC", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BK", "BL", "BM", "BN",
                "BO", "BP", "BQ", "BR", "BS", "BT", "BU", "BV", "BW", "BX", "BY", "BZ"};
        List<Factor> factors = new ArrayList<Factor>();
        int rows = sheet.getRows();
        int cols = sheet.getColumns();
        for (int j = 5; j < cols; j++) {
            // R之后的数据都是脏数据
            if (j == 6) {
                continue;
            }
            if (j >= 17) {
                break;
            }
            // String cellVal = sheet.getCell(j, 0).getContents();
            String cellVal = header[j];
            Factor factor = new Factor();
            factor.setName(cellVal);
            // 填充参数的值
            List<String> factorVals = new ArrayList<String>();
            for (int i = 1; i < rows; i++) {
                String factorVal = sheet.getCell(j, i).getContents();
                if (factorVal.equals("") || factorVal.equals("NA")
                        || factorVal.equals("00")) {
                    factorVal = "0";
                }
                if (!factorVal.equals("0") && filter(factorVal))
                    factorVals.add(factorVal);
            }
            factor.setFactorVals(factorVals);
            factors.add(factor);
        }

        // 对数据进行归一化处理
        for (Factor factor : factors) {
            List<String> facList = factor.getFactorVals();
            List<String> newList = new ArrayList<String>();
            double min = getMin(facList);
            double max = getMax(facList);
            for (String fac : facList) {
                Double oneFac = (Double.parseDouble(fac) - min) / (max - min);
                double f = oneFac;
                BigDecimal b = new BigDecimal(f * 100);
                Double f1 = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                newList.add(f1.toString());
            }
            factor.setFactorVals(newList);
        }

        if (DEBUG) {
            for (Factor factor : factors) {
                logger.debug(factor);
            }
        }
        return factors;
    }

    /**
     * 实验室检查（免疫）
     * 生成BoxPlot所需要的代码
     */
    private void genBoxPlotCode() {
        Workbook readWb = null;
        try {
            // 构建Workbook对象, 只读Workbook对象
            // 直接从本地文件创建Workbook
            InputStream readInputStream = new FileInputStream(baseFile);
            readWb = Workbook.getWorkbook(readInputStream);
            Integer sheetIndex = 4;
            // 获取读Sheet表
            Sheet readwbSheet = readWb.getSheet(sheetIndex);
            String sheetName = readwbSheet.getName();
            // 获取Sheet表中所包含的总列数
            int rsColumns = readwbSheet.getColumns();
            // 获取Sheet表中所包含的总行数
            int rsRows = readwbSheet.getRows();
            List<Factor> factors = getFactors(readwbSheet);
            List<String> facList = new ArrayList<String>();
            for (Factor factor : factors) {
                facList.add(factor.getName());
                StringBuilder builder = new StringBuilder();
                builder.append(factor.getName());
                builder.append(" <- c(");
                List<String> facVals = factor.getFactorVals();
                String lastVal = facVals.get(facVals.size() - 1);

                for (int i = 0, size = facVals.size(); i < size; i++) {
                    String facVal = facVals.get(i);
                    if (filter(facVal)) {
                        builder.append(facVal);
                        if (i != size - 1) {
                            builder.append(", ");
                        }
                    }
                }
                builder.append(")");
                String facData = builder.toString();
                System.out.println(facData);
            }

            // 生成R语言boxplot图所需要的代码
            StringBuilder boxplotCode = new StringBuilder();
            boxplotCode.append("boxplot(");
            for (String fac : facList) {
                boxplotCode.append(fac + ",");
            }
            boxplotCode.append("notch=FALSE, names=c(");
            String lastFacVal = facList.get(facList.size() - 1);
            for (String fac : facList) {
                boxplotCode.append("'" + fac + "'");
                if (!fac.equals(lastFacVal)) {
                    boxplotCode.append(", ");
                }
            }
            // 最后一行boxplot字符串
            boxplotCode.append("))");
            System.out.println(boxplotCode.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWb.close();
        }
    }

    /**
     * 过滤数据
     */
    private void parseXSLFile() {
        Workbook readWb = null;
        try {
            // 构建Workbook对象, 只读Workbook对象
            // 直接从本地文件创建Workbook
            InputStream readInputStream = new FileInputStream(baseFile);
            WritableWorkbook wwb = Workbook.createWorkbook(new FileOutputStream(new File(outBoxPlotFile)));
            WritableSheet ws = wwb.createSheet("boxplot", 0);
            readWb = Workbook.getWorkbook(readInputStream);
            Integer sheetIndex = 0;
            // 获取读Sheet表
            Sheet readwbSheet = readWb.getSheet(sheetIndex);
            String sheetName = readwbSheet.getName();
            // 获取Sheet表中所包含的总列数
            int rsColumns = readwbSheet.getColumns();
            // 获取Sheet表中所包含的总行数
            int rsRows = readwbSheet.getRows();
            for (int i = 0; i < rsRows; i++) {
                for (int j = 0; j < rsColumns; j++) {
                    String cellVal = readwbSheet.getCell(j, i).getContents();
                    if (cellVal.startsWith("<")) {
                        cellVal = cellVal.substring(1, cellVal.length());
                    }
                    if (!filter(cellVal)) {
                        cellVal = "0";
                    }
                    Label label = new Label(j, i, cellVal);
                    ws.addCell(label);
                }
            }
            //写入Excel对象
            wwb.write();
            wwb.close();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对数据进行归一化处理
     * 使其在0-100之间均匀分布
     */
    private void regData() {
        Workbook readWb = null;
        try {
            // 构建Workbook对象, 只读Workbook对象
            // 直接从本地文件创建Workbook
            InputStream readInputStream = new FileInputStream(outBoxPlotFile);
            WritableWorkbook wwb = Workbook.createWorkbook(new FileOutputStream(new File(outRegressionFile)));
            WritableSheet ws = wwb.createSheet("regboxplot", 0);
            readWb = Workbook.getWorkbook(readInputStream);
            Integer sheetIndex = 0;
            // 获取读Sheet表
            Sheet readSheet = readWb.getSheet(sheetIndex);
            String sheetName = readSheet.getName();
            // 获取Sheet表中所包含的总列数
            int rsColumns = readSheet.getColumns();
            // 获取Sheet表中所包含的总行数
            int rsRows = readSheet.getRows();
            for (int i = 0; i < rsRows; i++) {
                for (int j = 0; j < rsColumns; j++) {
                    String cellVal = readSheet.getCell(j, i).getContents();
                    String regCellVal = null;
                    if (i != 0) {
                        regCellVal = getRegVal(readSheet, cellVal, j);
                    }
                    Label label = new Label(j, i, i == 0 ? cellVal : regCellVal);
                    ws.addCell(label);
                }
            }
            //写入Excel对象
            wwb.write();
            wwb.close();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得某一列归一化之后的值
     *
     * @param readSheet
     * @param col
     * @return
     */
    private String getRegVal(Sheet readSheet, String rawStr, int col) {
        // 获取Sheet表中所包含的总列数
        int rsColumns = readSheet.getColumns();
        // 获取Sheet表中所包含的总行数
        int rsRows = readSheet.getRows();
        double max = -1.0;
        double min = 10000.0;
        // 计算得到最大最小值
        for (int i = 1; i < rsRows; i++) {
            String cellValStr = readSheet.getCell(col, i).getContents();
            double cellVal = Double.parseDouble(cellValStr);
            if (cellVal > max) {
                max = cellVal;
            }
            if (cellVal < min) {
                min = cellVal;
            }
        }
        double rawVal = Double.parseDouble(rawStr);
        // 方法一:
        // Double newVal = rawVal / max * 100;
        // 方法二:
        Double newVal = (rawVal - min) / (max - min) * 100;
        return rawStr;
    }

    /**
     * 过滤不满足条件的值
     *
     * @param str
     * @return
     */
    private boolean filter(String str) {
        if (StringUtils.contains("<", str) || str.equals("NA")) {
            return false;
        }
        return true;
    }

    /**
     * 获得该列最大值
     *
     * @param list
     * @return
     */
    private double getMax(List<String> list) {
        Double max = -1.0;
        for (String val : list) {
            double dVal = Double.parseDouble(val);
            if (dVal > max) {
                max = dVal;
            }
        }
        return max;
    }

    /**
     * 获得该列最小值
     *
     * @param list
     * @return
     */
    private double getMin(List<String> list) {
        Double min = 5000.0;
        for (String val : list) {
            double dVal = Double.parseDouble(val);
            if (dVal < min) {
                min = dVal;
            }
        }
        return min;
    }

    public static void main(String[] args) {
        BoxPlotGenerator generator = new BoxPlotGenerator();
        generator.regData();
    }
}