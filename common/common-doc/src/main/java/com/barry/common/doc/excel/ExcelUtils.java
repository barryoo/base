package com.barry.common.doc.excel;

import com.barry.common.core.constants.SymbolConst;
import com.barry.common.mvc.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

/**
 * Excel工具类，用于将Workbook对象转换为输出对象
 *
 * @author Zhong.Chengzhe
 */
@Slf4j
public final class ExcelUtils {

    private ExcelUtils(){}

    private static final String FIELD_NAME = "fieldName";
    private static final int SIZE_THRESHOLD = 16;
    private static final String MS_EXCEL = "application/vnd.ms-excel";

    /**
     * 将Workbook对象直接写入某个路径的文件
     *
     * @param workbook workbook
     * @param pathName file path + file name
     * @return File, 路径不存在则返回空
     */
    public static File exportFile(Workbook workbook, String pathName) {
        File file = null;
        FileOutputStream os = null;
        try {
            file = new File(pathName);
            os = new FileOutputStream(file);
            workbook.write(os);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                os.close();
                workbook.close();
            } catch (IOException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return file;
    }

    /**
     * 用于上传文件到文件系统，将Workbook对象保存为CommonsMultipartFile对象
     *
     * @param workbook workbook
     * @param fileName 生成的文件名
     * @return CommonsMultipartFile，生成失败返回空
     */
    public static CommonsMultipartFile exportMultipartFile(Workbook workbook, String fileName) {
        CommonsMultipartFile multipartFile = null;
        try {
            //DiskFileItemFactory()：构造一个配置好的该类的实例
            //第一个参数threshold(阈值)：以字节为单位.在该阈值之下的item会被存储在内存中，在该阈值之上的item会被当做文件存储
            //第二个参数data repository：将在其中创建文件的目录.用于配置在创建文件项目时，当文件项目大于临界值时使用的临时文件夹，默认采用系统默认的临时文件路径
            FileItemFactory factory = new DiskFileItemFactory(SIZE_THRESHOLD, null);
            //fieldName：表单字段的名称；第二个参数 ContentType；第三个参数isFormField；第四个：文件名
            FileItem item = factory.createItem(FIELD_NAME, MediaType.TEXT_PLAIN_VALUE, true, fileName);
            workbook.write(item.getOutputStream());
            multipartFile = new CommonsMultipartFile(item);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return multipartFile;
    }

    /**
     * 用于直接下载一个Excel文件，将Workbook对象直接写入HttpServletResponse
     *
     * @param workbook workbook
     * @param response HttpServletResponse
     * @param fileName 生成的文件名
     * @return HttpServletResponse，写入失败则返回原response对象
     */
    public static HttpServletResponse download(Workbook workbook, HttpServletResponse response, String fileName) {
        try {
            setHeaderForExcel(response, fileName);
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return response;
    }

    /**
     * 为Response设置Excel下载的Header
     */
    public static void setHeaderForExcel(HttpServletResponse response, String fileName) {
        ServletUtils.setFileStreamHeader(response, fileName);
        response.setHeader(CONTENT_TYPE, MS_EXCEL);
    }

    /**
     * 创建指定的Sheet表名称及表头,返回Sheet表对象
     *
     * @param workbook          workbook
     * @param sheetName         Sheet表名称
     * @param headerCellContent 表头列属性
     * @return 返回创建好后的Sheet表对象
     */
    public static Sheet setSheetAndHeader(Workbook workbook, String sheetName, String[] headerCellContent) {
        Sheet sheet = isExistSheet(workbook, sheetName);
        CellStyle cellStyle = workbook.createCellStyle();
        Row headerRow = isExistRow(sheet, 0);
        List<Object> headerRowDatas = new ArrayList<>();
        for (String headCellValue : headerCellContent) {
            headerRowDatas.add(headCellValue);
        }
        setRowValue(headerRow, headerRowDatas, cellStyle);
        return sheet;
    }

    /**
     * 设置从给定Sheet表、给定开始行批量新增、更新内容, 返回结束行下标索引
     *
     * @param sheet         指定Sheet表
     * @param startRowIndex 新增、更新开始行索引号
     * @param rowsDatas     行内容数据
     * @return 返回Sheet表更新、新增后的行号
     */
    public static int setGivenRowDatas(Workbook workbook, Sheet sheet, int startRowIndex, List<List<Object>> rowsDatas) {
        CellStyle cellStyle = workbook.createCellStyle();
        int rowSize = rowsDatas.size();
        int endIndex = startRowIndex + rowSize;
        for (int i = 0; i < rowSize; i++) {
            Row row = isExistRow(sheet, startRowIndex + i);
            setRowValue(row, rowsDatas.get(i), cellStyle);
        }
        return endIndex;
    }


    /**
     * 设置一行的数据,返回设置后的行对象
     *
     * @param row     行对象
     * @param rowData 行数据
     * @return 设置后的行对象
     */
    public static Row setRowValue(Row row, List<Object> rowData, CellStyle cellStyle) {
        for (int i = 0; i < rowData.size(); i++) {
            Cell cell = isExistCell(row, i);
            setCellValue(cell, rowData.get(i), cellStyle);
        }
        return row;
    }

    /**
     * 设置单元格值
     *
     * @param cell  需要设置的单元格
     * @param value 设置给单元格cell的值
     * @return 设置好的单元格列对象
     */
    public static Cell setCellValue(Cell cell, Object value, CellStyle cellStyle) {
        if (Objects.isNull(value)) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Integer) {
            // Integer 设置数值型整数
            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(Double.valueOf(String.valueOf(value)));
        } else if (value instanceof Double || (value instanceof BigDecimal && !String.valueOf(value).contains(SymbolConst.PERCENT))) {
            // Double BigDecimal保留两位小数点
            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(Double.valueOf(String.valueOf(value)));
        } else {
            cell.setCellValue(String.valueOf(value));
        }
        return cell;
    }

    /**
     * 验证指定Sheet表名称是否存在,存在则返回原Sheet表对象,不存在则创建Sheet表对象后返回
     *
     * @param workbook  workbook
     * @param sheetName 需要验证的Sheet表名称
     * @return Sheet
     */
    public static Sheet isExistSheet(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return workbook.createSheet(sheetName);
        } else {
            return sheet;
        }
    }

    /**
     * 验证指定Sheet表下的下标索引行是否存在,存在则返回原行对象,不存在则创建Row对象后返回
     *
     * @param sheet    指定的Sheet对象
     * @param rowIndex 指定的下标索引行号
     * @return Row
     */
    public static Row isExistRow(Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return sheet.createRow(rowIndex);
        } else {
            return row;
        }
    }

    /**
     * 验证指定Row行的单元格下标索引是否存在,存在则返回原单元格对象,不存在则创建单元格对象后返回
     *
     * @param row       指定的Row行对象
     * @param cellIndex 指定的下标索引单元格列号
     * @return Cell
     */
    public static Cell isExistCell(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return row.createCell(cellIndex);
        } else {
            return cell;
        }
    }

}
