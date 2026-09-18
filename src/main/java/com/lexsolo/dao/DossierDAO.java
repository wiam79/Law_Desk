package com.lexsolo.dao;

import com.lexsolo.entity.Dossier;
import com.lexsolo.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class DossierDAO {

    public void add(Dossier dossier) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(dossier);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void update(Dossier dossier) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(dossier);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Dossier dossier = session.get(Dossier.class, id);
            if (dossier != null) {
                session.remove(dossier);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Dossier findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Dossier.class, id);
        }
    }

    public List<Dossier> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Join fetch to avoid LazyInitializationException if accessed outside session
            return session.createQuery("FROM Dossier d JOIN FETCH d.client", Dossier.class).list();
        }
    }

    public List<Dossier> findByClientId(Long clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Dossier> query = session.createQuery("FROM Dossier d WHERE d.client.id = :clientId", Dossier.class);
            query.setParameter("clientId", clientId);
            return query.list();
        }
    }

    public List<Dossier> findByStatus(Dossier.DossierStatus status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Dossier> query = session.createQuery("FROM Dossier d WHERE d.status = :status", Dossier.class);
            query.setParameter("status", status);
            return query.list();
        }
    }
}