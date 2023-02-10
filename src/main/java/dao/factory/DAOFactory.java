package dao.factory;

import dao.DAOQuestions;
import dao.DAORooms;
import dao.DAOScores;
import dao.DAOUser;

public interface DAOFactory {
    DAOQuestions createDAOQuestions();
    DAOUser createDAOUsers();
    DAORooms createDAORooms();
    DAOScores createDAOScores();

}
