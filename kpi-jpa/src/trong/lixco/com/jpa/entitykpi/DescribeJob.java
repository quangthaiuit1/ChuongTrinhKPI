package trong.lixco.com.jpa.entitykpi;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21
 * Mo ta cong viec
 */
@Entity
public class DescribeJob extends AbstractEntity {
	
	public DescribeJob(){
		relaLocal=new RelaLocal();
	}
	@OneToOne
	private RelaLocal relaLocal;//quan he noi bo trong phong
	private String codePJob;//vi tri cong viec/chuc vu
	private String code;//ma so
	private String docPublicOrEdit;//ban hanh/sua doi
	private Date date;//ngay
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "describeJob")
	private List<OrverviewJob> orverviewJobs;//tom tat cong viec
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "describeJob")
	private List<MissionResponsibility> missionResponsibilities; //nhiem vu va trach nhiem chinh
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "describeJob")
	private List<MissionAnother> missionAnothers; //nhiem vu va trach nhiem khac
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "describeJob")
	private List<RelaInternal> relaInternals;//quan he ben ngoai phong ban
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "describeJob")
	private List<RelaExternal> relaExternals;//quan he ben ngoai cong ty
	
	@Column(columnDefinition = "TEXT")
	private String permissionInJob;//pham vi quyen han cong viec
	@Column(columnDefinition = "TEXT")
	private String permissionFinance;//pham vi quyen han tai chinh
	
	/**
	 * yeu cau cong viec ve trinh do, ky nang
	 * 
	 */
	private String educationLevel;//trinh do van hoa tot nghiep
	private String professionalSkill;// trinh do chuyen mon 
	private String specialized;// chuyen nganh hoạc linh vuc tuong duong
	private String language;//ngoai ngu
	private String languageSkill;//trinh do ngoai ngu
	private String computerSkill;//trinh do ngoai ngu
	private String experience;//kinh nghiem
	private String health;//suc khoe
	private boolean isTalk=false; //giaotiep
	private boolean isShow=false;//thuyet trinh
	private boolean isDoc=false;///soan thao van ban
	private boolean isAnalyse=false;//phan tích
	private boolean isGroup=false;//lam viec theo nhom
	private boolean isNegotiat=false;//dàm phán
	private boolean isManInfor=false;//quan ly ho so
	private boolean isSelfEducated=false;//tu hoc
	private boolean isManJob=false;// to chuc cong viec
	private boolean isAssign=false;//giao viec
	private boolean isCheckEmp=false;//danh gia nhan vien
	private boolean isMeeting=false;//hoi hop
	private boolean isProcessProblem=false;//giai quyet van de
	private boolean isLeader=false;//lanh dao
	private boolean isInterview=false;//phong van
	private boolean isThink=false;//tu duy
	
	private String positionWoking;//vi tri lam viec
	private String envirWorking;//moi truong lam viec
	private String timeWorking;//thoi gian lam viec
	private String toolWorking;//phuong tien lam viec
	private String healthWorking;// the chat/ hao phi suc khoẻ
	private String intellectWorking;//hao phi tri oc/tri tue
	private String protectWorking;//bao ho lao dong
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDocPublicOrEdit() {
		return docPublicOrEdit;
	}
	public void setDocPublicOrEdit(String docPublicOrEdit) {
		this.docPublicOrEdit = docPublicOrEdit;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public List<OrverviewJob> getOrverviewJobs() {
		return orverviewJobs;
	}
	public void setOrverviewJobs(List<OrverviewJob> orverviewJobs) {
		this.orverviewJobs = orverviewJobs;
	}
	public List<MissionResponsibility> getMissionResponsibilities() {
		return missionResponsibilities;
	}
	public void setMissionResponsibilities(List<MissionResponsibility> missionResponsibilities) {
		this.missionResponsibilities = missionResponsibilities;
	}
	public RelaLocal getRelaLocal() {
		return relaLocal;
	}
	public void setRelaLocal(RelaLocal relaLocal) {
		this.relaLocal = relaLocal;
	}
	public List<RelaInternal> getRelaInternals() {
		return relaInternals;
	}
	public void setRelaInternals(List<RelaInternal> relaInternals) {
		this.relaInternals = relaInternals;
	}
	public List<RelaExternal> getRelaExternals() {
		return relaExternals;
	}
	public void setRelaExternals(List<RelaExternal> relaExternals) {
		this.relaExternals = relaExternals;
	}
	public String getPermissionInJob() {
		return permissionInJob;
	}
	public void setPermissionInJob(String permissionInJob) {
		this.permissionInJob = permissionInJob;
	}
	public String getPermissionFinance() {
		return permissionFinance;
	}
	public void setPermissionFinance(String permissionFinance) {
		this.permissionFinance = permissionFinance;
	}
	public String getEducationLevel() {
		return educationLevel;
	}
	public void setEducationLevel(String educationLevel) {
		this.educationLevel = educationLevel;
	}
	public String getProfessionalSkill() {
		return professionalSkill;
	}
	public void setProfessionalSkill(String professionalSkill) {
		this.professionalSkill = professionalSkill;
	}
	public String getSpecialized() {
		return specialized;
	}
	public void setSpecialized(String specialized) {
		this.specialized = specialized;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getLanguageSkill() {
		return languageSkill;
	}
	public void setLanguageSkill(String languageSkill) {
		this.languageSkill = languageSkill;
	}
	public String getExperience() {
		return experience;
	}
	public void setExperience(String experience) {
		this.experience = experience;
	}
	public String getHealth() {
		return health;
	}
	public void setHealth(String health) {
		this.health = health;
	}
	public boolean isTalk() {
		return isTalk;
	}
	public void setTalk(boolean isTalk) {
		this.isTalk = isTalk;
	}
	public boolean isShow() {
		return isShow;
	}
	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}
	public boolean isDoc() {
		return isDoc;
	}
	public void setDoc(boolean isDoc) {
		this.isDoc = isDoc;
	}
	public boolean isAnalyse() {
		return isAnalyse;
	}
	public void setAnalyse(boolean isAnalyse) {
		this.isAnalyse = isAnalyse;
	}
	public boolean isGroup() {
		return isGroup;
	}
	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}
	public boolean isNegotiat() {
		return isNegotiat;
	}
	public void setNegotiat(boolean isNegotiat) {
		this.isNegotiat = isNegotiat;
	}
	public boolean isManInfor() {
		return isManInfor;
	}
	public void setManInfor(boolean isManInfor) {
		this.isManInfor = isManInfor;
	}
	public boolean isSelfEducated() {
		return isSelfEducated;
	}
	public void setSelfEducated(boolean isSelfEducated) {
		this.isSelfEducated = isSelfEducated;
	}
	public boolean isManJob() {
		return isManJob;
	}
	public void setManJob(boolean isManJob) {
		this.isManJob = isManJob;
	}
	public boolean isAssign() {
		return isAssign;
	}
	public void setAssign(boolean isAssign) {
		this.isAssign = isAssign;
	}
	public boolean isCheckEmp() {
		return isCheckEmp;
	}
	public void setCheckEmp(boolean isCheckEmp) {
		this.isCheckEmp = isCheckEmp;
	}
	public boolean isMeeting() {
		return isMeeting;
	}
	public void setMeeting(boolean isMeeting) {
		this.isMeeting = isMeeting;
	}
	public boolean isProcessProblem() {
		return isProcessProblem;
	}
	public void setProcessProblem(boolean isProcessProblem) {
		this.isProcessProblem = isProcessProblem;
	}
	public boolean isLeader() {
		return isLeader;
	}
	public void setLeader(boolean isLeader) {
		this.isLeader = isLeader;
	}
	public boolean isInterview() {
		return isInterview;
	}
	public void setInterview(boolean isInterview) {
		this.isInterview = isInterview;
	}
	public boolean isThink() {
		return isThink;
	}
	public void setThink(boolean isThink) {
		this.isThink = isThink;
	}
	public String getPositionWoking() {
		return positionWoking;
	}
	public void setPositionWoking(String positionWoking) {
		this.positionWoking = positionWoking;
	}
	public String getEnvirWorking() {
		return envirWorking;
	}
	public void setEnvirWorking(String envirWorking) {
		this.envirWorking = envirWorking;
	}
	public String getTimeWorking() {
		return timeWorking;
	}
	public void setTimeWorking(String timeWorking) {
		this.timeWorking = timeWorking;
	}
	public String getToolWorking() {
		return toolWorking;
	}
	public void setToolWorking(String toolWorking) {
		this.toolWorking = toolWorking;
	}
	public String getHealthWorking() {
		return healthWorking;
	}
	public void setHealthWorking(String healthWorking) {
		this.healthWorking = healthWorking;
	}
	public String getIntellectWorking() {
		return intellectWorking;
	}
	public void setIntellectWorking(String intellectWorking) {
		this.intellectWorking = intellectWorking;
	}
	
	public String getProtectWorking() {
		return protectWorking;
	}
	public void setProtectWorking(String protectWorking) {
		this.protectWorking = protectWorking;
	}
	public List<MissionAnother> getMissionAnothers() {
		return missionAnothers;
	}
	public void setMissionAnothers(List<MissionAnother> missionAnothers) {
		this.missionAnothers = missionAnothers;
	}
	public String getComputerSkill() {
		return computerSkill;
	}
	public void setComputerSkill(String computerSkill) {
		this.computerSkill = computerSkill;
	}
	public String getCodePJob() {
		return codePJob;
	}
	public void setCodePJob(String codePJob) {
		this.codePJob = codePJob;
	}
	
}
