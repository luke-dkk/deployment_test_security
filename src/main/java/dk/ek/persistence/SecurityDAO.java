package dk.ek.persistence;


import dk.ek.exceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class SecurityDAO implements ISecurityDAO {
    EntityManagerFactory emf;
    public SecurityDAO(EntityManagerFactory emf){
        this.emf = emf;
    }

    @Override
    public User getVerifiedUser(String username, String password) throws ValidationException {
        try(EntityManager em = emf.createEntityManager()){
            User user = em.find(User.class, username);
            if(user.verifyPassword(password)){
                return user;
            } else {
                throw new ValidationException("User could not be validated");
            }
        }
    }

    @Override
    public User createUser(String username, String password) {
        try(EntityManager em = emf.createEntityManager()){
            User user = new User(username, password);
            Role userRole = em.find(Role.class, "user");
            em.getTransaction().begin();
            if(userRole == null){
                userRole = new Role("user");
                em.persist(userRole);
            }
            user.addRole(userRole);
            em.persist(user);

            em.getTransaction().commit();
            return user;
        }
    }

    @Override
    public Role createRole(String role) {
        return null;
    }

    @Override
    public User addUserRole(String username, String role) {
        return null;
    }

    public static void main(String[] args) {
        ISecurityDAO dao = new SecurityDAO(HibernateConfig.getEntityManagerFactory());
//        User user = dao.createUser("newUser3", "newPassword");
        try {
            User foundUser = dao.getVerifiedUser("newUser3", "newPassword");
            System.out.println(foundUser);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }
}
