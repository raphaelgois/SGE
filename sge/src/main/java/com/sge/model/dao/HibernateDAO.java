package com.sge.model.dao;

import java.io.Serializable;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;

    public class HibernateDAO<t> implements InterfaceDAO<t>, Serializable{

        private static final long serialVersionUID = 1L;
        private Class<t> classe;
        private Session session;

    public HibernateDAO(Class<t> classe, Session session) {
        super();
        this.classe = classe;
        this.session = session;
    }
        
        
        
    @Override
    public void save(t entity) {
        session.save(entity);
    }

    @Override
    public void update(t entity) {
        session.update(entity);
    }

    @Override
    public void remove(t entity) {
        session.delete(entity);
    }
    public void merge(t entity) {
        session.merge(entity);
    }

    @Override
    public t getEntity(Serializable id) {
        t entity = (t)session.get(classe, id);
            return entity;
        
    }

    @Override
    public t getEntityByDetachedCriteria(DetachedCriteria criteria) {
        t entity = (t)criteria.getExecutableCriteria(session).uniqueResult();
        return entity;
    }

    @Override
    public List<t> getListByDetachedCriteria(DetachedCriteria criteria) {
        return criteria.getExecutableCriteria(session).list();
    }
    
    @Override
    public List<t> getEntities() {
        List<t> entities = (List<t>) session.createCriteria(classe).list();
        return entities;
    }

}
