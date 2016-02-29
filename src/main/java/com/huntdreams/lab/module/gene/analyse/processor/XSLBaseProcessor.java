package com.huntdreams.lab.module.gene.analyse.processor;

import com.huntdreams.lab.common.BaseProcessor;
import com.huntdreams.lab.module.gene.analyse.bean.Gene;
import com.huntdreams.lab.module.gene.analyse.bean.Record;
import com.huntdreams.lab.module.gene.analyse.bean.SNP;
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

/**
 * XSLBaseProcessor
 * 计算每个基因患病的概率
 * 基因总数:38
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 2/28/16 2:41 PM.
 */
public class XSLBaseProcessor extends BaseProcessor {

    private final String baseFile = docPath + "/ratio/cal_ratio.xls";
    private final String outFile = docPath + "/ratio/out_cal_ratio.xls";

    private ArrayList<Gene> geneList;

    /**
     * 获得基因以及对应的SNP位点
     *
     * @return 基因列表
     */
    private ArrayList<Gene> getGeneList() {
        Workbook readWb = null;
        ArrayList<Gene> geneList = new ArrayList<Gene>();
        try {
            // 构建Workbook对象, 只读Workbook对象
            // 直接从本地文件创建Workbook
            InputStream readInputStream = new FileInputStream(baseFile);
            readWb = Workbook.getWorkbook(readInputStream);
            Integer sheetIndex = 1;
            // 获取读Sheet表
            Sheet readwbSheet = readWb.getSheet(sheetIndex);
            String sheetName = readwbSheet.getName();
            // 获取Sheet表中所包含的总列数
            int rsColumns = readwbSheet.getColumns();
            // 获取Sheet表中所包含的总行数
            int rsRows = readwbSheet.getRows();

            // 计算所有基因
            for (int i = 1; i < rsRows; i++) {
                Cell cell = readwbSheet.getCell(1, i);
                String value = cell.getContents();
                Gene gene = new Gene();
                gene.setName(value);

                if (!geneList.contains(gene)) {//该基因还不在list里面
                    ArrayList<SNP> snpList = new ArrayList<SNP>();
                    // 将该基因的所有位点加进去
                    for (int k = i; k < rsRows; k++) {
                        Cell geneCell = readwbSheet.getCell(1, k);
                        Cell snpCell = readwbSheet.getCell(2, k);
                        String geneValue = geneCell.getContents();
                        String snpValue = snpCell.getContents();
                        if (!geneValue.equals(value)) {
                            i = k - 1;
                            break;
                        }
                        // 属于同一个基因
                        SNP snp = new SNP();
                        snp.setName(snpValue);
                        snpList.add(snp);
                    }
                    gene.setSnpList(snpList);
                    gene.setSnpCount(snpList.size());
                    geneList.add(gene);
                }
            }
            return geneList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWb.close();
        }
        this.geneList = geneList;
        return geneList;
    }

    /**
     * 根据基因名获得基因
     *
     * @param name 基因名
     * @return 目标基因
     */
    private Gene getGeneByName(String name) {
        ArrayList<Gene> geneList = getGeneList();
        Gene objGene = null;
        for (Gene gene : geneList) {
            if (gene.getName().equals(name)) {
                objGene = gene;
                break;
            }
        }
        return objGene;
    }

    /**
     * 打印基因列表
     *
     * @param geneList 基因列表
     */
    private void printGeneList(ArrayList<Gene> geneList) {
        for (Gene gene : geneList) {
            ArrayList<SNP> snps = gene.getSnpList();
            for (SNP snp : snps) {
                logger.debug("gene: " + gene.getName() + ", snp: " + snp.getName());
            }
        }
    }

    /**
     * 将每个基因单独列一个sheet
     * 使其分离
     */
    private void splitGenes() {
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
            int geneCnt = 0;
            for (int sheetIndex = 6, sheetSize = readSheetList.length;
                 sheetIndex < sheetSize; sheetIndex++) {
                //获取读Sheet表
                Sheet readwbSheet = readWb.getSheet(sheetIndex);
                String sheetName = readwbSheet.getName();
                logger.debug("sheetName = " + sheetName);

                //获取Sheet表中所包含的总列数
                int rsColumns = readwbSheet.getColumns();
                //获取Sheet表中所包含的总行数
                int rsRows = readwbSheet.getRows();
                int colIndex = 0;
                while (colIndex < rsColumns) {
                    //创建工作表
                    WritableSheet ws = wwb.createSheet(sheetName, geneCnt);
                    String geneName = readwbSheet.getCell(colIndex, 0).getContents();
                    Gene gene = getGeneByName(geneName);
                    for (int i = 0; i < rsRows; i++) {
                        for (int j = colIndex; j < colIndex + gene.getSnpCount(); j++) {
                            String snpVal = readwbSheet.getCell(j, i).getContents();
                            Label label = new Label(j, i, snpVal);
                            ws.addCell(label);
                        }
                    }
                    colIndex += gene.getSnpCount();
                    geneCnt++;
                }
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
     * 获得记录列表
     *
     * @return 记录列表
     */
    private ArrayList<Record> getRecordList() {
        ArrayList<Record> recordList = new ArrayList<Record>();

        Workbook readWb = null;
        try {
            //构建Workbook对象, 只读Workbook对象
            //直接从本地文件创建Workbook
            InputStream readInputStream = new FileInputStream(baseFile);
            readWb = Workbook.getWorkbook(readInputStream);
            //利用已经创建的Excel工作薄,创建新的可写入的Excel工作薄
            Sheet[] readSheetList = readWb.getSheets();

            Sheet firstSheet = readWb.getSheet(0);
            for (int row = 0, rowSize = firstSheet.getRows(); row < rowSize; row++) {
                Record record = new Record();
                // 是否患病
                boolean ill = firstSheet.getCell(1, row).getContents().equals("病例") ? true : false;
                record.setIll(ill);
                ArrayList<Gene> recordGeneList = new ArrayList<Gene>();
                for (int sheetIndex = 6, sheetSize = readSheetList.length;
                     sheetIndex < sheetSize; sheetIndex++) {
                    //获取读Sheet表
                    Sheet readSheet = readWb.getSheet(sheetIndex);
                    String sheetName = readSheet.getName();
                    logger.debug("sheetName = " + sheetName);

                    //获取Sheet表中所包含的总列数
                    int rsColumns = readSheet.getColumns();
                    //获取Sheet表中所包含的总行数
                    int rsRows = readSheet.getRows();
                    int colIndex = 0;
                    while (colIndex < rsColumns - 1) {
                        String geneName = readSheet.getCell(colIndex, 0).getContents();
                        Gene gene = getGeneByName(geneName);
                        logger.debug(gene);
                        if (gene != null) {
                            ArrayList<SNP> snps = new ArrayList<SNP>();
                            for (int j = colIndex; j < colIndex + gene.getSnpCount(); j++) {
                                SNP snp = new SNP();
                                String snpName = readSheet.getCell(j, 1).getContents();
                                String snpVal = readSheet.getCell(j, row).getContents();
                                snp.setName(snpName);
                                snp.setValue(snpVal);
                                snps.add(snp);
                            }
                            gene.setSnpList(snps);
                            recordGeneList.add(gene);
                        }
                        colIndex += gene.getSnpCount();
                    }
                }
                record.setGeneList(recordGeneList);
                recordList.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWb.close();
        }
        return recordList;
    }

    /**
     * 打印记录
     */
    private void printRecordList() {
        ArrayList<Record> records = getRecordList();
        for (Record record : records) {
            for (Gene gene : record.getGeneList()) {
                for (SNP snp : gene.getSnpList()) {
                    logger.debug((record.getIll() ? "患病" : "对照") + gene.getName() + snp.getValue());
                }
            }
        }
    }

    /**
     * 打印患病情况
     */
    private void printCondition() {
        Workbook readWb = null;
        try {
            //构建Workbook对象, 只读Workbook对象
            //直接从本地文件创建Workbook
            InputStream readInputStream = new FileInputStream(baseFile);
            readWb = Workbook.getWorkbook(readInputStream);
            //Sheet的下标是从0开始
            //利用已经创建的Excel工作薄,创建新的可写入的Excel工作薄
            WritableWorkbook wwb = Workbook.createWorkbook(new FileOutputStream(new File(outFile)));
            //获取读Sheet表
            Sheet readwbSheet = readWb.getSheet(0);
            String sheetName = readwbSheet.getName();
            logger.debug("sheetName = " + sheetName);

            //获取Sheet表中所包含的总列数
            int rsColumns = readwbSheet.getColumns();
            //获取Sheet表中所包含的总行数
            int rsRows = readwbSheet.getRows();
            //创建工作表
            WritableSheet ws = wwb.createSheet(sheetName, 0);
            for (int i = 0; i < rsRows; i++) {
                String snpVal = readwbSheet.getCell(1, i).getContents();
                String ill = snpVal.equals("病例") ? "1" : "0";
                Label label = new Label(0, i, ill);
                String sexVal = readwbSheet.getCell(3, i).getContents();
                String sex = sexVal.equals("男") ? "1" : "0";
                Label sexLabel = new Label(1, i, sex);
                ws.addCell(label);
                ws.addCell(sexLabel);
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


    public static void main(String[] args) {
        XSLBaseProcessor XSLProcessor = new XSLBaseProcessor();
        XSLProcessor.printCondition();
    }
}