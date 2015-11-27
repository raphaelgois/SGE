package com.sge.model.dao;

import java.io.Serializable;
import java.util.List;
import org.hibernate.criterion.DetachedCriteria;

    public interface InterfaceDAO<t> {
    void save (t entity);
    void update (t entity);
    void remove (t entity);
    t getEntity(Serializable id);
    t getEntityByDetachedCriteria(DetachedCriteria criteria);
    List<t> getEntities();
    List<t> getListByDetachedCriteria(DetachedCriteria criteria);
    
}
