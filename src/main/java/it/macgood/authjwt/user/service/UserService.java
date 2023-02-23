package it.macgood.authjwt.user;

import it.macgood.authjwt.user.model.User;
import it.macgood.authjwt.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public User findById(Integer id) {

        return userRepository.findById(id).orElseThrow();
    }
}
