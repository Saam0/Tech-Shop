package com.techshop.admin.user;

import com.techshop.admin.paging.PagingAndSortingHelper;
import com.techshop.common.entity.Role;
import com.techshop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {

    public static final int USERS_PER_PAGE = 5;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void getAllUsersByPage(int pageNum, PagingAndSortingHelper helper) {
//        Sort sort = Sort.by(helper.getSortField());
//        sort = helper.getSortDir().equals("asc") ? sort.ascending() : sort.descending();
//        Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
//        Page<User> userPage = null;
//        if (helper.getKeyword() != null) {
//            userPage= userRepository.findAll(helper.getKeyword(), pageable);
//        }else{
//            userPage = userRepository.findAll(pageable);
//        }
//        helper.updateModelAttributes(pageNum,userPage);
        helper.listEntities(pageNum,USERS_PER_PAGE,userRepository);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public User save(User user) {

        if (user.getId() != null) {
            User existingUser = userRepository.findById(user.getId()).get();
            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                encodePassword(user);
            }
        } else {
            encodePassword(user);
        }
        return userRepository.save(user);
    }

    public User updateAccount(User userInForm) throws UserNotFoundException {
        User userInDB = findById(userInForm.getId());
        if (!userInForm.getPassword().isEmpty()){
            userInDB.setPassword(userInForm.getPassword());
            encodePassword(userInDB);
        }

        if (userInForm.getPhotos()!=null){
            userInDB.setPhotos(userInForm.getPhotos());
        }
        userInDB.setFirstName(userInForm.getFirstName());
        userInDB.setLastName(userInForm.getLastName());

        return userRepository.save(userInDB);

    }

    public User findById(Long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Could not find any user with ID " + id));
    }

    public void encodePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    public boolean isEmailUnique(Long id, String email) {
        User user = userRepository.getUserByEmail(email);
        if (user == null)
            return true;
        boolean isCreatingNew = (id == null);
        if (isCreatingNew) {
            if (user != null) {
                return false;
            }
        } else {
            if (!user.getId().equals(id)) {
                return false;
            }
        }
        return true;
    }

    public void deleteById(Long id) throws UserNotFoundException {
        User user = findById(id);
        userRepository.deleteById(id);
    }

    public void updateUserEnabledStatus(Long id, boolean enabled) {
        userRepository.updateEnabledStatus(id, enabled);
    }
}
