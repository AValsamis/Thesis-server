package gr.uoa.di.repository;

import gr.uoa.di.entities.Wifi;
import gr.uoa.di.entities.WifiInZone;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by skand on 10/26/2016.
 */
@Transactional
@Repository
public interface WifiInZoneRepository extends CrudRepository<WifiInZone,Long> {

    @Query("select z from WifiInZone z where z.wifi = :wifi")
    public WifiInZone[] findZonesByWifiId(@Param("wifi") Wifi wifi);
}