package com.huntdreams.lab.module.gene.processor;

import com.huntdreams.lab.common.BaseProcessor;
import com.huntdreams.lab.module.gene.bean.Gene;
import com.huntdreams.lab.util.CSVFileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVProcessor
 * <p/>
 * CSVReader 类库使用说明
 * 有BUG,列读不完全
 * http://www.cnblogs.com/mbigger/archive/2013/01/04/2844423.html
 * <p/>
 * Apache-Commons CSV文件的读和写
 * http://www.what21.com/programming/java/apache/csv.html
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 2/28/16 10:19 PM.
 */
public class CSVProcessor extends BaseProcessor {
    private final String baseFile = docPath + "/ratio/records.csv";

    /**
     * 获得基因列表
     *
     * @return 基因列表
     * @throws IOException
     */
    private List<Gene> getGeneList() throws IOException {
        List<Gene> geneList = new ArrayList<Gene>();
        try {
            CSVFileUtil fileUtil = new CSVFileUtil(baseFile);
            String header = fileUtil.readLine();
            logger.debug(header);
            String[] headers = header.split(",");

            for (String head : headers) {
                if (head.length() < 5) {
                    continue;
                }
                logger.debug(head);
                Gene gene = new Gene();
                gene.setName(head);
                if (!geneList.contains(gene)) {
                    geneList.add(gene);
                }
            }
            // Just for debug
//            for (Gene gene : geneList) {
//                logger.debug(gene.getName());
//            }
//            logger.debug(geneList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return geneList;
    }

    private void process() {

    }

    public static void main(String[] args) throws IOException {
        CSVProcessor csvProcessor = new CSVProcessor();
        csvProcessor.getGeneList();
    }
}