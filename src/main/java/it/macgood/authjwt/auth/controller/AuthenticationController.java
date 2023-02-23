package it.macgood.authjwt.auth;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.objects.users.GetNameCase;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/enter")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @Value("${vk.auth.client-id}")
    private String clientId;

    @Value("${vk.auth.client-secret}")
    private String clientSecret;

    @Value("$vk.auth.access-secret")
    private String accessSecret;

    @PostMapping("/registration")
    public ResponseEntity<AuthenticationResponse> register(
            HttpServletRequest httpRequest,
            HttpServletResponse response,
            @RequestBody RegisterRequest registerRequest
    ) throws IOException {

        if (registerRequest.getEmail().isEmpty() || registerRequest.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(service.register(response, registerRequest));
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthenticationResponse> authenticate(
            HttpServletResponse response,
            @RequestBody AuthenticationRequest request
    ) {

        AuthenticationResponse authenticate = service.authenticate(request);

        return ResponseEntity.ok(authenticate);

    }

    private static final String REDIRECT_URI = "http://localhost:8080/api/v1/enter/vk";

    @GetMapping("/vk")
    public ResponseEntity<AuthenticationResponse> enterByVK(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            HttpServletResponse response
    ) throws ClientException, ApiException, IOException {

        if (code == null) {
            String auth = "https://oauth.vk.com/authorize?"
                    + "client_id=" + clientId
                    + "&display=page"
                    + "&redirect_uri=" + REDIRECT_URI
                    + "&scope=friends,email,wall,photos,offline"
                    + "&response_type=code"
                    + "&v=5.131"
                    + "&state=1";

            response.sendRedirect(auth);

            return ResponseEntity.ok(null);
        } else {

            TransportClient transportClient = new HttpTransportClient();
            VkApiClient vk = new VkApiClient(transportClient);

            UserAuthResponse authResponse = vk.oAuth()
                    .userAuthorizationCodeFlow(
                            Integer.valueOf(clientId),
                            clientSecret,
                            REDIRECT_URI,
                            code)
                    .execute();

            UserActor actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());

            List<GetResponse> execute = vk.users().get(actor)
                    .userIds(String.valueOf(actor.getId()))
                    .fields(Fields.SEX, Fields.BDATE, Fields.PHOTO_200_ORIG, Fields.SCREEN_NAME)
                    .nameCase(GetNameCase.NOMINATIVE)
                    .execute();

            GetResponse user = execute.get(0);

            VkRegisterRequest request = VkRegisterRequest.builder()
                    .email(authResponse.getEmail())
                    .password(accessSecret)
                    .firstname(user.getFirstName())
                    .lastname(user.getLastName())
                    .nickname(user.getScreenName())
                    .photo(String.valueOf(user.getPhoto200Orig()))
                    .dateOfBirth(user.getBdate())
                    .build();

            return ResponseEntity.ok(service.enterFromVk(response, request));
        }
    }

}
