package com.artek.SessionFactory;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.telegram.telegrambots.meta.logging.BotLogger;

public class SessionFactoryUtil {
    private static volatile SessionFactory sessionFactory;

    private SessionFactoryUtil() {}

    public static SessionFactory getSessionFactory() {
        if (SessionFactoryUtil.sessionFactory == null) {
            synchronized (SessionFactoryUtil.class) {
                if (SessionFactoryUtil.sessionFactory == null) {
                    build();
                }
            }
        }
        return SessionFactoryUtil.sessionFactory;
    }

    public static void build() {
        try {
            Configuration configuration = new Configuration().configure();
//            configuration.addAnnotatedClass(User.class);
//            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();

//            builder.applySettings(configuration.getProperties());
//            MetadataSources sources = new MetadataSources(builder.build()).addAnnotatedClass(User.class);
//            Metadata metadata = sources.getMetadataBuilder().build();


//            SessionFactoryUtil.sessionFactory = configuration.buildSessionFactory(builder.build());

//            SessionFactoryUtil.sessionFactory = metadata.getSessionFactoryBuilder().build();
            SessionFactoryUtil.sessionFactory = configuration.buildSessionFactory();
        }
        catch (Exception e) {
            BotLogger.error("SESSION_FACTORY_UTIL", e);
        }

    }
}
