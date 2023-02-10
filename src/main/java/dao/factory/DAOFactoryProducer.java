package dao.factory;

import dao.jdbc.DAOFactoryJDBC;
import dao.jpa.DAOFactoryJPA;

public class DAOFactoryProducer {
    public static DAOFactory getFactory(DAOType type) {
        return switch (type){
            case JPA -> new DAOFactoryJPA();
            case JDBC -> new DAOFactoryJDBC();
            default -> throw new IllegalArgumentException();
        };
    }
}