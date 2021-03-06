package gr.uoa.di.repository;

import gr.uoa.di.entities.User;
import gr.uoa.di.entities.Wifi;
import gr.uoa.di.entities.WifiInZone;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface WifiInZoneRepository extends CrudRepository<WifiInZone,Long> {

    @Query("select z from WifiInZone z, Zone zone where zone.zoneId=z.zone and z.wifi = :wifi and zone.user=:user")
    public WifiInZone[] findZonesByWifiId(@Param("wifi") Wifi wifi, @Param("user")User user);
}