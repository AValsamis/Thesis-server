package gr.uoa.di.repository;

import gr.uoa.di.entities.Wifi;
import gr.uoa.di.entities.Zone;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by skand on 10/16/2016.
 */
@Transactional
@Repository
public interface ZoneRepository extends CrudRepository<Zone,Long> {

//    public Zone findById(Long Id);
    public Zone findByFriendlyName(String friendlyName);
    @Query("select z.friendlyName from Zone z where z.zoneId = :zone_id")
    public String findFrienldyNameByZoneId(@Param("zone_id")Long zoneId);
//    @Query("select z from Zone z where z.wifi = :wifi")
//    public Zone[] findZonesByWifiId(@Param("wifi") Wifi wifi);

}
