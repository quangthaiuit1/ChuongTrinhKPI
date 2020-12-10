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
import trong.lixco.com.jpa.thai.KPIPersonalOther;
import trong.lixco.com.jpa.thai.KPIPersonalOtherDetail;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PersonalOtherService extends AbstractService<KPIPersonalOther> {

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
	protected Class<KPIPersonalOther> getEntityClass() {
		return KPIPersonalOther.class;
	}

	// @Override
	// public KPIPersonalOther findById(long id) {
	// KPIPersonalOther result=findById(id);
	// result.getKpiPersonalOtherDetails().size();
	// return result;
	// }

	public List<KPIPersonalOther> find(List<String> codeEmps, int kmonth, int kyear) {
		try {
			// primary
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<KPIPersonalOther> cq = cb.createQuery(KPIPersonalOther.class);
			Root<KPIPersonalOther> root = cq.from(KPIPersonalOther.class);
			List<Predicate> queries = new ArrayList<>();
			if (codeEmps != null && !codeEmps.isEmpty()) {
				Predicate codeEmpQuery = cb.in(root.get("codeEmp")).value(codeEmps);
				queries.add(codeEmpQuery);
			}
			if (kmonth != 0) {
				Predicate kmonthQuery = cb.equal(root.get("kMonth"), kmonth);
				queries.add(kmonthQuery);
			}

			if (kyear != 0) {
				Predicate kyearQuery = cb.equal(root.get("kYear"), kyear);
				queries.add(kyearQuery);
			}
			Predicate data[] = new Predicate[queries.size()];
			for (int i = 0; i < queries.size(); i++) {
				data[i] = queries.get(i);
			}
			Predicate finalPredicate = cb.and(data);
			cq.where(finalPredicate);
			TypedQuery<KPIPersonalOther> query = em.createQuery(cq);
			List<KPIPersonalOther> results = query.getResultList();
			List<KPIPersonalOther> resultsFinal = new ArrayList<>();
			if (!results.isEmpty()) {
				for (int i = 0; i < results.size(); i++) {
					KPIPersonalOther kpiother = results.get(i);
					List<KPIPersonalOtherDetail> listKPIPersonalOtherDetail = new ArrayList<>();
					listKPIPersonalOtherDetail.addAll(results.get(i).getKpiPersonalOtherDetails());
					kpiother.setKpiPersonalOtherDetails(listKPIPersonalOtherDetail);
					resultsFinal.add(kpiother);
				}
			}
			return resultsFinal;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public List<KPIPersonalOther> find(String nameDepart, int kmonth, int kyear, String employeeCode) {
		// primary
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KPIPersonalOther> cq = cb.createQuery(KPIPersonalOther.class);
		Root<KPIPersonalOther> root = cq.from(KPIPersonalOther.class);
		List<Predicate> queries = new ArrayList<>();
		if (nameDepart != null) {
			Predicate nameDepartQuery = cb.equal(root.get("nameDepart"), nameDepart);
			queries.add(nameDepartQuery);
		}
		if (employeeCode != null) {
			Predicate employeeCodeQuery = cb.equal(root.get("codeEmp"), employeeCode);
			queries.add(employeeCodeQuery);
		}
		if (kmonth != 0) {
			Predicate kmonthQuery = cb.equal(root.get("kMonth"), kmonth);
			queries.add(kmonthQuery);
		}

		if (kyear != 0) {
			Predicate kyearQuery = cb.equal(root.get("kYear"), kyear);
			queries.add(kyearQuery);
		}

		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<KPIPersonalOther> query = em.createQuery(cq);
		List<KPIPersonalOther> results = query.getResultList();
		List<KPIPersonalOther> resultsFinal = new ArrayList<>();
		if (!results.isEmpty()) {
			for (int i = 0; i < results.size(); i++) {
				KPIPersonalOther kpiother = results.get(i);
				List<KPIPersonalOtherDetail> listKPIPersonalOtherDetail = new ArrayList<>();
				listKPIPersonalOtherDetail.addAll(results.get(i).getKpiPersonalOtherDetails());
				kpiother.setKpiPersonalOtherDetails(listKPIPersonalOtherDetail);
				resultsFinal.add(kpiother);
			}
		}
		return resultsFinal;
	}
}
