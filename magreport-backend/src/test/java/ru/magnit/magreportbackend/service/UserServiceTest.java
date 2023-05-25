package ru.magnit.magreportbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;
import ru.magnit.magreportbackend.config.AuthConfig;
import ru.magnit.magreportbackend.domain.user.UserRoleTypeEnum;
import ru.magnit.magreportbackend.domain.user.UserStatusEnum;
import ru.magnit.magreportbackend.dto.inner.RoleView;
import ru.magnit.magreportbackend.dto.inner.UserView;
import ru.magnit.magreportbackend.dto.request.user.RoleRequest;
import ru.magnit.magreportbackend.dto.request.user.UserRequest;
import ru.magnit.magreportbackend.dto.request.user.UserStatusSetRequest;
import ru.magnit.magreportbackend.dto.response.user.DomainShortResponse;
import ru.magnit.magreportbackend.dto.response.user.UserNameResponse;
import ru.magnit.magreportbackend.dto.response.user.UserResponse;
import ru.magnit.magreportbackend.mapper.auth.GrantedAuthorityMapper;
import ru.magnit.magreportbackend.service.domain.LdapService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private final String NAME = "Test";
    private final String DOMAIN = "MAGREPORT_LOCAL";
    private final UserStatusEnum STATUS = UserStatusEnum.ACTIVE;

    @InjectMocks
    private UserService service;

    @Mock
    private UserDomainService domainService;

    @Mock
    private LdapService ldapService;

    @Mock
    private GrantedAuthorityMapper grantedAuthorityMapper;

    @Mock
    private AuthConfig authConfig;

    @Test
    void getUserAuthorities() {
        when(domainService.getUserRoles(any(), any(), any())).thenReturn(Collections.singletonList(new RoleView()));
        when(grantedAuthorityMapper.from(Collections.singletonList(any()))).thenReturn(Collections.singletonList(getGrantedAuthority(new RoleView().setName(NAME))));

        List<GrantedAuthority> grantedAuthorities = service.getUserAuthorities(NAME, DOMAIN);
        assertNotNull(grantedAuthorities);
        assertNotEquals(0, grantedAuthorities.size());


        verify(domainService).getUserRoles(any(),any(), any());
        verify(grantedAuthorityMapper).from(Collections.singletonList(any()));
        verifyNoMoreInteractions(grantedAuthorityMapper, domainService);
    }

    public GrantedAuthority getGrantedAuthority(RoleView source) {
        return source::getName;
    }

    @Test
    void getUserRoles() {
        when(domainService.getUserRoles(any(), any(), any())).thenReturn(Collections.singletonList(new RoleView().setName(NAME)));

        List<String> roles = service.getUserRoles(NAME, DOMAIN);
        assertNotNull(roles);
        assertNotEquals(0, roles.size());
    }

    @Test
    void getCurrentUserName() {
        when(domainService.getCurrentUserName()).thenReturn(new UserNameResponse(NAME, DOMAIN));

        final var result = service.getCurrentUserName();

        assertEquals(NAME, result.name());
        assertEquals(DOMAIN, result.domain());

        verify(domainService).getCurrentUserName();
        verifyNoMoreInteractions(domainService, ldapService, grantedAuthorityMapper);
    }

    @Test
    void loginUserTest1() {

        when(ldapService.getUserFullName(any(), any())).thenReturn("Ivanov Ivan Ivanovich");
        when(ldapService.getUserEmail(any(), any())).thenReturn("ivanov_ii@magnit.ru");
        when(domainService.getOrCreateUserByName(any())).thenReturn(getUserView(UserStatusEnum.ACTIVE));
        when(authConfig.getDefaultDomain()).thenReturn("TEST_DOMAIN");

        var user = service.loginUser(null, NAME, Collections.emptyList());

        assertNotNull(user);

        verify(authConfig).getDefaultDomain();
        verify(ldapService).getUserFullName(any(), any());
        verify(ldapService).getUserEmail(any(), any());
        verify(domainService).removeDeletedRoles(any(),any(),any());
        verify(domainService).addInsertedRoles(any(),any(),any());
        verify(domainService).getDomainGroupRoles(anyList());
        verify(domainService).getUserRoles(any(),any(),any());
        verify(domainService).getOrCreateUserByName(any());

        verifyNoMoreInteractions(authConfig, domainService, ldapService);
    }

    @Test
    void loginUserTest2() {

        when(ldapService.getUserFullName(any(), any())).thenReturn("Ivanov Ivan Ivanovich");
        when(ldapService.getUserEmail(any(), any())).thenReturn("ivanov_ii@magnit.ru");
        when(domainService.getOrCreateUserByName(any())).thenReturn(getUserView(UserStatusEnum.LOGGED_OFF));
        when(authConfig.getDefaultDomain()).thenReturn("TEST_DOMAIN");

        var user = service.loginUser(null, NAME, Collections.emptyList());

        assertNotNull(user);

        verify(authConfig).getDefaultDomain();
        verify(ldapService).getUserFullName(any(), any());
        verify(ldapService).getUserEmail(any(), any());
        verify(domainService).removeDeletedRoles(any(),any(),any());
        verify(domainService).addInsertedRoles(any(),any(),any());
        verify(domainService).getDomainGroupRoles(anyList());
        verify(domainService).getUserRoles(any(),any(),any());
        verify(domainService).getOrCreateUserByName(any());
        verify(domainService).setUserStatus(anyList(),any());

        verifyNoMoreInteractions(authConfig, domainService, ldapService);
    }

    @Test
    void loginUserTest3() {

        when(ldapService.getUserFullName(any(), any())).thenReturn("Ivanov Ivan Ivanovich");
        when(ldapService.getUserEmail(any(), any())).thenReturn("ivanov_ii@magnit.ru");
        when(domainService.getOrCreateUserByName(any())).thenReturn(getUserView(UserStatusEnum.DISABLED));
        when(authConfig.getDefaultDomain()).thenReturn("TEST_DOMAIN");

        var user = service.loginUser(null, NAME, Collections.emptyList());

        assertNull(user);

        verify(authConfig).getDefaultDomain();
        verify(ldapService).getUserFullName(any(), any());
        verify(ldapService).getUserEmail(any(), any());
        verify(domainService).getOrCreateUserByName(any());


        verifyNoMoreInteractions(authConfig, domainService, ldapService);
    }

    @Test
    void loginUserException() {

        var user = service.loginUser(null, NAME, Collections.emptyList());
        assertNull(user);
    }

    @Test
    void getAllUsers() {
        when(domainService.showAllUsers()).thenReturn(Collections.singletonList(new UserResponse()));

        final var result = service.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(domainService).showAllUsers();

        verifyNoMoreInteractions(domainService, ldapService, grantedAuthorityMapper);
    }

    @Test
    void getUserStatus() {

        //user exists
        when(domainService.getUserByName(any(), anyString())).thenReturn(new UserView().setStatus(STATUS));

        var result = service.getUserStatus(NAME, DOMAIN);

        assertNotNull(result);
        assertEquals(STATUS, result);

        verify(domainService).getUserByName(any(), any());

        verifyNoMoreInteractions(domainService, ldapService, grantedAuthorityMapper);

        Mockito.reset(domainService);


        //user not exists
        when(domainService.getUserByName(any(), any())).thenReturn(null);

        result = service.getUserStatus(NAME, DOMAIN);

        assertNull(result);

        verify(domainService).getUserByName(any(), any());

        verifyNoMoreInteractions(domainService, ldapService, grantedAuthorityMapper);
    }

    @Test
    void logoffUsers() {

        // usernames provided
        var userNames = Collections.singletonList(1L);
        service.logoffUsers(userNames);

        verify(domainService).setUserStatus(Collections.singletonList(1L), UserStatusEnum.LOGGED_OFF);

        verifyNoMoreInteractions(domainService, ldapService, grantedAuthorityMapper);

        Mockito.reset(domainService);

        //usernames are empty or null
        userNames = Collections.emptyList();

        when(domainService.getCurrentUser()).thenReturn(new UserView().setId(2L));
        service.logoffUsers(userNames);

        verify(domainService).setUserStatus(Collections.singletonList(2L), UserStatusEnum.LOGGED_OFF);

        verifyNoMoreInteractions(domainService, ldapService, grantedAuthorityMapper);
    }

    @Test
    void getAllStatuses() {

        final var result = service.getAllStatuses();

        assertNotNull(result);
        assertEquals(UserStatusEnum.values().length, result.size());
        assertTrue(result.contains(UserStatusEnum.ACTIVE.name()));
        assertTrue(result.contains(UserStatusEnum.DISABLED.name()));
        assertTrue(result.contains(UserStatusEnum.LOGGED_OFF.name()));

        verifyNoInteractions(domainService, ldapService, grantedAuthorityMapper);
    }

    @Test
    void setUserStatus() {
        final var request = spy(getUserStatusSetRequest());
        service.setUserStatus(request);

        verify(request).getUserIds();
        verify(request).getStatus();
        verify(domainService).setUserStatus(anyList(), any());

        verifyNoMoreInteractions(request, domainService, ldapService, grantedAuthorityMapper);
    }

    @Test
    void getUserResponse() {
        final var request = spy(getUserRequest());

        when(domainService.getUserResponse(any(), any())).thenReturn(new UserResponse());

        final var result = service.getUserResponse(request);

        assertNotNull(result);

        verify(request).getUserName();
        verify(request).getDomain();
        verify(domainService).getUserResponse(any(), any());

        verifyNoMoreInteractions(request, domainService, ldapService, grantedAuthorityMapper);
    }

    @Test
    void clearRoles() {
        service.clearRoles(NAME, DOMAIN, Collections.singletonList("role"));

        verify(domainService).getUserRolesIds(any(), any(), anyList());
        verify(domainService).deleteUserRoles(anyList());

        verifyNoMoreInteractions(domainService, ldapService, grantedAuthorityMapper);
    }

    @Test
    void setUserRoles() {
        service.setUserRoles(NAME, DOMAIN, Collections.singletonList("role"), UserRoleTypeEnum.ASM);

        verify(domainService).setUserRoles(any(), anyString(), anyList(), any());

        verifyNoMoreInteractions(domainService, ldapService, grantedAuthorityMapper);
    }

    @Test
    void logoffAllUsers() {
        service.logoffAllUsers();

        verify(domainService).setAllUsersStatus(UserStatusEnum.LOGGED_OFF);
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void getAllActualUsers() {

        when(domainService.getNotArchiveUsers()).thenReturn(Collections.singletonList(new UserResponse()));

        var response = service.getAllActualUsers();

        assertEquals(1, response.size());

        verify(domainService).getNotArchiveUsers();
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void getUsersByRole() {

        when(domainService.getActualUsersByRole(any())).thenReturn(Collections.singletonList(new UserResponse()));

        var response = service.getUsersByRole(new RoleRequest().setId(1L));

        assertEquals(1, response.size());

        verify(domainService).getActualUsersByRole(any());
        verifyNoMoreInteractions(domainService);
    }

    @Test
    void checkStatusUsersOff() {

        ReflectionTestUtils.setField(service, "updateUserInfo", false);
        service.checkStatusUsers();
        verifyNoInteractions(domainService, ldapService);
    }

    @Test
    void checkStatusUsers() {

        ReflectionTestUtils.setField(service, "updateUserInfo", true);
        when(domainService.getNotArchiveUsers()).thenReturn(Arrays.asList(getUserResponseObject("Test1"), getUserResponseObject(""), getUserResponseObject("Test2")));
        when(ldapService.getUserInfo(anyList())).thenReturn(getUserInfo());

        service.checkStatusUsers();

        verify(domainService).getNotArchiveUsers();
        verify(ldapService).getUserInfo(anyList());
        verify(domainService).setUserStatus(anyList(),eq(UserStatusEnum.ARCHIVE));
        verify(domainService, times(2)).editUser(any());
        verifyNoMoreInteractions(domainService, ldapService);
    }

    private UserRequest getUserRequest() {
        return new UserRequest()
                .setUserName(NAME);
    }

    private UserStatusSetRequest getUserStatusSetRequest() {
        return new UserStatusSetRequest()
                .setStatus(STATUS)
                .setUserIds(Collections.singletonList(1L));
    }

    private UserView getUserView(UserStatusEnum status) {
        return new UserView()
                .setName("Ivanov_II")
                .setDomain(new DomainShortResponse( 1L,"TEST_DOMAIN"))
                .setStatus(status);
    }

    private UserResponse getUserResponseObject(String name){
        return new UserResponse()
                .setId(1L)
                .setDomain(new DomainShortResponse(1L, "TEST_DOMAIN"))
                .setName(name)
                .setStatus(UserStatusEnum.ACTIVE);
    }

    private Map<String, Pair<String,String>> getUserInfo(){
        var result = new HashMap<String, Pair<String,String>>();
                result.put("TEST_DOMAIN\\Test1", new Pair<>("new email","new name"));
                result.put("TEST_DOMAIN\\Test2", new Pair<>("new email","new name user"));
                return result;
    }
}
