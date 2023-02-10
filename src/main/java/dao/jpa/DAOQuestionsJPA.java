package dao.jpa;

import dao.DAOQuestions;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.Questions;
import model.Rooms;

import java.util.List;

public class DAOQuestionsJPA implements DAOQuestions {
    private EntityManager entityManager;

    public DAOQuestionsJPA(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean delete(Questions obj) {
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
    public List<Questions> findAll() {
        TypedQuery<Questions> query = entityManager.createNamedQuery("Questions.findAll", Questions.class);
        return query.getResultList();
    }

    @Override
    public Questions getById(Integer q_id) {
        TypedQuery<Questions> query = entityManager.createNamedQuery("Questions.getById", Questions.class);
        query.setParameter("q_id", q_id);
        return query.getSingleResult();
    }

    @Override
    public Questions insert(Questions obj) {
        entityManager.getTransaction().begin();
        entityManager.persist(obj);
        entityManager.getTransaction().commit();

        return entityManager.find(Questions.class, obj.getId());
    }

    @Override
    public boolean update(Questions obj) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(obj);
            entityManager.getTransaction().commit();
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Questions> getByRoom(Rooms room){
        TypedQuery<Questions> query = entityManager.createNamedQuery("Questions.getByRoom", Questions.class);
        query.setParameter("room", room);
        return query.getResultList();
    }

    @Override
    public Questions getByRoomAndNum(Rooms room, Integer order_question){
        TypedQuery<Questions> query = entityManager.createNamedQuery("Questions.getByRoomAndNum", Questions.class);
        query.setParameter("room", room).setParameter("order_question",order_question);
        return query.getSingleResult();
    }

    @Override
    public Long findNumberQuestionsByRoom(Rooms room) {
        TypedQuery<Long> query = entityManager.createNamedQuery("Questions.findNumberQuestionsByRoom", Long.class);
        query.setParameter("room", room);

        return query.getSingleResult();
    }
}
