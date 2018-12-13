package com.artek.Models;


import com.artek.SessionFactory.SessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class UserDAO {

    public User findByLogin(String login) {
        Session session = SessionFactoryUtil.getSessionFactory().openSession();
        String hql = "FROM User WHERE User.login=:loginParam";
        Query query = session.createQuery(hql);
        query.setParameter("loginParam", login);
        User user = (User) query.uniqueResult();
        session.close();
        return user;
    }

    public User findById(int userId) {
        Session session = SessionFactoryUtil.getSessionFactory().openSession();
        String hql = "FROM User WHERE User.userId=:userIdParam";
        Query query = session.createQuery(hql);
        query.setParameter("userIdParam", userId);
        User user = (User) query.uniqueResult();
        session.close();
        return user;
    }
}
