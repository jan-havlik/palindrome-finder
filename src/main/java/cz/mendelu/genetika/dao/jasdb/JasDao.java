package cz.mendelu.genetika.dao.jasdb;

import com.oberasoftware.jasdb.api.entitymapper.EntityManager;
import nl.renarj.jasdb.api.DBSession;
import nl.renarj.jasdb.api.model.EntityBag;
import nl.renarj.jasdb.api.query.QueryBuilder;
import nl.renarj.jasdb.core.exceptions.JasDBStorageException;
import nl.renarj.jasdb.index.keys.types.KeyType;
import nl.renarj.jasdb.index.search.IndexField;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by xkoloma1 on 15.01.2016.
 */
public abstract class JasDao<T> {

    protected final String bagName;
    protected final EntityBag entityBag;
    protected final EntityManager entityManager;
    protected final Class<T> persistentClass;

    public JasDao(DBSession dbSession, String bagName) {
        try {
            this.bagName = bagName;
            this.entityBag = dbSession.createOrGetBag(bagName);
            this.entityManager = dbSession.getEntityManager();
            this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (JasDBStorageException e) {
            throw new JasException(String.format("Create DAO object for %s failed.", bagName), e);
        }
    }

    protected void registerIndexString(String filedName, KeyType keyType, boolean unique) {
        try {
            boolean hasIndex = entityBag.getIndexNames().stream() // Hratky s lambdou, ma to zjistit jestli existuje index se stejnym prefixem jako fieldName
                    .map(index -> index.startsWith(filedName)) // prevedu na booleany
                    .reduce(false, (a, b) -> a || b); // provedu or pres vsechny hodnoty

            if (!hasIndex) {
                entityBag.ensureIndex(new IndexField(filedName, keyType), unique);
                entityBag.flush();
            }
        } catch (JasDBStorageException e) {
            throw new JasException(String.format("Create index named %s on bag %s failed.", filedName, bagName), e);
        }
    }

    public T store(T entity) {
        try {
            entityManager.persist(entity);
            entityBag.flush();
            return entity;
        } catch (JasDBStorageException e) {
            throw new JasException(String.format("Store at bag %s: %s failed.", bagName, entity), e);
        }
    }

    public void storeAll(List<T> entities) {
        try {
            for(T entity : entities) {
                entityManager.persist(entity);
            }
            entityBag.flush();
        } catch (JasDBStorageException e) {
            throw new JasException(String.format("Flush bag %s failed.", bagName), e);
        }
    }

    public T get(String id) {
        try {
            return entityManager.findEntity(persistentClass, id);
        } catch (JasDBStorageException e) {
            throw new JasException(String.format("Get entity from bag %s by id %s failed.", bagName, id), e);
        }
    }


    public List<T> findAll() {
        try {
            return entityManager.findEntities(persistentClass, QueryBuilder.createBuilder());
        } catch (JasDBStorageException e) {
            throw new JasException(String.format("Find all from bag %s failed.", bagName), e);
        }
    }

    public void delete(T entity) {
        try {
            entityManager.remove(entity);
            entityBag.flush();
        } catch (JasDBStorageException e) {
            throw new JasException(String.format("Delete entity from bag %s failed.", bagName), e);
        }
    }

    public void deleteAll(List<T> entities) {
        try {
            for(T entity : entities) {
                entityManager.remove(entity);
            }
            entityBag.flush();
        } catch (JasDBStorageException e) {
            throw new JasException(String.format("Flush bag %s failed.", bagName), e);
        }
    }

    public void delete(String id) {
        T entity = get(id);
        if (entity != null) {
            delete(entity);
        }
    }

    protected T findByFiled(String filedName, String value) {
            try {
                List<T> result = entityManager.findEntities(persistentClass, QueryBuilder.createBuilder().field(filedName).value(value), 1);
                return (result.isEmpty()) ? null : result.get(0);
            } catch (JasDBStorageException e) {
                throw new JasException(String.format("Find entity from bag %s by field %s failed.", bagName, filedName), e);
            }
    }

    protected T findByFiled(String filedName, long value) {
        try {
            List<T> result = entityManager.findEntities(persistentClass, QueryBuilder.createBuilder().field(filedName).value(value), 1);
            return (result.isEmpty()) ? null : result.get(0);
        } catch (JasDBStorageException e) {
            throw new JasException(String.format("Find entity from bag %s by field %s failed.", bagName, filedName), e);
        }
    }

    protected List<T> findByField(String filedName, String value) {
        try {
            return entityManager.findEntities(persistentClass, QueryBuilder.createBuilder().field(filedName).value(value));
        } catch (JasDBStorageException e) {
            throw new JasException(String.format("Find entity from bag %s by %s failed.", bagName, filedName), e);
        }
    }

    protected List<T> findByField(String filedName, long value) {
        try {
            return entityManager.findEntities(persistentClass, QueryBuilder.createBuilder().field(filedName).value(value));
        } catch (JasDBStorageException e) {
            throw new JasException(String.format("Find entity from bag %s by %s failed.", bagName, filedName), e);
        }

    }

}
