package ru.magnit.magreportbackend.service.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.magnit.magreportbackend.config.AuthConfig;
import ru.magnit.magreportbackend.domain.user.Domain;
import ru.magnit.magreportbackend.domain.user.Role;
import ru.magnit.magreportbackend.domain.user.User;
import ru.magnit.magreportbackend.domain.user.UserRole;
import ru.magnit.magreportbackend.domain.user.UserRoleType;
import ru.magnit.magreportbackend.domain.user.UserRoleTypeEnum;
import ru.magnit.magreportbackend.domain.user.UserStatus;
import ru.magnit.magreportbackend.domain.user.UserStatusEnum;
import ru.magnit.magreportbackend.dto.inner.RoleView;
import ru.magnit.magreportbackend.dto.inner.UserInfo;
import ru.magnit.magreportbackend.dto.inner.UserView;
import ru.magnit.magreportbackend.dto.response.user.DomainShortResponse;
import ru.magnit.magreportbackend.dto.response.user.UserResponse;
import ru.magnit.magreportbackend.mapper.auth.RoleViewMapper;
import ru.magnit.magreportbackend.mapper.auth.UserResponseMapper;
import ru.magnit.magreportbackend.mapper.auth.UserViewMapper;
import ru.magnit.magreportbackend.repository.DomainRepository;
import ru.magnit.magreportbackend.repository.RoleRepository;
import ru.magnit.magreportbackend.repository.UserRepository;
import ru.magnit.magreportbackend.repository.UserRoleRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserDomainServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private DomainRepository domainRepository;

    @Mock
    private UserResponseMapper userResponseMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserViewMapper userViewMapper;

    @Mock
    private RoleViewMapper roleViewMapper;

    @Mock
    private AuthConfig authConfig;


    @InjectMocks
    private UserDomainService domainService;

    private final String NAME = "Test";
    private final String DOMAIN = "MAGREPORT_LOCAL";


    @Test
    void getCurrentUserName() {

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(getAuthentication());

        var response = domainService.getCurrentUserName();

        assertNotNull(response);
    }

    @Test
    void getCurrentUser() {

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(getAuthentication());
        when(domainRepository.getByName(anyString())).thenReturn(getDomain());
        when(userRepository.getUserByNameAndDomainId(any(), any())).thenReturn(getUser());
        when(userViewMapper.from((User) any())).thenReturn(getUserView());

        var response = domainService.getCurrentUser();

        assertNotNull(response);

        verify(userRepository).getUserByNameAndDomainId(any(), any());
        verify(securityContext).getAuthentication();

        verifyNoMoreInteractions(userRepository, securityContext);
    }

    @Test
    void getOrCreateUserByName() {

        when(userRepository.existsByNameAndDomainId(any(),any())).thenReturn(true);
        when(userRepository.getUserByNameAndDomainId(any(), any())).thenReturn(new User());
        when(userViewMapper.from((User) any())).thenReturn(getUserView());
        when(domainRepository.getByName(any())).thenReturn(getDomain());


        var response = domainService.getOrCreateUserByName(getUserInfo());
        assertNotNull(response);

        verify(userRepository).existsByNameAndDomainId(any(),any());
        verify(userRepository).getUserByNameAndDomainId(any(), any());
        verify(userRepository).save(any());
        verify(userViewMapper).from((User) any());

        verifyNoMoreInteractions(userRepository, userViewMapper);

        Mockito.reset(userRepository);

        when(userRepository.existsByNameAndDomainId(any(),any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(new User());

        response = domainService.getOrCreateUserByName(getUserInfo());
        assertNotNull(response);

        verify(userRepository).existsByNameAndDomainId(any(),any());
        verify(userRepository).save(any());
        verify(userViewMapper, times(2)).from((User) any());

        verifyNoMoreInteractions(userRepository, userViewMapper);
    }

    @Test
    void getUserByName() {

        when(userRepository.getUserByNameAndDomainId(any(), any())).thenReturn(new User());
        when(userViewMapper.from((User) any())).thenReturn(getUserView());
        when(domainRepository.getByName(anyString())).thenReturn(getDomain());

        var response = domainService.getUserByName(NAME, DOMAIN);

        assertNotNull(response);

        verify(userRepository).getUserByNameAndDomainId(any(), any());
        verify(userViewMapper).from((User) any());

        verifyNoMoreInteractions(userRepository, userViewMapper);

        when(userRepository.getUserByNameAndDomainId(any(), any())).thenReturn(null);


        response = domainService.getUserByName(NAME, DOMAIN);

        assertNull(response);

        verify(userRepository, times(2)).getUserByNameAndDomainId(any(), any());
        verify(userViewMapper).from((User) any());

        verifyNoMoreInteractions(userRepository, userViewMapper);

    }

    @Test
    void addInsertedRoles() {

        domainService.addInsertedRoles(getUserView(), getListRoleView(), Collections.singletonList(getRoleView(1L)));

        verify(userRoleRepository, times(2)).save(any());
        verifyNoMoreInteractions(userRoleRepository);
    }

    @Test
    void removeDeletedRoles() {

        domainService.removeDeletedRoles(getUserView(), Collections.singletonList(getRoleView(1L)), getListRoleView());

        verify(userRoleRepository, times(2)).deleteUserRoleByUserIdAndRoleIdAndUserRoleTypeId(any(), any(), any());
        verifyNoMoreInteractions(userRoleRepository);
    }

    @Test
    void getDomainGroupRoles() {

        when(roleRepository.getRolesByDomainGroupsList(any())).thenReturn(Collections.singletonList(new Role()));
        when(roleViewMapper.from((Role) any())).thenReturn(getRoleView(1L));

        var response = domainService.getDomainGroupRoles(Collections.singletonList(""));
        assertNotNull(response);

        verify(roleRepository).getRolesByDomainGroupsList(any());
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void getUserRoles() {

        when(roleViewMapper.from((Role) any())).thenReturn(getRoleView(1L));
        when(domainRepository.getByName(anyString())).thenReturn(getDomain());
        when(userRepository.getUserByNameAndDomainId(any(), any())).thenReturn(getUser());

        var response = domainService.getUserRoles(NAME, DOMAIN,  UserRoleTypeEnum.DOMAIN);
        assertNotNull(response);

        verify(userRepository).getUserByNameAndDomainId(any(), any());
        verify(roleViewMapper, times(1)).from((Role) any());

        verifyNoMoreInteractions(roleViewMapper, userRepository);

    }

    @Test
    void getCurrentUserRoles() {

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(getAuthentication());
        when(roleViewMapper.from((Role) any())).thenReturn(getRoleView(1L));
        when(userRepository.getUserByNameAndDomainId(any(),any())).thenReturn(getUser());
        when(domainRepository.getByName(anyString())).thenReturn(getDomain());


        var response = domainService.getCurrentUserRoles(UserRoleTypeEnum.DOMAIN);
        assertNotNull(response);

        verify(userRepository).getUserByNameAndDomainId(any(), any());
        verify(roleViewMapper, times(1)).from((Role) any());

        verifyNoMoreInteractions(roleViewMapper, userRepository);

    }

    @Test
    void showAllUsers() {


        when(userResponseMapper.from(anyList())).thenReturn(Collections.singletonList(new UserResponse()));
        when(userRepository.findAll()).thenReturn(Collections.singletonList(getUser()));

        domainService.showAllUsers();

        verify(userRepository).findAll();
        verify(userResponseMapper).from(anyList());

        verifyNoMoreInteractions(userRepository, userResponseMapper);
    }

    @Test
    void setUserStatus() {
        when(userRepository.getReferenceById(any())).thenReturn(getUser());

        domainService.setUserStatus(Collections.singletonList(1L), UserStatusEnum.ACTIVE);

        verify(userRepository).getReferenceById(any());
        verify(userRepository).save(any());

        verifyNoMoreInteractions(userRepository);

    }

    @Test
    void getUserResponse() {

        when(userRepository.getUserByNameAndDomainId(any(), any())).thenReturn(getUser());
        when(userResponseMapper.from((User) any())).thenReturn(new UserResponse());
        when(domainRepository.getByName(anyString())).thenReturn(getDomain());

        var response = domainService.getUserResponse(NAME, DOMAIN);
        assertNotNull(response);

        verify(userRepository).getUserByNameAndDomainId(any(), any());
        verify(userResponseMapper).from((User) any());

        verifyNoMoreInteractions(userRepository, userResponseMapper);
    }

    @Test
    void clearRoles() {

        when(userRepository.getUserByNameAndDomainId(any(),any())).thenReturn(getUser());
        when(domainRepository.getByName(anyString())).thenReturn(getDomain());

        domainService.clearRoles(NAME, DOMAIN, Collections.emptyList());
        domainService.clearRoles(NAME, DOMAIN, Collections.singletonList("name"));

        verify(userRepository, times(2)).getUserByNameAndDomainId(any(), any());
        verify(userRoleRepository, times(3)).deleteById(any());

        verifyNoMoreInteractions(userRepository, userRoleRepository);
    }

    @Test
    void setUserRoles() {
        when(domainRepository.getByName(anyString())).thenReturn(new Domain().setId(1L));
        when(roleRepository.findAllByNameIn(anyList())).thenReturn(Collections.singletonList(new Role()));
        when(userRepository.getUserByNameAndDomainId(any(),any())).thenReturn(getUser());

        domainService.setUserRoles(NAME,DOMAIN, Collections.singletonList("role"), UserRoleTypeEnum.ASM);

        verify(roleRepository).findAllByNameIn(anyList());
        verify(userRepository).getUserByNameAndDomainId(any(), any());
        verify(userRoleRepository).save(any());

        verifyNoMoreInteractions(roleRepository, userRepository, userRoleRepository);
    }

    @Test
    void getUserRolesIds() {

        when(domainRepository.getByName(anyString())).thenReturn(new Domain().setId(1L));
        var response = domainService.getUserRolesIds("", "", Collections.emptyList());
        assertTrue(response.isEmpty());

        when(userRepository.getUserByNameAndDomainId(any(),any())).thenReturn(getUser());

        response = domainService.getUserRolesIds(NAME, DOMAIN, Collections.singletonList("name"));
        assertFalse(response.isEmpty());

        verify(userRepository, times(2)).getUserByNameAndDomainId(any(), any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUserRoles() {

        domainService.deleteUserRoles(Collections.emptyList());
        domainService.deleteUserRoles(Collections.singletonList(1L));

        verify(userRoleRepository).deleteAllByIdIn(anyList());
        verifyNoMoreInteractions(userRoleRepository);
    }

    @Test
    void setAllUsersStatus() {

        domainService.setAllUsersStatus(UserStatusEnum.DISABLED);

        verify(userRepository).setStatusToAllUsers(any());
        verifyNoMoreInteractions(userRepository);
    }

    private UserView getUserView() {
        return new UserView()
                .setId(1L)
                .setFirstName("First name")
                .setName("Name")
                .setLastName("Last name")
                .setEmail("email")
                .setPatronymic("Patronymic")
                .setDescription("description")
                .setDomain(new DomainShortResponse(1L, "Name"))
                .setStatus(UserStatusEnum.ACTIVE);
    }

    private RoleView getRoleView(Long id) {
        return new RoleView()
                .setId(id)
                .setName("name")
                .setDescription("description");
    }

    private List<RoleView> getListRoleView() {
        return Arrays.asList(
                getRoleView(1L),
                getRoleView(2L),
                getRoleView(3L)
        );
    }

    private UserInfo getUserInfo() {
        return new UserInfo()
                .setFirstName("First_name")
                .setLastName("Last_name")
                .setPatronymic("Patronymic")
                .setEmail("email")
                .setLoginName("Username");
    }

    private User getUser() {
        return new User()
                .setFirstName("First_name")
                .setLastName("Last_name")
                .setPatronymic("Patronymic")
                .setEmail("email")
                .setDomain(new Domain().setId(1L).setName("Name"))
                .setUserStatus(new UserStatus())
                .setUserRoles(Arrays.asList(
                        new UserRole()
                                .setId(0L)
                                .setUserRoleType(new UserRoleType().setId(0L))
                                .setRole(new Role()
                                        .setId(0L)
                                        .setName("name")),
                        new UserRole()
                                .setId(1L)
                                .setUserRoleType(new UserRoleType().setId(1L))
                                .setRole(new Role()
                                        .setId(1L)
                                        .setName("name")),
                        new UserRole()
                                .setId(2L)
                                .setUserRoleType(new UserRoleType().setId(3L))
                                .setRole(new Role()
                                        .setId(2L)
                                        .setName("name"))
                ));
    }

    private Authentication getAuthentication() {
        return new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return "DOMAIN";
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean b) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return "Username";
            }
        };
    }

    private Domain getDomain() {
        return new Domain();
    }
}
