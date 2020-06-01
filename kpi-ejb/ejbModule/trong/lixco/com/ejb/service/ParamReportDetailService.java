package trong.lixco.com.ejb.service;

import java.util.LinkedList;
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
import javax.persistence.criteria.Subquery;

import trong.lixco.com.jpa.entitykpi.ParamReport;
import trong.lixco.com.jpa.entitykpi.ParamReportDetail;

/**
 * The Class MemberBO.
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ParamReportDetailService extends AbstractService<ParamReportDetail> {
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
	protected Class<ParamReportDetail> getEntityClass() {
		return ParamReportDetail.class;
	}

	public ParamReportDetail findByParamReport(ParamReport paramReport) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ParamReportDetail> cq = cb.createQuery(ParamReportDetail.class);
		Root<ParamReportDetail> root = cq.from(ParamReportDetail.class);
		cq.select(root).where(cb.equal(root.get("paramReport"), paramReport));
		TypedQuery<ParamReportDetail> query = em.createQuery(cq);
		List<ParamReportDetail> results = query.getResultList();
		if (results.size() != 0) {
			return results.get(0);
		} else {
			return null;
		}

	}

	public List<ParamReportDetail> findByParamReports(ParamReport paramReport) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ParamReportDetail> cq = cb.createQuery(ParamReportDetail.class);
		Root<ParamReportDetail> root = cq.from(ParamReportDetail.class);
		cq.select(root).where(cb.equal(root.get("paramReport"), paramReport));
		TypedQuery<ParamReportDetail> query = em.createQuery(cq);
		List<ParamReportDetail> results = query.getResultList();
		return results;
	}

	public List<ParamReportDetail> findByParamReports_param_name(String nameReport) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ParamReportDetail> cq = cb.createQuery(ParamReportDetail.class);
		List<Predicate> predicatesParent = new LinkedList<Predicate>();
		Root<ParamReportDetail> root = cq.from(ParamReportDetail.class);

		// Subquery
		List<Predicate> predicates = new LinkedList<Predicate>();
		Subquery<ParamReport> sqOne = cq.subquery(ParamReport.class);

		Root<ParamReport> root2 = sqOne.from(ParamReport.class);
		Predicate predicateSup = cb.equal(root2.get("namReport"), nameReport);
		predicates.add(predicateSup);

		// query
		sqOne.where(cb.and(predicates.toArray(new Predicate[0])));
		sqOne.select(root2);

		Predicate predicateParent2 = cb.equal(root.get("paramReport"), cb.any(sqOne));
		predicatesParent.add(predicateParent2);

		// Query (DetailFollowContract)
		cq.select(root).where(cb.and(predicatesParent.toArray(new Predicate[0])));
		List<ParamReportDetail> fcs = em.createQuery(cq).getResultList();

		return fcs;

	}

	public List<ParamReportDetail> findRang(String nameReport) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ParamReportDetail> cq = cb.createQuery(ParamReportDetail.class);
		List<Predicate> predicatesParent = new LinkedList<Predicate>();
		Root<ParamReportDetail> root = cq.from(ParamReportDetail.class);

		if (nameReport != null && !"".equals(nameReport.trim())) {
			// Subquery
			List<Predicate> predicates = new LinkedList<Predicate>();
			Subquery<ParamReport> sqOne = cq.subquery(ParamReport.class);

			Root<ParamReport> root2 = sqOne.from(ParamReport.class);
			Predicate predicateSup = cb.equal(root2.get("namReport"), nameReport);
			predicates.add(predicateSup);

			// query
			sqOne.where(cb.and(predicates.toArray(new Predicate[0])));
			sqOne.select(root2);

			Predicate predicateParent2 = cb.equal(root.get("paramReport"), cb.any(sqOne));
			predicatesParent.add(predicateParent2);
		}
		// Query (DetailFollowContract)
		cq.select(root).where(cb.and(predicatesParent.toArray(new Predicate[0]))).orderBy(cb.desc(root.get("id")));
		List<ParamReportDetail> fcs = em.createQuery(cq).getResultList();

		return fcs;

	}

}
