package com.home.simplewarehouse.singleton.simplecache;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.simplewarehouse.singleton.simplecache.model.ApplConfig;

/**
 * Session Bean implementation class ApplConfigManagerBean.
 */
@Stateless
@Local(ApplConfigManager.class)
@Remote(ApplConfigService.class)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ApplConfigManagerBean implements ApplConfigManager, ApplConfigService {
	private static final Logger LOG = LogManager.getLogger(ApplConfigManagerBean.class);

	@PersistenceContext
	private EntityManager em;

	@EJB
	ConfigCache configCache;

    /**
     * Default constructor.
     */
    public ApplConfigManagerBean() {
		super();
		LOG.trace("--> ApplConfigManagerBean");
		LOG.trace("<-- ApplConfigManagerBean");
    }

	@Override
	public List<ApplConfig> getAll() {
		LOG.trace("--> getAll()");

		TypedQuery<ApplConfig> query = em.createQuery("SELECT a FROM ApplConfig a ORDER BY a.keyVal", ApplConfig.class);
		List<ApplConfig> configList = query.getResultList();

		LOG.trace("<-- getAll()");

		return configList;
	}

	@Override
	public List<ApplConfig> getContent() {
		LOG.trace("--> getContent()");

		List<ApplConfig> configList = getAll();

		LOG.trace("<-- getContent()");

		return configList;
	}

	@Override
	public List<ApplConfig> getContent(int offset, int count) {
		LOG.trace("--> getContent(" + offset + ", " + count + ")");

        if (offset < 0) {
            throw new IllegalArgumentException("offset < 0");
        }
        if (count < 1) {
            throw new IllegalArgumentException("count < 1");
        }

        TypedQuery<ApplConfig> query = em.createQuery("select a FROM ApplConfig a ORDER BY a.keyVal", ApplConfig.class);
        query.setFirstResult(offset);
        query.setMaxResults(count);

        List<ApplConfig> configList = query.getResultList();

		LOG.trace("<-- getContent(" + offset + ", " + count + ")");

		return configList;
	}

	@Override
	public ApplConfig getById(String keyVal) {
		LOG.trace("--> getById(" + keyVal + ')');

		ApplConfig applConfigItem = em.find(ApplConfig.class, keyVal);

		LOG.trace("<-- getById");

		return applConfigItem;
	}

	@Override
	public ApplConfig create(ApplConfig config) {
		LOG.trace("--> create");

		em.persist(config);

		LOG.trace("<-- create");

		return getById(config.getKeyVal());
	}
	@Override
	public ApplConfig update(ApplConfig config) {
		LOG.trace("--> update");

		ApplConfig entry = getById(config.getKeyVal());
		if (entry != null) {
			entry.setParamVal(config.getParamVal());
		}

		LOG.trace("<-- update");

		return entry;
	}

	@Override
	public ApplConfig delete(String keyVal) {
		LOG.trace("--> delete(" + keyVal + ')');

		ApplConfig config = em.find(ApplConfig.class, keyVal);

		if (config != null) {
			em.remove(config);

			LOG.debug("deleted: " + config);
		}
		else {
			LOG.debug("Key value <" + keyVal + "> not found");
		}
		LOG.trace("<-- delete(" + keyVal + ')');

		return config;
    }

	@Override
	public int count() {
		return getAll().size();
	}

	@Override
	public void refresh() {
		configCache.refresh();
	}
}
