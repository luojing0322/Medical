package com.huntdreams.lab.analyse.processor;

import com.huntdreams.lab.analyse.bean.Gene;
import com.huntdreams.lab.analyse.bean.SNP;
import com.huntdreams.lab.filter.Processor;
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
 * RatioProcessor
 * 计算每个基因患病的概率
 * 基因总数:38
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 2/28/16 2:41 PM.
 */
public class RatioProcessor extends Processor {

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


    public static void main(String[] args) {
        RatioProcessor ratioProcessor = new RatioProcessor();
        ratioProcessor.splitGenes();
    }
}