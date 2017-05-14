package gr.uoa.di.repository;

import gr.uoa.di.entities.AccelerometerStats;
import gr.uoa.di.entities.DataCollectionServiceStatus;
import gr.uoa.di.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface DataCollectionServiceStatusRepository extends CrudRepository<DataCollectionServiceStatus,Long> {

    @Query("select d from DataCollectionServiceStatus d where d.elderlyUser.userId=:userId order by d.timestamp desc")
    public List<DataCollectionServiceStatus> getLatestTimestampForUser(@Param("userId") Long userId);

    @Query("select d.elderlyUser from DataCollectionServiceStatus d group by d.elderlyUser")
    public List<User> findUsersThatFallDetectionShouldRun();

}
