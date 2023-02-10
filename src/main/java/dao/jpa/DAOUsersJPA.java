package dao.jpa;

import dao.DAOUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.Rooms;
import model.Scores;
import model.Users;

import java.util.List;

public class DAOUsersJPA implements DAOUser {
    private EntityManager entityManager;

    public DAOUsersJPA(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean delete(Users obj) {
        try {
            entityManager.getTransaction().begin();
            entityManager.remove(obj);
            entityManager.getTransaction().commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Users> findAll() {
        TypedQuery<Users> query = entityManager.createNamedQuery("Users.findAll", Users.class);
        return query.getResultList();
    }

    @Override
    public Users getById(String id) {
        TypedQuery<Users> query = entityManager.createNamedQuery("Users.getById", Users.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public Users insert(Users obj) {
        entityManager.getTransaction().begin();
        entityManager.persist(obj);
        entityManager.getTransaction().commit();

        return entityManager.find(Users.class, obj.getId());
    }

    @Override
    public boolean update(Users obj) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(obj);
            entityManager.getTransaction().commit();
            return true;
        }catch (Exception e) {
            return false;
        }
    }
}
