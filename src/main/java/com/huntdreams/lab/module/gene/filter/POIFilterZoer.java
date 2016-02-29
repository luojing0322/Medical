package com.huntdreams.lab.module.gene.filter;

import com.huntdreams.lab.common.BaseProcessor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 利用apache poi 处理excel
 * <p/>
 * 使用案例:
 * http://www.cnblogs.com/hongten/p/java_poi_excel_xls_xlsx.html
 *
 * @author tyee.noprom@qq.com
 * @time 2/18/16 2:49 PM.
 */
public class POIFilterZoer extends BaseProcessor {

    private final String baseFile = docPath + "/ratio/read_by_poi.xlsx";
    private final String outFile = docPath + "/ratio/out_by_poi.xlsx";
    private final String poiOutFile = docPath + "/ratio/out_by_poi.xlsx";

    private void process() throws IOException {
        readXlsx(baseFile);
    }

    public void readXlsx(String path) throws IOException {
        InputStream is = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        // Read the Sheet
        for (int numSheet = 0, sheetCount = xssfWorkbook.getNumberOfSheets();
             numSheet < sheetCount; numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            // Read the Row
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {
                    for (int i = 0, rowCount = xssfRow.getPhysicalNumberOfCells();
                         i < rowCount; i++) {
                        XSSFCell cell = xssfRow.getCell(i);
                        logger.debug(getValue(cell));
                    }
                }
            }
        }
    }

    private String getValue(XSSFCell xssfRow) {
        if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
            return String.valueOf(xssfRow.getBooleanCellValue());
        } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
            return String.valueOf(xssfRow.getNumericCellValue());
        } else {
            return String.valueOf(xssfRow.getStringCellValue());
        }
    }


    public static void main(String[] args) throws IOException {
        POIFilterZoer poiFilterZoer = new POIFilterZoer();
        poiFilterZoer.process();
    }
}
