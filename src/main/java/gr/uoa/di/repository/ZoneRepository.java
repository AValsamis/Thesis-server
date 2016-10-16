package gr.uoa.di.repository;

import gr.uoa.di.entities.Zone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by skand on 10/16/2016.
 */
@Transactional
@Repository
public interface ZoneRepository extends CrudRepository<Zone,Long> {

    public Zone findByZoneId(String zoneId);
}
