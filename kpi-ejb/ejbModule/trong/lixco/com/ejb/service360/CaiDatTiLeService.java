package trong.lixco.com.ejb.service360;

import java.text.SimpleDateFormat;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import trong.lixco.com.ejb.service.AbstractService;
import trong.lixco.com.jpa.entity360.CaiDatTiLe;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CaiDatTiLeService extends AbstractService<CaiDatTiLe> {
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;
	static SimpleDateFormat sf;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected SessionContext getUt() {
		return ct;
	}

	@Override
	protected Class<CaiDatTiLe> getEntityClass() {
		return CaiDatTiLe.class;
	}

}
