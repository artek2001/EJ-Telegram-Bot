package com.artek.Dao;

import com.artek.Models.User;
import com.artek.SessionFactory.SessionFactoryUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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

        } else {
            currentInstance = instance;
        }
        return currentInstance;

    }

    public void addUser(Integer userId, String login, String password, boolean isActive, int state) {

        Session session = SessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String sql = "INSERT INTO users (userId, login, password, isActive, state) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE isActive=?, ej_grsmubot.users.state=?";
        Query query = session.createSQLQuery(sql);
        query.setParameter(1, userId);
        query.setParameter(2, login);
        query.setParameter(3, password);
        query.setParameter(4, state);
        query.setParameter(5, isActive ? 1 : 0);
        query.setParameter(6, isActive ? 1 : 0);
        query.setParameter(7, 1);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        session.close();
        return credentials;
    }

    public boolean isUserActive(Integer userId) {
        User user = null;
        try {
            user = null;

            Session session = SessionFactoryUtil.getSessionFactory().openSession();
            String hql = "FROM com.artek.Models.User as User WHERE User.userId=:param";
            Query query = session.createQuery(hql);
            query.setParameter("param", userId);

            user = (User) query.getSingleResult();

            session.close();
        } catch (Exception e) {
            //when there is no user with such userId
            return false;
        }

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

    public int getUserState(Integer userId) {
        Session session = SessionFactoryUtil.getSessionFactory().openSession();
        String hql = "FROM com.artek.Models.User as User WHERE User.userId=:parameter";
        Query query = session.createQuery(hql);
        query.setParameter("parameter", userId);

        User user = (User) query.getSingleResult();
        session.close();
        return user.getState();
    }

    public void setUserState(Integer userId, int state) {
        Session session = SessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();

        String sql = "UPDATE users SET state = ? WHERE userId = ?";
        Query query = session.createSQLQuery(sql);
        query.setParameter(1, state);
        query.setParameter(2, userId);
        query.executeUpdate();
        session.getTransaction().commit();

        session.close();
    }

    public String getRecentMarks(Integer userId) {
        Session session = SessionFactoryUtil.getSessionFactory().openSession();
        String sql = "SELECT allMarksLast FROM ej_grsmubot.users WHERE userId=?";
        Query query = session.createSQLQuery(sql);
        query.setParameter(1, userId);

        String marks = (String) query.getSingleResult();
        session.close();
        return marks;
    }

    public void setRecentMarks(String login, String allSubjectsWithMarks) {
        Session session = SessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();

        String sql = "UPDATE users SET allMarksLast = ? WHERE login = ?";
        Query query = session.createSQLQuery(sql);
        query.setParameter(1, allSubjectsWithMarks);
        query.setParameter(2, login);
        query.executeUpdate();
        session.getTransaction().commit();

        session.close();
    }

    public void addAllSubjects(String login, String allSubjects) {
        Session session = SessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();

        String sql = "UPDATE users SET subjects = ? WHERE login = ?";
        Query query = session.createSQLQuery(sql);
        query.setParameter(1, allSubjects);
        query.setParameter(2, login);
        query.executeUpdate();
        session.getTransaction().commit();

        session.close();
    }

    public ArrayList<String> getAllSubjects(Integer userId) {
        ArrayList<String> allSubj = new ArrayList<>();

        Session session = SessionFactoryUtil.getSessionFactory().openSession();

        String sql = "SELECT allMarksLast FROM ej_grsmubot.users WHERE userId=?";
        Query query = session.createSQLQuery(sql);
        query.setParameter(1, userId);

        String json = (String)query.getSingleResult();

        JsonNode rootNode = null;
        try {
            rootNode = new ObjectMapper().readTree(json);
        } catch (IOException e) {
            BotLogger.error("ManagerDAO", "Error reading Json NodeTree");
        }

        for (Iterator<String> it = rootNode.fieldNames(); it.hasNext(); ) {
            String subjName = it.next();
            allSubj.add(subjName);
        }

        session.close();
        return  allSubj;
    }
}
