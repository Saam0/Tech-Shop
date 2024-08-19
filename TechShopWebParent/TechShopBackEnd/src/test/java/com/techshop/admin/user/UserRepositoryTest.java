package com.techshop.admin.user;

import com.techshop.common.entity.Role;
import com.techshop.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager testEntityManager;



    @Test
    public void testCreateNewUserWithOneRole(){
        Role roleAdmin = roleRepository.findById(1L).get();
        User userPoxos = new User("poxos@mail.ru","pox","Poxos","Poxosyan");
        userPoxos.addRole(roleAdmin);
        User savedUser = userRepository.save(userPoxos);
        assertThat(savedUser.getId()).isGreaterThan(0);

    }

    @Test
    public void testCreateNewUserWithTwoRole(){
        Role roleAssistant = roleRepository.findById(5L).get();
        Role roleEditor = roleRepository.findById(3L).get();

        User userGago = new User("gago@mail.ru","gag","Gagik","Kirakosyan");
        userGago.addRole(roleAssistant);
        userGago.addRole(roleEditor);
        User savedUser = userRepository.save(userGago);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllUser(){
        List<User> users = userRepository.findAll();
        users.forEach(System.out::println);
    }
    @Test
    public void testGetUserById(){
        User byId = userRepository.findById(1L).get();
        System.out.println(byId);
        assertThat(byId).isNotNull();
    }
    @Test
    public void testUpdateUserDetails() {
        User user = userRepository.findById(1L).get();
        user.setEmail("Kirakos@gmail.com");
        user.setEnabled(true);
    }
    @Test
    public void testUpdateUserRoles() {
        User user = userRepository.findById(2L).get();
        Role roleAssistant = roleRepository.findById(5L).get();
        Role roleShipper = roleRepository.findById(4L).get();
        user.getRoles().remove(roleAssistant);
        user.addRole(roleShipper);
        userRepository.save(user);

    }
    @Test
    public void testDeleteUser() {
        userRepository.deleteById(2L);
    }
    @Test
    public void testGetUserByEmail(){
        String email = "samvelyazhyan@gmail.com";
        User user = userRepository.getUserByEmail(email);
        assertThat(user).isNotNull();
    }
    @Test
    public void testDisableUser(){
        Long id = 1L;
        userRepository.updateEnabledStatus(id,true);
    }

    @Test
    public void testListFirstPage(){

        int pageSize = 4;
        int pageNumber = 0;
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<User> page = userRepository.findAll(pageable);
        List<User> userList = page.getContent();
        userList.forEach(System.out::println);
        assertThat(userList.size()).isEqualTo(pageSize);
    }

    @Test
    public void testSearchUsers(){

        String keyword="bruce";
        int pageSize = 4;
        int pageNumber = 0;
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<User> page = userRepository.findAll(keyword,pageable);
        List<User> userList = page.getContent();
        userList.forEach(System.out::println);
        assertThat(userList.size()).isGreaterThan(0);
    }

}

