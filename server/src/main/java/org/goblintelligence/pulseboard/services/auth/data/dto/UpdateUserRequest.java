package org.goblintelligence.pulseboard.services.auth.data.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @NotNull(message = "Id cannot be empty!")
    private Integer id;
    private String name;
    private String phoneNumber;
    private String organization;
}
