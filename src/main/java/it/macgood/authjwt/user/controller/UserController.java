package it.macgood.authjwt.user;

import com.fasterxml.jackson.annotation.JsonView;
import it.macgood.authjwt.views.View;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @JsonView(View.GetProfileInfo.class)
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(
            HttpServletRequest   request,
            HttpServletResponse  response,
            @PathVariable String id
    ) {
        String auth = request.getHeader("authorization");
        auth = auth.substring(7);

        User user = userService.findById(Integer.parseInt(id));

        if (user.getCurrentToken().equals(auth)) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
