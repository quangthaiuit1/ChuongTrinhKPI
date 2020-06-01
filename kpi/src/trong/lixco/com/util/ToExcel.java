package trong.lixco.com.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.UploadedFile;

import trong.lixco.com.jpa.entity360.CauHoi;
import trong.lixco.com.jpa.entity360.ChiTietDanhGia;
import trong.lixco.com.jpa.entity360.PhongTrao;
import trong.lixco.com.jpa.entity360.PhongTraoPhong;
import trong.lixco.com.jpa.entity360.ViPham;

public class ToExcel {
	public static List<PhongTraoPhong> docexcelthamgiaphongtraophong(UploadedFile uploadedFile) {
		try {
			List<PhongTraoPhong> phongTraoPhongs = new ArrayList<PhongTraoPhong>();
			InputStream input = uploadedFile.getInputstream();
			// Get the workbook instance for XLS file
			XSSFWorkbook workbook = new XSSFWorkbook(input);
			// Get first sheet from the workbook
			for (int p = 0; p < workbook.getNumberOfSheets(); p++) {
				Sheet sheet = workbook.getSheetAt(p);

				// Iterate through each rows from first sheet
				Row row;
				int tam = -1;
				// Chay danh sach tu 1 -> dong cuoi cung
				int numrow = sheet.getLastRowNum();
				String manpp = "";
				for (int i = 0; i <= numrow; i++) {
					row = sheet.getRow(i);
					if (tam == -1) {
						// Vi tri bat dau la " Ngay"
						for (int j = 0; j <= numrow; j++) {
							try {
								if (row.getCell(j).getStringCellValue().equalsIgnoreCase("STT")) {
									tam = j;
									break;
								}
							} catch (Exception e) {
							}
						}
					} else {
						// Bat dau lay gia tri
						int STT = 0;
						String maphong = "", tenphong = ""; double solanthamgia=0.0;
						try {
							STT = (int) row.getCell(tam).getNumericCellValue();
						} catch (Exception e) {
						}
						try {
							maphong = row.getCell(tam + 1).getStringCellValue();
						} catch (Exception e) {
						}
						try {
							tenphong = row.getCell(tam + 2).getStringCellValue();
						} catch (Exception e) {
						}
						try {
							solanthamgia = row.getCell(tam+3).getNumericCellValue();
						} catch (Exception e) {
						}
						PhongTraoPhong ct = new PhongTraoPhong();
						ct.setMaphong(maphong);
						ct.setTenphong(tenphong);
						ct.setSodiem(solanthamgia);
						phongTraoPhongs.add(ct);

					}
				}
			}
			input.close();
			return phongTraoPhongs;
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<PhongTraoPhong>();
		}
	}
	public static List<PhongTrao> docexcelthamgiaphongtrao(UploadedFile uploadedFile) {
		try {
			List<PhongTrao> phongTraos = new ArrayList<PhongTrao>();
			InputStream input = uploadedFile.getInputstream();
			// Get the workbook instance for XLS file
			XSSFWorkbook workbook = new XSSFWorkbook(input);
			// Get first sheet from the workbook
			for (int p = 0; p < workbook.getNumberOfSheets(); p++) {
				Sheet sheet = workbook.getSheetAt(p);

				// Iterate through each rows from first sheet
				Row row;
				int tam = -1;
				// Chay danh sach tu 1 -> dong cuoi cung
				int numrow = sheet.getLastRowNum();
				String manpp = "";
				for (int i = 0; i <= numrow; i++) {
					row = sheet.getRow(i);
					if (tam == -1) {
						// Vi tri bat dau la " Ngay"
						for (int j = 0; j <= numrow; j++) {
							try {
								if (row.getCell(j).getStringCellValue().equalsIgnoreCase("STT")) {
									tam = j;
									break;
								}
							} catch (Exception e) {
							}
						}
					} else {
						// Bat dau lay gia tri
						int STT = 0;
						String manvdanhgia = "", tennvdanhgia = ""; double solanthamgia=0.0;
						try {
							STT = (int) row.getCell(tam).getNumericCellValue();
						} catch (Exception e) {
						}
						try {
							manvdanhgia = row.getCell(tam + 1).getStringCellValue();
						} catch (Exception e) {
						}
						try {
							tennvdanhgia = row.getCell(tam + 2).getStringCellValue();
						} catch (Exception e) {
						}
						try {
							solanthamgia = Math.round(row.getCell(tam+3).getNumericCellValue()*10.0)/10.0;
						} catch (Exception e) {
						}
						PhongTrao ct = new PhongTrao();
						ct.setManhanvien(manvdanhgia);
						ct.setTennhanvien(tennvdanhgia);
						ct.setSolanthamgia(solanthamgia);
						phongTraos.add(ct);

					}
				}
			}
			input.close();
			return phongTraos;
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<PhongTrao>();
		}
	}
	public static List<ViPham> docexcelvipham(UploadedFile uploadedFile) {
		try {
			List<ViPham> viphams = new ArrayList<ViPham>();
			InputStream input = uploadedFile.getInputstream();
			// Get the workbook instance for XLS file
			XSSFWorkbook workbook = new XSSFWorkbook(input);
			// Get first sheet from the workbook
			for (int p = 0; p < workbook.getNumberOfSheets(); p++) {
				Sheet sheet = workbook.getSheetAt(p);

				// Iterate through each rows from first sheet
				Row row;
				int tam = -1;
				// Chay danh sach tu 1 -> dong cuoi cung
				int numrow = sheet.getLastRowNum();
				String manpp = "";
				for (int i = 0; i <= numrow; i++) {
					row = sheet.getRow(i);
					if (tam == -1) {
						// Vi tri bat dau la " Ngay"
						for (int j = 0; j <= numrow; j++) {
							try {
								if (row.getCell(j).getStringCellValue().equalsIgnoreCase("STT")) {
									tam = j;
									break;
								}
							} catch (Exception e) {
							}
						}
					} else {
						// Bat dau lay gia tri
						int STT = 0;
						String manvdanhgia = "", tennvdanhgia = ""; double solanvipham=0;
						try {
							STT = (int) row.getCell(tam).getNumericCellValue();
						} catch (Exception e) {
						}
						try {
							manvdanhgia = row.getCell(tam + 1).getStringCellValue();
						} catch (Exception e) {
						}
						try {
							tennvdanhgia = row.getCell(tam + 2).getStringCellValue();
						} catch (Exception e) {
						}
						try {
							solanvipham =  row.getCell(tam+3).getNumericCellValue();
						} catch (Exception e) {
						}
						ViPham ct = new ViPham();
						ct.setManhanvien(manvdanhgia);
						ct.setTennhanvien(tennvdanhgia);
						ct.setSolanvipham(solanvipham);
						viphams.add(ct);

					}
				}
			}
			input.close();
			return viphams;
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<ViPham>();
		}
	}
	public static List<ChiTietDanhGia> docexcelcaidat(UploadedFile uploadedFile) {
		try {
			List<ChiTietDanhGia> chiTietDanhGias = new ArrayList<ChiTietDanhGia>();
			InputStream input = uploadedFile.getInputstream();
			// Get the workbook instance for XLS file
			XSSFWorkbook workbook = new XSSFWorkbook(input);
			// Get first sheet from the workbook
			for (int p = 0; p < workbook.getNumberOfSheets(); p++) {
				Sheet sheet = workbook.getSheetAt(p);

				// Iterate through each rows from first sheet
				Row row;
				int tam = -1;
				// Chay danh sach tu 1 -> dong cuoi cung
				int numrow = sheet.getLastRowNum();
				String manpp = "";
				for (int i = 0; i <= numrow; i++) {
					row = sheet.getRow(i);
					if (tam == -1) {
						// Vi tri bat dau la " Ngay"
						for (int j = 0; j <= numrow; j++) {
							try {
								if (row.getCell(j).getStringCellValue().equalsIgnoreCase("STT")) {
									tam = j;
									break;
								}
							} catch (Exception e) {
							}
						}
					} else {
						// Bat dau lay gia tri
						int STT = 0;
						String manvdanhgia = "", tennvdanhgia = "", nv1dg = "", nv2dg = "", nv3dg = "", nv4dg = "", nv5dg = "", nv6dg = "";
						try {
							STT = (int) row.getCell(tam).getNumericCellValue();
						} catch (Exception e) {
						}
						try {
							manvdanhgia = row.getCell(tam + 1).getStringCellValue();
						} catch (Exception e) {
						}
						try {
							tennvdanhgia = row.getCell(tam + 2).getStringCellValue();
						} catch (Exception e) {
						}
						try {
							nv1dg = row.getCell(tam + 3).getStringCellValue();
						} catch (Exception e) {
						}
						try {
							nv2dg = row.getCell(tam + 4).getStringCellValue();
						} catch (Exception e) {
						}
						try {
							nv3dg = row.getCell(tam + 5).getStringCellValue();
						} catch (Exception e) {
						}
						try {
							nv4dg = row.getCell(tam + 6).getStringCellValue();
						} catch (Exception e) {
						}
						try {
							nv5dg = row.getCell(tam + 7).getStringCellValue();
						} catch (Exception e) {
						}
						try {
							nv6dg = row.getCell(tam + 8).getStringCellValue();
						} catch (Exception e) {
						}
						ChiTietDanhGia ct = new ChiTietDanhGia();
						ct.setManhanvien(manvdanhgia);
						ct.setTennhanvien(tennvdanhgia);
						ct.setNv1(nv1dg);
						ct.setNv2(nv2dg);
						ct.setNv3(nv3dg);
						ct.setNv4(nv4dg);
						ct.setNv5(nv5dg);
						ct.setNv6(nv6dg);
						chiTietDanhGias.add(ct);

					}
				}
			}
			input.close();
			return chiTietDanhGias;
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<ChiTietDanhGia>();
		}
	}

	public static List<CauHoi> docexcelcauhoi(UploadedFile uploadedFile) {
		try {
			List<CauHoi> cauhois = new ArrayList<CauHoi>();
			InputStream input = uploadedFile.getInputstream();
			// Get the workbook instance for XLS file
			XSSFWorkbook workbook = new XSSFWorkbook(input);
			// Get first sheet from the workbook
			for (int p = 0; p < workbook.getNumberOfSheets(); p++) {
				Sheet sheet = workbook.getSheetAt(p);

				// Iterate through each rows from first sheet
				Row row;
				int tam = -1;
				// Chay danh sach tu 1 -> dong cuoi cung
				int numrow = sheet.getLastRowNum();
				String manpp = "";
				for (int i = 0; i <= numrow; i++) {
					row = sheet.getRow(i);
					if (tam == -1) {
						// Vi tri bat dau la " Ngay"
						for (int j = 0; j <= numrow; j++) {
							try {
								if (row.getCell(j).getStringCellValue().equalsIgnoreCase("STT")) {
									tam = j;
									break;
								}
							} catch (Exception e) {
							}
						}
					} else {
						// Bat dau lay gia tri
						int STT = 0;
						int nhomcauhoi = 1;
						String ndcauhoi = "";
						try {
							STT = (int) row.getCell(tam).getNumericCellValue();
						} catch (Exception e) {
						}
						try {
							nhomcauhoi = (int) row.getCell(tam + 1).getNumericCellValue();
						} catch (Exception e) {
						}
						try {
							ndcauhoi = row.getCell(tam + 2).getStringCellValue();
						} catch (Exception e) {
						}

						CauHoi cauHoi = new CauHoi();
						cauHoi.setMacauhoi(STT);
						cauHoi.setNhomcauhoi(nhomcauhoi);
						cauHoi.setNoidung(ndcauhoi);
						cauhois.add(cauHoi);
					}
				}
			}
			input.close();
			return cauhois;
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<CauHoi>();
		}
	}

	public static void writeXLS(List<Object[]> params, ServletOutputStream svl) throws IOException {
		// Create blank workbook
		HSSFWorkbook workbook = new HSSFWorkbook();
		// Create a blank sheet
		HSSFSheet spreadsheet = workbook.createSheet("Sheet1");

		// Create row object
		Row row;
		// This data needs to be written (Object[])
		Map<String, Object[]> empinfo = new LinkedHashMap<String, Object[]>();
		for (int i = 0; i < params.size(); i++) {
			empinfo.put((i + 1) + "", params.get(i));
		}
		// Iterate over data and write to sheet
		Set<String> keyid = empinfo.keySet();
		int rowid = 0;
		SimpleDateFormat fd = new SimpleDateFormat("dd-MM-yyyy");
		for (String key : keyid) {
			row = spreadsheet.createRow(rowid++);
			Object[] objectArr = empinfo.get(key);
			int cellid = 0;
			for (Object obj : objectArr) {
				Cell cell = row.createCell(cellid++);
				cell.setCellValue(obj + "");
				if (obj != null && obj.getClass().equals(Double.class)) {
					cell.setCellValue((double) obj);
				} else if (obj != null && obj.getClass().equals(Date.class)) {

					cell.setCellValue(fd.format((Date) obj));
				} else {
					cell.setCellValue(obj + "");
				}
			}
		}

		// Write the workbook in file system
		workbook.write(svl);
	}

	public static void writeXLS2(List<Object[]> params, ServletOutputStream svl) throws IOException {
		// Create blank workbook
		HSSFWorkbook workbook = new HSSFWorkbook();
		// Create a blank sheet
		HSSFSheet spreadsheet = workbook.createSheet("Sheet1");
		// Create row object
		Row row;
		SimpleDateFormat fd = new SimpleDateFormat("dd-MM-yyyy");
		for (int i = 0; i < params.size(); i++) {
			row = spreadsheet.createRow(i + 1);
			for (int j = 0; j < params.get(i).length; j++) {
				Cell cell = row.createCell(j + 1);
				Object obj = params.get(i)[j];
				if (obj != null && obj.getClass().equals(Double.class)) {
					cell.setCellValue((double) obj);
				} else if (obj != null && obj.getClass().equals(Date.class)) {

					cell.setCellValue(fd.format((Date) obj));
				} else {
					cell.setCellValue(obj + "");
				}
			}
		}

		// Write the workbook in file system
		workbook.write(svl);
	}

	public static double formatNumber(int sole, double value) {
		BigDecimal bd = new BigDecimal(value);
		BigDecimal bd2 = bd.setScale(sole, BigDecimal.ROUND_HALF_UP);

		return bd2.doubleValue();
	}

	public static double formatNumber(double value) {
		BigDecimal bd = new BigDecimal(value);
		BigDecimal bd2 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);

		return bd2.doubleValue();
	}

	public static void main(String[] args) {
		System.out.println(12345 + 54321l);
	}

}
