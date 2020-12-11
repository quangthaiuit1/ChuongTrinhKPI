package trong.lixco.com.jpa.thai;

import java.util.Calendar;

public class EmployeeThai {
	private java.lang.String cateJobCode;
	private java.lang.String code;
	private java.lang.String codeDepart;
	private java.lang.String codeOld;
	private java.util.Calendar dayAtWork;
	private java.lang.String email;
	private long id;
	private boolean layOff;
	private java.lang.String name;
	private java.lang.String nameDepart;
	private int numberDayOff;
	private java.util.Calendar timeFixed;
	private boolean workShift;
	private String positionCode;

	public EmployeeThai() {
		super();
	}

	public EmployeeThai(String code, long id) {
		super();
		this.code = code;
		this.id = id;
	}

	public EmployeeThai(long id, String code, String name, String positionCode) {
		super();
		this.code = code;
		this.id = id;
		this.name = name;
		this.positionCode = positionCode;
	}

	public EmployeeThai(String cateJobCode, String code, String codeDepart, String codeOld, String email, long id,
			String name) {
		super();
		this.cateJobCode = cateJobCode;
		this.code = code;
		this.codeDepart = codeDepart;
		this.codeOld = codeOld;
		this.email = email;
		this.id = id;
		this.name = name;
	}

	public EmployeeThai(String cateJobCode, String code, String codeDepart, String codeOld, Calendar dayAtWork,
			String email, long id, boolean layOff, String name, String nameDepart, int numberDayOff, Calendar timeFixed,
			boolean workShift) {
		super();
		this.cateJobCode = cateJobCode;
		this.code = code;
		this.codeDepart = codeDepart;
		this.codeOld = codeOld;
		this.dayAtWork = dayAtWork;
		this.email = email;
		this.id = id;
		this.layOff = layOff;
		this.name = name;
		this.nameDepart = nameDepart;
		this.numberDayOff = numberDayOff;
		this.timeFixed = timeFixed;
		this.workShift = workShift;
	}

	public java.lang.String getCateJobCode() {
		return cateJobCode;
	}

	public void setCateJobCode(java.lang.String cateJobCode) {
		this.cateJobCode = cateJobCode;
	}

	public java.lang.String getCode() {
		return code;
	}

	public void setCode(java.lang.String code) {
		this.code = code;
	}

	public java.lang.String getCodeDepart() {
		return codeDepart;
	}

	public void setCodeDepart(java.lang.String codeDepart) {
		this.codeDepart = codeDepart;
	}

	public java.lang.String getCodeOld() {
		return codeOld;
	}

	public void setCodeOld(java.lang.String codeOld) {
		this.codeOld = codeOld;
	}

	public java.util.Calendar getDayAtWork() {
		return dayAtWork;
	}

	public void setDayAtWork(java.util.Calendar dayAtWork) {
		this.dayAtWork = dayAtWork;
	}

	public java.lang.String getEmail() {
		return email;
	}

	public void setEmail(java.lang.String email) {
		this.email = email;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isLayOff() {
		return layOff;
	}

	public void setLayOff(boolean layOff) {
		this.layOff = layOff;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String name) {
		this.name = name;
	}

	public java.lang.String getNameDepart() {
		return nameDepart;
	}

	public void setNameDepart(java.lang.String nameDepart) {
		this.nameDepart = nameDepart;
	}

	public int getNumberDayOff() {
		return numberDayOff;
	}

	public void setNumberDayOff(int numberDayOff) {
		this.numberDayOff = numberDayOff;
	}

	public java.util.Calendar getTimeFixed() {
		return timeFixed;
	}

	public void setTimeFixed(java.util.Calendar timeFixed) {
		this.timeFixed = timeFixed;
	}

	public boolean isWorkShift() {
		return workShift;
	}

	public void setWorkShift(boolean workShift) {
		this.workShift = workShift;
	}

	public String getPositionCode() {
		return positionCode;
	}

	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
	}

}
