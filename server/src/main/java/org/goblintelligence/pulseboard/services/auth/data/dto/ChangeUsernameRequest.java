package org.goblintelligence.pulseboard.services.auth.data.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChangeUsernameRequest extends ChangeAccountImportantDataRequest {

    @NotEmpty(message = "Username cannot be empty!")
    private String username;
}


