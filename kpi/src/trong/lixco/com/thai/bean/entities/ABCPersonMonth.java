package trong.lixco.com.thai.bean.entities;

public class ABCPersonMonth {
	private long id;
	private String employeeName;
	private String departmentName;
	private double kpiTo; // diem kpi to
	private double kpiCaNhan; // diem kpi ca nhan
	private double tongdiem;
	private String xeploai; // A , B, C
	private String note = ""; // ghi chu: thai san , khong lam

	public ABCPersonMonth() {
		super();
	}

	public ABCPersonMonth(long id, String employeeName, String departmentName, double kpiTo, double kpiCaNhan,
			double tongdiem, String xeploai, String note) {
		super();
		this.id = id;
		this.employeeName = employeeName;
		this.departmentName = departmentName;
		this.kpiTo = kpiTo;
		this.kpiCaNhan = kpiCaNhan;
		this.tongdiem = tongdiem;
		this.xeploai = xeploai;
		this.note = note;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public double getKpiTo() {
		return kpiTo;
	}

	public void setKpiTo(double kpiTo) {
		this.kpiTo = kpiTo;
	}

	public double getKpiCaNhan() {
		return kpiCaNhan;
	}

	public void setKpiCaNhan(double kpiCaNhan) {
		this.kpiCaNhan = kpiCaNhan;
	}

	public double getTongdiem() {
		return tongdiem;
	}

	public void setTongdiem(double tongdiem) {
		this.tongdiem = tongdiem;
	}

	public String getXeploai() {
		return xeploai;
	}

	public void setXeploai(String xeploai) {
		this.xeploai = xeploai;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
