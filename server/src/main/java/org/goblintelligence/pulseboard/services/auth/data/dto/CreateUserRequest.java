package org.goblintelligence.pulseboard.services.auth.data.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotEmpty(message = "Username cannot be empty!")
    private String username;
    @NotEmpty(message = "Email cannot be empty!")
    private String email;
    private String name;
    private String phoneNumber;
    private String organization;
}
