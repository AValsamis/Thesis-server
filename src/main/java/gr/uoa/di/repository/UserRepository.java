package gr.uoa.di.repository;

/**
 * Created by Angelos on 9/25/2016.
 */

import gr.uoa.di.entities.User;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UserRepository extends CrudRepository<User,Long> {

    public User findByUsername(String username);
}