package trong.lixco.com.thai.apitrong;

public class PositionJobData{
	private long id;
	private String code;
	private String name;
	private String codeDepart;
	private boolean disable;
	private boolean kpilast;
	
	public PositionJobData(long id,String code, String name, String codeDepart,boolean disable,boolean kpilast) {
		this.id=id;
		this.code = code;
		this.name = name;
		this.codeDepart = codeDepart;
		this.disable=disable;
		this.kpilast=kpilast;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCodeDepart() {
		return codeDepart;
	}
	public void setCodeDepart(String codeDepart) {
		this.codeDepart = codeDepart;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public boolean isDisable() {
		return disable;
	}
	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	public boolean isKpilast() {
		return kpilast;
	}
	public void setKpilast(boolean kpilast) {
		this.kpilast = kpilast;
	}
}
