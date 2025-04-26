package Services;

import Models.Personne;

import java.util.List;

public interface IService<T> {
    void Create(T t) throws Exception;
    void Update(T t)throws Exception;
    List<T> Display()throws Exception;
    void Delete()throws Exception;
}
