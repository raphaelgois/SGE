package com.sge.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

    public class HibernateUtil {
    private static final SessionFactory sessionFactory;
    public static final String HIBERNATE_SESSION = "hibernate_session";
    
    static {
        try {
            System.out.println("Tentando abrir session factory");
            Configuration configuration = new Configuration().configure();
            ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            System.out.println("Session factory criada corretamente");
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao iniciar a session factory. "+ e);
            throw new ExceptionInInitializerError(e); 
        }
    }
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
}

    