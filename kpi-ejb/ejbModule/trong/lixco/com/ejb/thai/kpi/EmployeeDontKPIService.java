package trong.lixco.com.ejb.thai.kpi;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import trong.lixco.com.ejb.service.AbstractService;
import trong.lixco.com.jpa.thai.EmployeeDontKPI;
import trong.lixco.com.jpa.thai.KPIPersonalPerformance;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class EmployeeDontKPIService extends AbstractService<EmployeeDontKPI> {

	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected SessionContext getUt() {
		return ct;
	}

	@Override
	protected Class<EmployeeDontKPI> getEntityClass() {
		return EmployeeDontKPI.class;
	}

	public List<EmployeeDontKPI> findByEmplMonthYear(String employeeCode, int month, int year) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EmployeeDontKPI> cq = cb.createQuery(EmployeeDontKPI.class);
		Root<EmployeeDontKPI> root = cq.from(EmployeeDontKPI.class);
		List<Predicate> queries = new ArrayList<>();
		if (employeeCode != null) {
			Predicate isTemp = cb.equal(root.get("employee_code"), employeeCode);
			queries.add(isTemp);
		}
		if (month != 0) {
			Predicate isTemp = cb.equal(root.get("month"), month);
			queries.add(isTemp);
		}
		if (employeeCode != null) {
			Predicate isTemp = cb.equal(root.get("year"), year);
			queries.add(isTemp);
		}

		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<EmployeeDontKPI> query = em.createQuery(cq);
		List<EmployeeDontKPI> results = query.getResultList();
		if (results.isEmpty()) {
			results = new ArrayList<>();
		}
		return results;
	}

	public List<EmployeeDontKPI> findDontKpiEver() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EmployeeDontKPI> cq = cb.createQuery(EmployeeDontKPI.class);
		Root<EmployeeDontKPI> root = cq.from(EmployeeDontKPI.class);
		List<Predicate> queries = new ArrayList<>();
		// Predicate codePJobQuery =
		// cb.in(root.get("codePJob")).value(codeJobs);
		Predicate isTemp = cb.equal(root.get("is_temp"), false);
		queries.add(isTemp);
		Predicate isThaiSan = cb.equal(root.get("is_thaisan"), false);
		queries.add(isThaiSan);

		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<EmployeeDontKPI> query = em.createQuery(cq);
		List<EmployeeDontKPI> results = query.getResultList();
		if (results.isEmpty()) {
			results = new ArrayList<>();
		}
		return results;
	}

	public List<EmployeeDontKPI> findDontKpiTempByMonth(int month, int year) {
		// primary
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EmployeeDontKPI> cq = cb.createQuery(EmployeeDontKPI.class);
		Root<EmployeeDontKPI> root = cq.from(EmployeeDontKPI.class);
		List<Predicate> queries = new ArrayList<>();
		if (month != 0) {
			Predicate preMonth = cb.equal(root.get("month"), month);
			queries.add(preMonth);
		}
		if (year != 0) {
			Predicate preYear = cb.equal(root.get("year"), year);
			queries.add(preYear);
		}
		Predicate pre = cb.equal(root.get("is_temp"), true);
		queries.add(pre);
		Predicate pre1 = cb.equal(root.get("is_thaisan"), false);
		queries.add(pre1);
		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<EmployeeDontKPI> query = em.createQuery(cq);
		List<EmployeeDontKPI> results = query.getResultList();
		return results;
	}

	public List<EmployeeDontKPI> findDontKpiThaiSanByMonth(int month, int year) {
		// primary
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EmployeeDontKPI> cq = cb.createQuery(EmployeeDontKPI.class);
		Root<EmployeeDontKPI> root = cq.from(EmployeeDontKPI.class);
		List<Predicate> queries = new ArrayList<>();
		if (month != 0) {
			Predicate pre = cb.equal(root.get("month"), month);
			queries.add(pre);
		}
		if (year != 0) {
			Predicate pre = cb.equal(root.get("year"), year);
			queries.add(pre);
		}
		Predicate pre = cb.equal(root.get("is_temp"), false);
		queries.add(pre);
		Predicate pre1 = cb.equal(root.get("is_thaisan"), true);
		queries.add(pre1);

		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<EmployeeDontKPI> query = em.createQuery(cq);
		List<EmployeeDontKPI> results = query.getResultList();
		return results;
	}

}
