package gr.uoa.di.repository;

import gr.uoa.di.entities.RecognizedActivityStorage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
@Repository
public interface RecognizedActivityStorageRepository extends CrudRepository<RecognizedActivityStorage,Long> {

    @Query("select r from RecognizedActivityStorage r where r.user.userId=:userId order by r.id desc")
    public List<RecognizedActivityStorage> getLastActivity(@Param("userId") Long userId);

    @Query("select r from RecognizedActivityStorage r where r.user.userId=:userId and r.timestamp<:timestamp order by r.id desc")
    public List<RecognizedActivityStorage> getActivityBeforeImpact(@Param("userId") Long userId, @Param("timestamp") Date timestamp);

    @Query("select r from RecognizedActivityStorage r where r.user.userId=:userId and r.timestamp>=:timestamp order by r.id desc")
    public List<RecognizedActivityStorage> getActivityAfterImpact(@Param("userId") Long userId, @Param("timestamp") Date timestamp);

}
