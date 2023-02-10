package model;

import jakarta.persistence.*;

@NamedQueries({
        @NamedQuery(name = "Questions.findAll", query = "SELECT q FROM Questions q"),
        @NamedQuery(name = "Questions.getById", query = "SELECT q FROM Questions q WHERE q.id = :id"),
        @NamedQuery(name = "Questions.getByRoom", query = "SELECT q FROM Questions q WHERE q.room = :room"),
        @NamedQuery(name = "Questions.findNumberQuestionsByRoom", query = "SELECT COUNT(q) FROM Questions q WHERE q.room = :room"),
        @NamedQuery(name = "Questions.getByRoomAndNum", query = "SELECT q FROM Questions q WHERE q.order_question = :order_question AND q.room = :room"),
})

@Table(name = "QUESTIONS")
@Entity
public class Questions {
    @Id
    @Column(name = "ID")
    int id;

    @Column(name = "ORDER_QUESTION")
    int order_question;

    @Column(name = "ASSIGNEMENT")
    String assignement;

    @Column(name = "TITLE")
    String title;

    @Column(name = "SUGGESTION")
    String suggestion;

    @Column(name = "ANSWER")
    String answer;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ROOM_ID")
    Rooms room;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder_question() {
        return order_question;
    }

    public void setOrder_question(int order_question) {
        this.order_question = order_question;
    }

    public String getAssignement() {
        return assignement;
    }

    public void setAssignement(String assignement) {
        this.assignement = assignement;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Rooms getRoom() {
        return room;
    }

    public void setRoom(Rooms room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "Questions{" +
                "id=" + id +
                ", order_question=" + order_question +
                ", assignement='" + assignement + '\'' +
                ", title='" + title + '\'' +
                ", suggestion='" + suggestion + '\'' +
                ", answer='" + answer + '\'' +
                ", room=" + room +
                '}';
    }
}