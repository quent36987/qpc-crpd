package back.app.data.repository;


import back.app.data.model.UserModel;
import back.app.data.repository.interfaces.UserRepository;
import back.app.utils.errors.RestError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryCustom {
    @Autowired
    private UserRepository userRepository;

    public UserModel getUserById(Long id) {
        Optional<UserModel> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw RestError.USER_NOT_FOUND.get();
        }

        return user.get();
    }

    public UserModel getUserByEmail(String email) {
        Optional<UserModel> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw RestError.USER_NOT_FOUND.get();
        }

        return user.get();
    }

    public UserModel saveUser(UserModel userModel) {
        return userRepository.save(userModel);
    }
}
