package org.goblintelligence.pulseboard.services.auth.data.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    @NotEmpty(message = "Username cannot be empty!")
    private String username;
    @Email(message = "Entered string is not valid email!")
    @NotEmpty(message = "Email cannot be empty!")
    private String email;
    private String name;
    private String phoneNumber;
    private String organization;
}
