package barcode.dao.services;

import javax.persistence.EntityManager;

/**
 * Created by xlinux on 11.06.19.
 */
public interface AbstractEntityManager {

    void test();
    EntityManager getEntityManager();

}
