package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class VeteranEntity extends BaseEntity {

    //(unique): Internal Control Number; needed for queries to Lighthouse Health API
    private String icn;
    //ommon identifier used by BGS
    private String participantId;
}
