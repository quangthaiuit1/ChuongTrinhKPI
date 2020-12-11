package trong.lixco.com.thai.mail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ibm.icu.util.Calendar;

import trong.lixco.com.jpa.thai.EmployeeThai;
import trong.lixco.com.jpa.thai.PositionDontKPI;
import trong.lixco.com.servicepublic.EmployeeDTO;
import trong.lixco.com.thai.apitrong.DepartmentData;
import trong.lixco.com.thai.apitrong.DepartmentDataService;
import trong.lixco.com.thai.apitrong.EmployeeData;
import trong.lixco.com.thai.apitrong.EmployeeDataService;
import trong.lixco.com.thai.bean.SendMailBean;
import trong.lixco.com.thai.bean.entities.PositionTemp;

public class JobSendMail implements Job {
	Connection con = null;
	PreparedStatement preStatement = null;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// System.out.println("Run my Job. Thai Test ne:" + new Date());
		// String queryLink = "SELECT * FROM dulieutrungtam";
		DepartmentData[] departmentHCMArray = DepartmentDataService.timtheophongquanly("20002");
		List<DepartmentData> departmentHCM = new ArrayList<>(Arrays.asList(departmentHCMArray));
		// HANDLE -> Tao list id
		// department
		List<Long> listDepartmentCodeHCM = new ArrayList<Long>();
		for (DepartmentData de : departmentHCM) {
			listDepartmentCodeHCM.add(de.getId());
		}

		StringBuilder builder = new StringBuilder();
		for (DepartmentData d : departmentHCM) {
			builder.append(d.getCode());
			builder.append(",");
		}
		String s = "";
		if (builder.toString().endsWith(",")) {
			s = builder.toString().substring(0, builder.toString().length() - 1);
		}

		builder = new StringBuilder();
		EmployeeData[] empsList1 = EmployeeDataService.timtheophongbancomail(s);
		List<String> list = new ArrayList<>();
		for (EmployeeData de : empsList1) {
			list.add(de.getCode());
		}
		builder = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			builder.append("?,");
		}

		// query nhan vien tu du lieu
		// trung tam
		String queryEmployee = "SELECT e.id ,emp.code as employee_code, emp.name as employee_name, p.code as position_code "
				+ "FROM dulieutrungtam.emppjob AS e "
				+ "INNER JOIN dulieutrungtam.employee AS emp ON e.employee_id = emp.id "
				+ "INNER JOIN dulieutrungtam.positionjob AS p ON p.id = e.positionJob_id " + "WHERE emp.code IN ("
				+ builder.deleteCharAt(builder.length() - 1).toString() + ");";
		List<EmployeeThai> allEmpPosi = new ArrayList<>();
		try {
			con = SendMailBean.getConnectionMySQL("remote", "Voquangthai1901",
					"jdbc:mysql://192.168.0.132:3306/kpi?useUnicode=yes&characterEncoding=UTF-8");
			// con = SendMailBean.getConnectionMySQL("remote", "remote2013",
			// "jdbc:mysql://192.168.0.5:3306/dulieutrungtam?useUnicode=yes&characterEncoding=UTF-8");
			preStatement = con.prepareStatement(queryEmployee);
			// handle param IN operator
			int index = 1;
			for (String id : list) {
				preStatement.setObject(index++, id);
			}
			ResultSet resultSet = preStatement.executeQuery();
			while (resultSet.next()) {
				EmployeeThai empThai = new EmployeeThai(resultSet.getLong("e.id"), resultSet.getString("employee_code"),
						resultSet.getString("employee_name"), resultSet.getString("position_code"));
				allEmpPosi.add(empThai);
			}

			// System.out.println(allEmpPosi.size());
			// handle list chuc danh khong lam kpi
			List<PositionTemp> listNonKPI = new ArrayList<>();
			// query bang chuc vu khong lam kpi
			String queryNonKpi = "SELECT pnon.id, p.code AS position_code FROM kpi.position_dont_kpi AS pnon INNER JOIN kpi.positionjob AS p ON pnon.positionjob_id = p.id;";
			preStatement = con.prepareStatement(queryNonKpi);
			resultSet = preStatement.executeQuery();
			while (resultSet.next()) {
				PositionTemp positionNonKPI = new PositionTemp(resultSet.getLong("pnon.id"),
						resultSet.getString("position_code"));
				listNonKPI.add(positionNonKPI);
			}
			// list nhan vien moi gom nhan vien co chuc danh
			List<EmployeeData> emplsFiltered = new ArrayList<>();
			for (int i = 0; i < empsList1.length; i++) {
				boolean isExist = false;
				int indexJ = 0;
				for (int j = 0; j < allEmpPosi.size(); j++) {
					if (empsList1[i].getCode().equals(allEmpPosi.get(j).getCode())) {
						isExist = true;
						indexJ = j;
						break;
					}
				}
				if (isExist) {
					empsList1[i].setCateJobCode(allEmpPosi.get(indexJ).getPositionCode());
					emplsFiltered.add(empsList1[i]);
				}
			}
			// danh sach sau khi loc nhung nguoi co chuc danh va nhung nguoi
			// khong lam kpi
			List<EmployeeData> emplsEnd = new ArrayList<>();
			for (int i = 0; i < emplsFiltered.size(); i++) {
				boolean isExist = false;
				for (int j = 0; j < listNonKPI.size(); j++) {
					if (emplsFiltered.get(i).getCateJobCode().equals(listNonKPI.get(j).getCode())) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					emplsEnd.add(emplsFiltered.get(i));
				}
			}
			// Build query nhung nguoi chua dang ki kpi
			List<EmployeeThai> emplsDaDangKyKPI = new ArrayList<>();
			List<EmployeeData> emplsChuaDangKyKPI = new ArrayList<>();
			List<String> emplsCodeEnd = new ArrayList<>();
			for (EmployeeData de : emplsEnd) {
				emplsCodeEnd.add(de.getCode());
			}
			builder = new StringBuilder();
			for (int i = 0; i < emplsCodeEnd.size(); i++) {
				builder.append("?,");
			}
			queryEmployee = "SELECT * FROM kpi.kpiperson AS k WHERE k.kmonth = ? AND k.kyear = ? AND k.codeEmp IN ("
					+ builder.deleteCharAt(builder.length() - 1).toString() + ");";
			preStatement = con.prepareStatement(queryEmployee);
			Date date = new Date();
			LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			int month = localDate.getMonthValue();
			int year = localDate.getYear();
			if (month == 12) {
				month = 1;
				year = year + 1;
			}
			preStatement.setInt(1, month);
			preStatement.setInt(2, year);
			// handle param IN operator
			index = 3;
			for (String id : emplsCodeEnd) {
				preStatement.setObject(index++, id);
			}
			resultSet = preStatement.executeQuery();
			while (resultSet.next()) {
				EmployeeThai empThai = new EmployeeThai(resultSet.getString("codeEmp"), resultSet.getLong("k.id"));
				emplsDaDangKyKPI.add(empThai);
			}
			// loc ra nhung nguoi chua dang ky
			for (int i = 0; i < emplsEnd.size(); i++) {
				boolean isExist = false;
				for (int j = 0; j < emplsDaDangKyKPI.size(); j++) {
					if (emplsEnd.get(i).getCode().equals(emplsDaDangKyKPI.get(j).getCode())) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					emplsChuaDangKyKPI.add(emplsEnd.get(i));
				}
			}
			System.out.println(emplsChuaDangKyKPI.size());
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
