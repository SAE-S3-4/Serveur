package dao.jdbc;


import dao.DAOQuestions;
import dao.DAORooms;
import dao.DAOScores;
import dao.DAOUser;
import dao.factory.DAOFactory;

public class DAOFactoryJDBC implements DAOFactory {

    @Override
    public DAOQuestions createDAOQuestions() {
        return null;
    }

    @Override
    public DAOUser createDAOUsers() {
        return null;
    }

    @Override
    public DAORooms createDAORooms() {
        return null;
    }

    @Override
    public DAOScores createDAOScores() {
        return null;
    }
}
