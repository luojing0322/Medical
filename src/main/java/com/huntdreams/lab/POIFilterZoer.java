package com.huntdreams.lab;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;

/**
 * 利用apache poi 处理excel
 *
 * @author tyee.noprom@qq.com
 * @time 2/18/16 2:49 PM.
 */
public class POIFilterZoer extends Processor {

    private final String baseFile = docPath + "/in_filter_00.xls";
    private final String outFile = docPath + "/out_filter_00.xls";
    private final String poiOutFile = docPath + "/poi_out.xls";

    private void process() {

    }

    private void demoWrite() {
        try{
            // 创建新的Excel 工作簿
            HSSFWorkbook workbook = new HSSFWorkbook();

            // 在Excel工作簿中建一工作表，其名为缺省值
            // 如要新建一名为"model"的工作表，其语句为：
            // HSSFSheet sheet = workbook.createSheet("model");
            HSSFSheet sheet = workbook.createSheet();
            // 在索引0的位置创建行（最顶端的行）
            HSSFRow row = sheet.createRow(100);
            // 在索引0的位置创建单元格（左上端）
            // 最多处理255列，太渣了!!!
            HSSFCell cell = row.createCell(300);

            // 定义单元格为字符串类型
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            // 在单元格中输入一些内容
            cell.setCellValue("增加值");
            // 新建一输出文件流
            FileOutputStream fileOut = new FileOutputStream(poiOutFile);
            // 把相应的Excel 工作簿存盘
            workbook.write(fileOut);
            fileOut.flush();
            // 操作结束，关闭文件
            fileOut.close();
            System.out.println("文件生成...");

        }catch(Exception e) {
            System.out.println("已运行 xlCreate() : " + e );
        }
    }

    public static void main(String[] args) {
        POIFilterZoer poiFilterZoer = new POIFilterZoer();
        poiFilterZoer.demoWrite();
    }
}
