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
import trong.lixco.com.jpa.thai.EmployeeSyncDataCenter;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class EmployeeSyncDataCenterService extends AbstractService<EmployeeSyncDataCenter> {
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
	protected Class<EmployeeSyncDataCenter> getEntityClass() {
		return EmployeeSyncDataCenter.class;
	}

	public EmployeeSyncDataCenter findByEmplCode(String employeeCode) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EmployeeSyncDataCenter> cq = cb.createQuery(EmployeeSyncDataCenter.class);
		Root<EmployeeSyncDataCenter> root = cq.from(EmployeeSyncDataCenter.class);
		List<Predicate> queries = new ArrayList<>();
		if (employeeCode != null) {
			Predicate isTemp = cb.equal(root.get("employee_code"), employeeCode);
			queries.add(isTemp);
		}

		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<EmployeeSyncDataCenter> query = em.createQuery(cq);
		List<EmployeeSyncDataCenter> results = query.getResultList();
		if (results.isEmpty()) {
			return new EmployeeSyncDataCenter();
		} else {
			return results.get(0);
		}
	}
	// Predicate codePJobQuery =
	// cb.in(root.get("codePJob")).value(codeJobs);

	public List<EmployeeSyncDataCenter> findByListEmplCode(List<String> employeesCode) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EmployeeSyncDataCenter> cq = cb.createQuery(EmployeeSyncDataCenter.class);
		Root<EmployeeSyncDataCenter> root = cq.from(EmployeeSyncDataCenter.class);
		List<Predicate> queries = new ArrayList<>();
		if (employeesCode != null) {
			Predicate codePJobQuery = cb.in(root.get("employee_code")).value(employeesCode);
			queries.add(codePJobQuery);
		}

		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<EmployeeSyncDataCenter> query = em.createQuery(cq);
		List<EmployeeSyncDataCenter> results = query.getResultList();
		if (results.isEmpty()) {
			return new ArrayList<>();
		} else {
			return results;
		}
	}
}
