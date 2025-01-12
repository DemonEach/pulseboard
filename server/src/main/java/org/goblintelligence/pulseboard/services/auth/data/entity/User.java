package org.goblintelligence.pulseboard.services.auth.data.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Data
@Table(name = "user")
public class User {

    @Id
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String name;
    private String phoneNumber;
    private String organization;
    private Boolean enabled;
    private OffsetDateTime creationTime;
    private OffsetDateTime updateTime;
}
