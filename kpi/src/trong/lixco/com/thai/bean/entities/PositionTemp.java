package trong.lixco.com.thai.bean.entities;

public class PositionTemp {
	private long id;
	private String code;

	public PositionTemp() {
		super();
	}

	public PositionTemp(long id, String code) {
		super();
		this.id = id;
		this.code = code;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
