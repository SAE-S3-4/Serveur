package dao;

import model.Rooms;
import model.Scores;
import model.Users;

import java.util.List;

public interface DAOScores extends DAO<Scores, Integer>{
    List<Scores> getByRoom(Rooms room);
    Scores getByUserAndRoom(Users user, Rooms room);
}
