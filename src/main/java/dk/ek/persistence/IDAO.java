package dk.ek.persistence;

import java.util.Set;

/**
 * CRUD operations
 * @param <T>
 */
public interface IDAO <T>{
    T create(T t);
    Set<T> get();
    T getByID(Long id);
    T update(T t);
    Long delete(T t);
}
