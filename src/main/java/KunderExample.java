import dto.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class KunderExample {
    public static void main(String[] args) {
        User user = User.builder()
                .userId("001")
                .firstName("John")
                .lastName("Smith")
                .city("London")
                .build();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("cassandra_pu");
        EntityManager em = emf.createEntityManager();

        em.persist(user);
        em.close();
        emf.close();
    }
}