package org.goblintelligence.pulseboard.services.auth.data.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class ChangeUserImportantDataRequest {

    @NotNull(message = "User id cannot be empty!")
    private Integer userId;
    @NotEmpty(message = "Current password cannot be empty!")
    private String currentPassword;
}
