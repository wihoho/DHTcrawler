import dto.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class KunderExample {
    public static void main(String[] args) {
        User user = User.builder()
                .userId("002")
                .firstName("John")
                .lastName("Smith")
                .city("London")
                .build();


        EntityManagerFactory emf = Persistence.createEntityManagerFactory("cassandra");
        EntityManager em = emf.createEntityManager();


        User user1 = em.find(User.class, "002");
        em.close();
        emf.close();
    }
}