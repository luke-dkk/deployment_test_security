package dk.ek.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.HashSet;
import java.util.Set;

public class DepartmentDAO implements IDAO<Department> {
    EntityManagerFactory emf;
    public DepartmentDAO(EntityManagerFactory _emf){
        this.emf = _emf;
    }
    @Override
    public Department create(Department d) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(d);
            em.getTransaction().commit();
            return d;
        }
    }

    @Override
    public Set<Department> get() {
        try(EntityManager em = emf.createEntityManager()){
            return new HashSet(em.createQuery("SELECT d FROM Department d").getResultList());
        }
    }

    @Override
    public Department getByID(Long id) {
        try(EntityManager em = emf.createEntityManager()){
            Department department = em.find(Department.class, id);
            if(department == null)
                throw new EntityNotFoundException("No entity found with id: "+id);
            return department;
        }
    }

    @Override
    public Department update(Department d) {
        try(EntityManager em = emf.createEntityManager()){
            Department foundEmployee = em.find(Department.class, d.getId());
            if(foundEmployee == null)
                throw new EntityNotFoundException("No entity found with id: "+d.getId());
            em.getTransaction().begin();
            Department department = em.merge(d);
            em.getTransaction().commit();
            return department;
        }
    }

    @Override
    public Long delete(Department d) {
        try(EntityManager em = emf.createEntityManager()){
            Department department = em.find(Department.class, d.getId());
            if(department == null)
                throw new EntityNotFoundException("No entity found with id: "+d.getId());
            em.getTransaction().begin();
            em.remove(department);
            em.getTransaction().commit();
            return department.getId();
        }
    }
}
