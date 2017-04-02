package gr.uoa.di.repository;

import gr.uoa.di.entities.User;
import gr.uoa.di.entities.Wifi;
import gr.uoa.di.entities.Zone;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Query("select z from Zone z where z.user.username = :userName  and z.isSafe=1")
    public List<Zone> findUserSafeZones(@Param("userName") String userName);

    @Query("select z from Zone z where z.user.username = :userName  and z.isSafe=0")
    public List<Zone> findUserDangerZones(@Param("userName") String userName);

    @Query("select z from Zone z where z.user.userId = :userId")
    public List<Zone> findZonesByUserId(@Param("userId") Long userId);

}
