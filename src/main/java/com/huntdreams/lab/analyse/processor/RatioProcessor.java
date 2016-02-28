package com.huntdreams.lab.analyse.processor;

import com.huntdreams.lab.analyse.bean.Gene;
import com.huntdreams.lab.analyse.bean.SNP;
import com.huntdreams.lab.filter.Processor;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * RatioProcessor
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 2/28/16 2:41 PM.
 */
public class RatioProcessor extends Processor {

    private final String baseFile = docPath + "/ratio/cal_ratio.xls";
    //private final String outFile = docPath + "/ratio/out_filter_00.xls";

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
                    geneList.add(gene);
                }
            }
            return geneList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWb.close();
        }
        return geneList;
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

    public static void main(String[] args) {
        RatioProcessor ratioProcessor = new RatioProcessor();
        ArrayList<Gene> genes = ratioProcessor.getGeneList();
        ratioProcessor.printGeneList(genes);
    }
}