package com.artek.Dao;

import com.artek.Models.User;
import com.artek.SessionFactory.SessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.transaction.Transactional;

public class ManagerDAO {

    public static volatile ManagerDAO instance;

    public static ManagerDAO getInstance() {
        final ManagerDAO currentInstance;
        if (instance == null) {
            synchronized (ManagerDAO.class) {
                if (instance == null) {
                    instance = new ManagerDAO();
                }
                currentInstance = instance;
            }

        }
        else {
            currentInstance = instance;
        }
        return currentInstance;

    }

    public void addUser(Integer userId, String login, String password, boolean isActive) {
        Session session = SessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String sql = "INSERT INTO users (userId, login, password, isActive) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE isActive=?";
        Query query = session.createSQLQuery(sql);
        query.setParameter(1, userId);
        query.setParameter(2, login);
        query.setParameter(3, password);
        query.setParameter(4, isActive ? 1 : 0);
        query.setParameter(5, isActive ? 1 : 0);
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    public String[] getUserCredentials(Integer userId) {
        String[] credentials = new String[2];
        Session session = SessionFactoryUtil.getSessionFactory().openSession();
        String hql = "FROM com.artek.Models.User as User WHERE User.userId=:userIdParam";
        try {
            Query query = session.createQuery(hql);
            query.setParameter("userIdParam", userId);
            User user = (User) query.uniqueResult();

            credentials[0] = user.getLogin();
            credentials[1] = user.getPassword();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        session.close();
        return credentials;
    }

    public boolean isUserActive(Integer userId) {
        Session session = SessionFactoryUtil.getSessionFactory().openSession();
        String hql = "FROM com.artek.Models.User as User WHERE User.userId=:param";
        Query query = session.createQuery(hql);
        query.setParameter("param", userId);

        User user= (User) query.getSingleResult();
        session.close();
        return user.getIsActive() == 1;
    }

    public void setIsActiveForUser(Integer isActive, Integer userId) {
        Session session = SessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();

        String sql = "UPDATE users SET isActive = ? WHERE userId = ?";
        Query query = session.createSQLQuery(sql);
        query.setParameter(1, isActive);
        query.setParameter(2, userId);
        query.executeUpdate();
        session.getTransaction().commit();

        session.close();
    }
}
