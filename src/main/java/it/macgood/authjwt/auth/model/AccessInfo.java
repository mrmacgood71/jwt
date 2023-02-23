package it.macgood.authjwt.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessInfo {

    @JsonProperty(value = "access_token")
    private String accessToken;
    @JsonProperty(value = "expires_in")
    private String expiresIn;
    @JsonProperty(value = "user_id")
    private String userId;
    private String email;
}
