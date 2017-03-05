package gr.uoa.di.entities;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by skand on 2/5/2017.
 */
@Table(name = "elderlyResponsible")
public class ElderlyResponsiblePK implements Serializable{

        private Long elderlyUser;
        private Long responsibleUser;

        public ElderlyResponsiblePK(){
        }


    }

