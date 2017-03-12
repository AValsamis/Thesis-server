package gr.uoa.di.repository;

import gr.uoa.di.entities.AccelerometerStats;
import gr.uoa.di.entities.DataCollectionServiceStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by skand on 3/12/2017.
 */

@Transactional
@Repository
public interface DataCollectionServiceStatusRepository extends CrudRepository<DataCollectionServiceStatus,Long> {

    @Query("select d from DataCollectionServiceStatus d where d.elderlyUser.userId=:userId order by d.timestamp desc")
    public List<DataCollectionServiceStatus> getLatestTimestampForUser(@Param("userId") Long userId);
}