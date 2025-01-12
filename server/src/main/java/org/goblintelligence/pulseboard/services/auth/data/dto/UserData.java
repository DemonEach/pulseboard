package org.goblintelligence.pulseboard.services.auth.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserData {

    private Integer id;
    private String username;
    private String email;
    private String name;
    private String phoneNumber;
    private String organization;
}
