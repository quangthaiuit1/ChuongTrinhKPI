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
import trong.lixco.com.jpa.thai.EmployeeDontKPIEver;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class EmployeeDontKPIEverService extends AbstractService<EmployeeDontKPIEver> {

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
	protected Class<EmployeeDontKPIEver> getEntityClass() {
		return EmployeeDontKPIEver.class;
	}

	public List<EmployeeDontKPIEver> findByEmpl(String employeeCode) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EmployeeDontKPIEver> cq = cb.createQuery(EmployeeDontKPIEver.class);
		Root<EmployeeDontKPIEver> root = cq.from(EmployeeDontKPIEver.class);
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
		TypedQuery<EmployeeDontKPIEver> query = em.createQuery(cq);
		List<EmployeeDontKPIEver> results = query.getResultList();
		if (results.isEmpty()) {
			results = new ArrayList<>();
		}
		return results;
	}
}
