package trong.lixco.com.thai.baocao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.logging.Logger;

import jxl.CellType;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.jpa.entitykpi.KPIDepMonth;
import trong.lixco.com.jpa.entitykpi.KPIPerson;

@ManagedBean
@ViewScoped
public class Excel extends AbstractBean<KPIPerson> {
	private static final long serialVersionUID = 1L;

	@Override
	protected void initItem() {
		// TODO Auto-generated method stub

	}
	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static XSSFCellStyle createStyleForTitle(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }
	
	public void printExcel() throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("KQ KPI CA NHAN THANG");

		List<KPIPerson> listKPIPersonByMonth = new ArrayList<KPIPerson>();
		List<KPIDepMonth> listKPIDepMonthByMonth = new ArrayList<>();

		int rownum = 0;
		Cell cell;
		Row row;
		//
		XSSFCellStyle style = createStyleForTitle(workbook);

		row = sheet.createRow(rownum);

		// EmpNo
		cell = row.createCell(0);
		cell.setCellValue("Họ và tên");
		cell.setCellStyle(style);
		// EmpName
		cell = row.createCell(1);
		cell.setCellValue("Đơn vị");
		cell.setCellStyle(style);
		// Salary
		cell = row.createCell(2);
		cell.setCellValue("KPI Phòng");
		cell.setCellStyle(style);
		// Grade
		cell = row.createCell(3);
		cell.setCellValue("KPI cá nhân");
		cell.setCellStyle(style);
		// Bonus
		cell = row.createCell(4);
		cell.setCellValue("Tổng điểm");
		cell.setCellStyle(style);
		//xep loai
		cell = row.createCell(4);
		cell.setCellValue("Xếp loại");
		cell.setCellStyle(style);

		// Data
//		for (Employee emp : list) {
//			rownum++;
//			row = sheet.createRow(rownum);
//
//			// EmpNo (A) 
//			cell = row.createCell(0, CellType.STRING);
//			cell.setCellValue(emp.getEmpNo());
//			// EmpName (B)
//			cell = row.createCell(1, CellType.STRING);
//			cell.setCellValue(emp.getEmpName());
//			// Salary (C)
//			cell = row.createCell(2, CellType.NUMERIC);
//			cell.setCellValue(emp.getSalary());
//			// Grade (D)
//			cell = row.createCell(3, CellType.NUMERIC);
//			cell.setCellValue(emp.getGrade());
//			// Bonus (E)
//			String formula = "0.1*C" + (rownum + 1) + "*D" + (rownum + 1);
//			cell = row.createCell(4, CellType.FORMULA);
//			cell.setCellFormula(formula);
//		}
		File file = new File("D:/kpicanhan.xlsx");
		file.getParentFile().mkdirs();

		FileOutputStream outFile = new FileOutputStream(file);
		workbook.write(outFile);
		System.out.println("Created file: " + file.getAbsolutePath());

	}

	public void ReadExcel() throws IOException {
		// Đọc một file XSL.
		FileInputStream inputStream = new FileInputStream(new File("C:/demo/employee.xls"));

		// Đối tượng workbook cho file XSL.
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

		// Lấy ra sheet đầu tiên từ workbook
		XSSFSheet sheet = workbook.getSheetAt(0);

//		// Lấy ra Iterator cho tất cả các dòng của sheet hiện tại.
//		Iterator<Row> rowIterator = sheet.iterator();
//
//		while (rowIterator.hasNext()) {
//			Row row = rowIterator.next();
//
//			// Lấy Iterator cho tất cả các cell của dòng hiện tại.
//			Iterator<Cell> cellIterator = row.cellIterator();
//
//			while (cellIterator.hasNext()) {
//				Cell cell = cellIterator.next();
//
//				// Đổi thành getCellType() nếu sử dụng POI 4.x
//				CellType cellType = cell.getCellTypeEnum();
//
//				switch (cellType) {
//				case _NONE:
//					System.out.print("");
//					System.out.print("\t");
//					break;
//				case BOOLEAN:
//					System.out.print(cell.getBooleanCellValue());
//					System.out.print("\t");
//					break;
//				case BLANK:
//					System.out.print("");
//					System.out.print("\t");
//					break;
//				case FORMULA:
//
//					// Công thức
//					System.out.print(cell.getCellFormula());
//					System.out.print("\t");
//
//					FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
//
//					// In ra giá trị từ công thức
//					System.out.print(evaluator.evaluate(cell).getNumberValue());
//					break;
//				case NUMERIC:
//					System.out.print(cell.getNumericCellValue());
//					System.out.print("\t");
//					break;
//				case STRING:
//					System.out.print(cell.getStringCellValue());
//					System.out.print("\t");
//					break;
//				case ERROR:
//					System.out.print("!");
//					System.out.print("\t");
//					break;
//				}
//
//			}
//			System.out.println("");
//		}
	}
}
