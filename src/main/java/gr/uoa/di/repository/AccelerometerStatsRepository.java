package gr.uoa.di.repository;

import gr.uoa.di.entities.AccelerometerStats;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by skand on 11/21/2016.
 */

@Transactional
@Repository
public interface AccelerometerStatsRepository extends CrudRepository<AccelerometerStats,Long> {

    @Query("select s from AccelerometerStats s where s.timeStamp > :timestamp")
    public List<AccelerometerStats> findByTimeStamp(@Param("timestamp")String timestamp);

}
