package trong.lixco.com.ejb.service;

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
import javax.persistence.criteria.Root;

import trong.lixco.com.jpa.entitykpi.ParamReport;
import trong.lixco.com.jpa.entitykpi.ParamReportDetail;

/**
 * The Class MemberBO.
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ParamReportService extends AbstractService<ParamReport> {
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
	protected Class<ParamReport> getEntityClass() {
		return ParamReport.class;
	}

	public ParamReport findByNamReport(String name) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ParamReport> cq = cb.createQuery(ParamReport.class);
		Root<ParamReport> root = cq.from(ParamReport.class);
		cq.select(root).where(cb.equal(root.get("namReport"), name));
		TypedQuery<ParamReport> query = em.createQuery(cq);
		List<ParamReport> results = query.getResultList();
		if (results.size() != 0) {
			return results.get(0);
		} else {
			return new ParamReport();
		}

	}

}
