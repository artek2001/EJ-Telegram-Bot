package com.artek.SessionFactory;

import com.artek.Models.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.util.HashMap;
import java.util.Map;

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
            configuration.addAnnotatedClass(User.class);
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();

            builder.applySettings(configuration.getProperties());
            MetadataSources sources = new MetadataSources(builder.build()).addAnnotatedClass(User.class);
            Metadata metadata = sources.getMetadataBuilder().build();


//            SessionFactoryUtil.sessionFactory = configuration.buildSessionFactory(builder.build());

            SessionFactoryUtil.sessionFactory = metadata.getSessionFactoryBuilder().build();
        }
        catch (Exception e) {
            BotLogger.error("SESSION_FACTORY_UTIL", e);
        }

    }
}
