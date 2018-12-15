package com.artek.SessionFactory;

import com.artek.Models.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class SessionFactoryUtil {
    private static SessionFactory sessionFactory;

    private SessionFactoryUtil() {}

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            build();
        }
        return sessionFactory;
    }

    public static SessionFactory build() {
        try {
            Configuration configuration = new Configuration().configure();
            configuration.addAnnotatedClass(User.class);
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();

            builder.applySettings(configuration.getProperties());

            sessionFactory = configuration.buildSessionFactory(builder.build());
        }
        catch (Exception e) {

        }
        return sessionFactory;
    }
}
