package trong.lixco.com.ejb.servicekpi;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import trong.lixco.com.ejb.service.AbstractService;
import trong.lixco.com.jpa.entitykpi.PathImageAssign;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PathImageAssignService extends AbstractService<PathImageAssign> {
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
	protected Class<PathImageAssign> getEntityClass() {
		return PathImageAssign.class;
	}
}
