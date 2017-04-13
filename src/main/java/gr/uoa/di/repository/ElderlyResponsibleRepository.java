package gr.uoa.di.repository;

import gr.uoa.di.entities.ElderlyResponsible;
import gr.uoa.di.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by skand on 2/5/2017.
 */
@Transactional
@Repository
public interface ElderlyResponsibleRepository extends CrudRepository<ElderlyResponsible,Long> {

    @Query("select e.elderlyUser.userId from ElderlyResponsible e where e.responsibleUser.userId = :userId")
    public Long findAssociatedElderly(@Param("userId")Long userId);

    @Query("select e.responsibleUser.userId from ElderlyResponsible e where  e.elderlyUser.userId= :userId")
    public Long findAssociatedGuardian(@Param("userId")Long userId);
}
