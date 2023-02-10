package dao.jpa;

import dao.DAORooms;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.Rooms;

import java.util.List;

public class DAORoomsJPA implements DAORooms {
    private EntityManager entityManager;

    public DAORoomsJPA(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean delete(Rooms obj) {
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
    public List<Rooms> findAll() {
        TypedQuery<Rooms> query = entityManager.createNamedQuery("Rooms.findAll", Rooms.class);
        return query.getResultList();
    }

    @Override
    public Rooms getById(String id) {
        TypedQuery<Rooms> query = entityManager.createNamedQuery("Rooms.getById", Rooms.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public Rooms insert(Rooms obj) {
        entityManager.getTransaction().begin();
        entityManager.persist(obj);
        entityManager.getTransaction().commit();

        return entityManager.find(Rooms.class, obj.getId());
    }

    @Override
    public boolean update(Rooms obj) {
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
