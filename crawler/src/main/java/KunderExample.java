import dto.User;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;


public class KunderExample {
    public static void main(String[] args) {
        User user = User.builder()
                .userId("002")
                .firstName("John")
                .lastName("Smith")
                .city("London")
                .build();


        CouchDbClient dbClient = new CouchDbClient();
        Response response = dbClient.save(user);

        User foo = dbClient.find(User.class, response.getId());

        dbClient.remove(foo);
    }
}