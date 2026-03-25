package dk.ek.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.HashSet;
import java.util.Set;

public class EmployeeDAO implements IDAO<Employee> {
    EntityManagerFactory emf;
    public EmployeeDAO(EntityManagerFactory _emf){
        this.emf = _emf;
    }
    @Override
    public Employee create(Employee e) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(e);
            em.getTransaction().commit();
            return e;
        }
    }

    @Override
    public Set<Employee> get() {
        try(EntityManager em = emf.createEntityManager()){
            return new HashSet(em.createQuery("SELECT e FROM Employee e").getResultList());
        }
    }

    @Override
    public Employee getByID(Long id) {
        try(EntityManager em = emf.createEntityManager()){
            Employee employee = em.find(Employee.class, id);
            if(employee == null)
                throw new EntityNotFoundException("No entity found with id: "+id);
            return employee;
        }
    }

    @Override
    public Employee update(Employee e) {
        try(EntityManager em = emf.createEntityManager()){
            Employee foundEmployee = em.find(Employee.class, e.getId());
            if(foundEmployee == null)
                throw new EntityNotFoundException("No entity found with id: "+e.getId());
            em.getTransaction().begin();
            Employee employee = em.merge(e);
            em.getTransaction().commit();
            return employee;
        }
    }

    public Employee updateDepartment(long empId, long deptId){
        try(EntityManager em = emf.createEntityManager()){
            Employee foundEmp = em.find(Employee.class, empId);
            Department foundDept = em.find(Department.class, deptId);
            if(foundEmp == null || foundDept == null)
                throw new EntityNotFoundException("Either Employee, Department or both were not found");
            em.getTransaction().begin();
            foundEmp.setDepartment(foundDept);
            em.getTransaction().commit();
            return foundEmp;
        }
    }

    /**
     * Update an Employee from a detached object WITHOUT cascading.
     *
     * Rules:
     * 1) Employee must have an ID and exist in DB (otherwise EntityNotFoundException)
     * 2) If a Department is provided:
     *    - If department.id == null  -> treat as NEW department, persist it
     *    - If department.id != null  -> it MUST exist, otherwise EntityNotFoundException
     * 3) Only non-null fields from empToBeUpdated are applied (patch update)
     */
    public Employee updateWithCheck(Employee incoming) {
        if (incoming == null || incoming.getId() == null)
            throw new IllegalArgumentException("Employee id required");

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Employee managed = requireEmployee(em, incoming.getId());

                if (incoming.getDepartment() != null) {
                    Department dept = resolveDepartment(em, incoming.getDepartment());
                    managed.setDepartment(dept);
                }

                if (incoming.getName() != null) managed.setName(incoming.getName());
                if (incoming.getEmail() != null) managed.setEmail(incoming.getEmail());

                em.getTransaction().commit();
                return managed;
            } catch (RuntimeException e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    private Employee requireEmployee(EntityManager em, Long id) {
        Employee e = em.find(Employee.class, id);
        if (e == null) throw new EntityNotFoundException("Employee not found: " + id);
        return e;
    }

    private Department resolveDepartment(EntityManager em, Department incomingDept) {
        Long id = incomingDept.getId();
        if (id == null) {
            em.persist(incomingDept);      // explicit persist (no cascade)
            return incomingDept;
        }
        Department managed = em.find(Department.class, id);
        if (managed == null) throw new EntityNotFoundException("Department not found: " + id);
        return managed;
    }



    @Override
    public Long delete(Employee e) {
        try(EntityManager em = emf.createEntityManager()){
            Employee employee = em.find(Employee.class, e.getId());
            if(employee == null)
                throw new EntityNotFoundException("No entity found with id: "+e.getId());
            em.getTransaction().begin();
            em.remove(employee);
            em.getTransaction().commit();
            return employee.getId();
        }
    }
}
