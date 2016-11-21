package gr.uoa.di.repository;

import gr.uoa.di.entities.AccelerometerStats;
import gr.uoa.di.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by skand on 11/21/2016.
 */

@Transactional
@Repository
public interface AccelerometerStatsRepository extends CrudRepository<AccelerometerStats,Long> {
}
