package dao;

import model.Questions;
import model.Rooms;

import java.util.List;

public interface DAOQuestions extends DAO<Questions, Integer> {
    List<Questions> getByRoom(Rooms room);
    Questions getByRoomAndNum(Rooms room, Integer order_question);
    Long findNumberQuestionsByRoom(Rooms room);
}
