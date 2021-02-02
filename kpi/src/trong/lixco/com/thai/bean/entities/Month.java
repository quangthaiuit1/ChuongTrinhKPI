package trong.lixco.com.thai.bean.entities;

public class Month {
	private long id;
	private String month;
	private int value;
	private boolean select = false;

	public Month() {
		super();
	}

	public Month(long id, String month, int value) {
		super();
		this.id = id;
		this.month = month;
		this.value = value;
	}

	public Month(long id, String month) {
		super();
		this.id = id;
		this.month = month;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
