package com.huntdreams.lab.module.immune;

import com.huntdreams.lab.common.BaseProcessor;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * KMeansProcessor
 * KMeans 聚类算法
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 16/3/7 下午4:04.
 */
public class KMeansProcessor extends BaseProcessor {

    private String baseFile = docPath + "/immune/immune.xls";
    private String kmeansOutFileImmune = docPath + "/immune/immune_kmeans_immune.xls";
    private String kmeansOutFileRegular = docPath + "/immune/immune_kmeans_regular.xls";
    private String kmeansOutFile = docPath + "/immune/immune_kmeans.xls";

    /**
     * 生成kmeans聚类结果
     *
     * @param facName
     * @param clzResult
     */
    private void facKmeansResult(
            WritableWorkbook wwb, WritableSheet ws,
            String facName, List<Double[]> clzResult, Integer cnt) throws IOException {
        try {
            //创建工作表
            ws = wwb.createSheet(filterHeader(facName), cnt);
            //ws = wwb.createSheet(cnt.toString(), cnt);
            for (int i = 0, size = clzResult.size(); i < size; i++) {
                Double[] oneClz = clzResult.get(i);
                Double minD = minDouble(oneClz);
                Double maxD = maxDouble(oneClz);
                String minMaxStr = "[" + minD + "-" + maxD + "]";
                Label minMax = new Label(i, 0, minMaxStr);
                if (oneClz.length > 0)
                    ws.addCell(minMax);
                logger.debug(minMaxStr);
                for (int j = 0; j < oneClz.length; j++) {
                    Label label = new Label(i, j + 1, String.valueOf(oneClz[j]));
                    ws.addCell(label);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 找最小值
     *
     * @param clz
     * @return
     */
    private Double minDouble(Double[] clz) {
        Double min = 100000000.0;
        for (Double d : clz) {
            if (d < min)
                min = d;
        }
        return min;
    }

    /**
     * 找最大值
     *
     * @param clz
     * @return
     */
    private Double maxDouble(Double[] clz) {
        Double max = -1.0;
        for (Double d : clz) {
            if (d > max)
                max = d;
        }
        return max;
    }

    /**
     * 过滤特殊字符
     *
     * @param str
     * @return
     */
    private String filterHeader(String str) {
        int left = str.indexOf("(");
        // String tmp = str.substring(0, left);
        String tmp = str.replace('/', ' ').trim();
        // logger.debug(tmp);
        return tmp;
    }

    /**
     * 得到kmeans结果并写入表格
     */
    private void getKmeansResult() {
        Workbook readWb = null;
        WritableWorkbook wwb = null;
        WritableSheet ws = null;
        try {
            // 直接从本地文件创建Workbook
            InputStream readInputStream = new FileInputStream(baseFile);
            readWb = Workbook.getWorkbook(readInputStream);
            wwb = Workbook.createWorkbook(new FileOutputStream(new File(kmeansOutFile)));
            Integer startIndex = genSheet(wwb, ws, readWb, 3, 5, 43, 0);
            genSheet(wwb, ws, readWb, 4, 5, 14, startIndex);
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
     * 生成sheet
     *
     * @param wwb
     * @param ws
     * @param readWb
     * @param sheetIndex
     * @param startCol
     * @param endCol
     * @param startSheetIndex
     * @return
     */
    private Integer genSheet(
            WritableWorkbook wwb, WritableSheet ws, Workbook readWb,
            Integer sheetIndex, Integer startCol,
            Integer endCol, Integer startSheetIndex) {
        Integer sheetCnt = startSheetIndex;
        // 获取读Sheet表
        Sheet readSheet = readWb.getSheet(sheetIndex);
        logger.debug(readSheet.getName());
        ArrayList<String> headers = getSheetHeaders(readSheet, startCol, endCol);
        for (String fac : headers) {
            ArrayList<Double> dataList = getColVals(readSheet, fac);
            Double[] p = dataList.toArray(new Double[dataList.size()]);
            //Double[] p = {1.0, 2.0, 3.0, 5.0, 6.0, 7.0, 9.0, 10.0, 11.0, 100.0, 150.0, 200.0, 1000.0};
            int k = 5;//聚类的类别数
            Double[][] g = cluster(p, k);
            List<Double[]> clzResult = Arrays.asList(g);
            try {
                facKmeansResult(wwb, ws, fac, clzResult, sheetCnt++);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sheetCnt;
    }

    private void testGenXsl() {
        try {
            WritableWorkbook wwb = Workbook.createWorkbook(new FileOutputStream(new File(kmeansOutFileImmune)));
            WritableSheet ws = null;
            for (int i = 1; i <= 10; i++) {
                //创建工作表
                ws = wwb.createSheet(String.valueOf(i), i);
                Label label = new Label(0, 0, String.valueOf(i));
                ws.addCell(label);
            }
            //写入Excel对象
            wwb.write();
            wwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public static void main(String[] args) {
        KMeansProcessor kMeansProcessor = new KMeansProcessor();
        kMeansProcessor.getKmeansResult();
        // kMeansProcessor.testGenXsl();
    }

    /**
     * 获得某列指标的值
     *
     * @return
     */
    private ArrayList<Double> getColVals(String facName) {
        Workbook readWb = null;
        ArrayList<Double> dataList = new ArrayList<Double>();
        try {
            // 直接从本地文件创建Workbook
            InputStream readInputStream = new FileInputStream(baseFile);
            readWb = Workbook.getWorkbook(readInputStream);
            Integer sheetIndex = 1;
            // 获取读Sheet表
            Sheet readSheet = readWb.getSheet(sheetIndex);
            dataList = getColVals(readSheet, facName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWb.close();
        }
        return dataList;
    }

    /**
     * 聚类函数主体。
     * 针对一维 double 数组。指定聚类数目 k。
     * 将数据聚成 k 类。
     *
     * @param p
     * @param k
     * @return
     */
    private static Double[][] cluster(Double[] p, int k) {
        // 存放聚类旧的聚类中心
        Double[] c = new Double[k];
        // 存放新计算的聚类中心
        Double[] nc = new Double[k];
        // 存放放回结果
        Double[][] g;
        // 初始化聚类中心
        // 经典方法是随机选取 k 个
        // 本例中采用前 k 个作为聚类中心
        // 聚类中心的选取不影响最终结果
        for (int i = 0; i < k; i++)
            c[i] = p[i];
        // 循环聚类，更新聚类中心
        // 到聚类中心不变为止
        while (true) {
            // 根据聚类中心将元素分类
            g = group(p, c);
            // 计算分类后的聚类中心
            for (int i = 0; i < g.length; i++) {
                nc[i] = center(g[i]);
            }
            // 如果聚类中心不同
            if (!equal(nc, c)) {
                // 为下一次聚类准备
                c = nc;
                nc = new Double[k];
            } else // 聚类结束
                break;
        }
        // 返回聚类结果
        return g;
    }

    /**
     * 聚类中心函数
     * 简单的一维聚类返回其算数平均值
     * 可扩展
     *
     * @param p
     * @return
     */
    private static double center(Double[] p) {
        return sum(p) / p.length;
    }

    /**
     * 给定 double 型数组 p 和聚类中心 c。
     * 根据 c 将 p 中元素聚类。返回二维数组。
     * 存放各组元素。
     *
     * @param p
     * @param c
     * @return
     */
    private static Double[][] group(Double[] p, Double[] c) {
        // 中间变量，用来分组标记
        int[] gi = new int[p.length];
        // 考察每一个元素 pi 同聚类中心 cj 的距离
        // pi 与 cj 的距离最小则归为 j 类
        for (int i = 0; i < p.length; i++) {
            // 存放距离
            Double[] d = new Double[c.length];
            // 计算到每个聚类中心的距离
            for (int j = 0; j < c.length; j++) {
                d[j] = distance(p[i], c[j]);
            }
            // 找出最小距离
            int ci = min(d);
            // 标记属于哪一组
            gi[i] = ci;
        }
        // 存放分组结果
        Double[][] g = new Double[c.length][];
        // 遍历每个聚类中心，分组
        for (int i = 0; i < c.length; i++) {
            // 中间变量，记录聚类后每一组的大小
            int s = 0;
            // 计算每一组的长度
            for (int j = 0; j < gi.length; j++)
                if (gi[j] == i)
                    s++;
            // 存储每一组的成员
            g[i] = new Double[s];
            s = 0;
            // 根据分组标记将各元素归位
            for (int j = 0; j < gi.length; j++)
                if (gi[j] == i) {
                    g[i][s] = p[j];
                    s++;
                }
        }
        // 返回分组结果
        return g;
    }

    /**
     * 计算两个点之间的距离， 这里采用最简单得一维欧氏距离， 可扩展。
     *
     * @param x
     * @param y
     * @return
     */
    private static double distance(double x, double y) {
        return Math.abs(x - y);
    }

    /**
     * 返回给定 double 数组各元素之和。
     *
     * @param p
     * @return
     */
    private static double sum(Double[] p) {
        double sum = 0.0;
        for (int i = 0; i < p.length; i++)
            sum += p[i];
        return sum;
    }

    /**
     * 给定 double 类型数组，返回最小值得下标。
     *
     * @param p
     * @return
     */
    private static int min(Double[] p) {
        int i = 0;
        double m = p[0];
        for (int j = 1; j < p.length; j++) {
            if (p[j] < m) {
                i = j;
                m = p[j];
            }
        }
        return i;
    }

    /**
     * 判断两个 double 数组是否相等。
     * 长度一样且对应位置值相同返回真。
     *
     * @param a
     * @param b
     * @return
     */
    private static boolean equal(Double[] a, Double[] b) {
        if (a.length != b.length)
            return false;
        else {
            for (int i = 0; i < a.length; i++) {
                if (!a[i].equals(b[i]))
                    return false;
            }
        }
        return true;
    }
}
