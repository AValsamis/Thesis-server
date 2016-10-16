package gr.uoa.di.repository;

import gr.uoa.di.entities.Wifi;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by skand on 10/16/2016.
 */

@Transactional
@Repository
public interface WifiRepository extends CrudRepository<Wifi,Long> {

    public Wifi findByWifiId(Long wifiId);
    public Wifi findByName(String name);
    public Wifi findByMacAddress(String macAddress);
}
