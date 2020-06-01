package trong.lixco.com.jpa.entitykpi;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21
 * Chuc vu (trong KPI theo mo ta cong viec)
 */
@Entity
public class PositionJob{
	
	@Id
	protected Long id;
	
	@Column(name = "created_date")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date createdDate = new Date();
	
	@Column(name = "modify_date")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date modifiedDate;
	
	@Column(name = "created_user")
	protected String createdUser;
	protected long iduser;
	@Column(columnDefinition = "TEXT")
	protected String note;
	private boolean disable=false;// tat nhan vien
	private boolean oldData=false;//du lieu cu
	
	@Transient
	private boolean select=false;
	@Transient
	private int index=0;
	@Transient
	private boolean color=false;
	
	private String code;
	private String name;
	private String codeDepart;//phong ban
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCodeDepart() {
		return codeDepart;
	}
	public void setCodeDepart(String codeDepart) {
		this.codeDepart = codeDepart;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getCreatedUser() {
		return createdUser;
	}
	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}
	public long getIduser() {
		return iduser;
	}
	public void setIduser(long iduser) {
		this.iduser = iduser;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public boolean isDisable() {
		return disable;
	}
	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	public boolean isOldData() {
		return oldData;
	}
	public void setOldData(boolean oldData) {
		this.oldData = oldData;
	}
	public boolean isSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public boolean isColor() {
		return color;
	}
	public void setColor(boolean color) {
		this.color = color;
	}
	

	
}
