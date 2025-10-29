package com.userservice.service;

import com.userservice.dao.UserDAO;
import com.userservice.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Юнит-тесты для UserServiceImpl с использованием Mockito
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity("Test UserEntity", "test@example.com", 30);
        testUser.setId(1L);
    }

    // ========== CREATE USER TESTS ==========

    @Test
    @DisplayName("Should create user with valid data")
    void testCreateUser_ValidData_ShouldSucceed() {
        // Given
        when(userDAO.existsByEmail("ivan@example.com")).thenReturn(false);
        when(userDAO.create(any(UserEntity.class))).thenReturn(testUser);

        // When
        UserEntity created = userService.createUser("Ivan Ivanov", "ivan@example.com", 30);

        // Then
        assertThat(created).isNotNull();
        verify(userDAO, times(1)).existsByEmail("ivan@example.com");
        verify(userDAO, times(1)).create(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should create user without age")
    void testCreateUser_WithoutAge_ShouldSucceed() {
        // Given
        when(userDAO.existsByEmail("petr@example.com")).thenReturn(false);
        when(userDAO.create(any(UserEntity.class))).thenReturn(testUser);

        // When
        UserEntity created = userService.createUser("Petr Petrov", "petr@example.com", null);

        // Then
        assertThat(created).isNotNull();
        verify(userDAO).create(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should fail to create user with null name")
    void testCreateUser_NullName_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> userService.createUser(null, "test@example.com", 30))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Name cannot be empty");

        verify(userDAO, never()).create(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should fail to create user with empty name")
    void testCreateUser_EmptyName_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> userService.createUser("   ", "test@example.com", 30))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Name cannot be empty");

        verify(userDAO, never()).create(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should fail to create user with name too long")
    void testCreateUser_NameTooLong_ShouldThrowException() {
        // Given
        String longName = "a".repeat(101);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(longName, "test@example.com", 30))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Name cannot exceed 100 characters");

        verify(userDAO, never()).create(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should fail to create user with null email")
    void testCreateUser_NullEmail_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> userService.createUser("John", null, 30))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email cannot be empty");

        verify(userDAO, never()).create(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should fail to create user with invalid email format")
    void testCreateUser_InvalidEmail_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> userService.createUser("John", "invalid-email", 30))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid email format");

        verify(userDAO, never()).create(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should fail to create user with email too long")
    void testCreateUser_EmailTooLong_ShouldThrowException() {
        // Given
        String longEmail = "a".repeat(140) + "@example.com";

        // When & Then
        assertThatThrownBy(() -> userService.createUser("John", longEmail, 30))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email cannot exceed 150 characters");

        verify(userDAO, never()).create(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should fail to create user with existing email")
    void testCreateUser_ExistingEmail_ShouldThrowException() {
        // Given
        when(userDAO.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser("John", "existing@example.com", 30))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("already exists");

        verify(userDAO, times(1)).existsByEmail("existing@example.com");
        verify(userDAO, never()).create(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should fail to create user with negative age")
    void testCreateUser_NegativeAge_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> userService.createUser("John", "john@example.com", -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Age must be between 0 and 150");

        verify(userDAO, never()).create(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should fail to create user with age too high")
    void testCreateUser_AgeTooHigh_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> userService.createUser("John", "john@example.com", 151))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Age must be between 0 and 150");

        verify(userDAO, never()).create(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should create user with boundary age values")
    void testCreateUser_BoundaryAges_ShouldSucceed() {
        // Given
        when(userDAO.existsByEmail(anyString())).thenReturn(false);
        when(userDAO.create(any(UserEntity.class))).thenReturn(testUser);

        // When & Then - age = 0
        assertThatCode(() -> userService.createUser("Young", "young@example.com", 0))
            .doesNotThrowAnyException();

        // When & Then - age = 150
        assertThatCode(() -> userService.createUser("Old", "old@example.com", 150))
            .doesNotThrowAnyException();

        verify(userDAO, times(2)).create(any(UserEntity.class));
    }

    // ========== GET USER TESTS ==========

    @Test
    @DisplayName("Should get user by id")
    void testGetUserById_ValidId_ShouldReturnUser() {
        // Given
        when(userDAO.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<UserEntity> found = userService.getUserById(1L);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(testUser);
        verify(userDAO, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when user not found by id")
    void testGetUserById_NonExistingId_ShouldReturnEmpty() {
        // Given
        when(userDAO.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<UserEntity> found = userService.getUserById(999L);

        // Then
        assertThat(found).isEmpty();
        verify(userDAO, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should fail to get user with null id")
    void testGetUserById_NullId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> userService.getUserById(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User ID must be positive");

        verify(userDAO, never()).findById(any());
    }

    @Test
    @DisplayName("Should fail to get user with negative id")
    void testGetUserById_NegativeId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> userService.getUserById(-1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User ID must be positive");

        verify(userDAO, never()).findById(any());
    }

    @Test
    @DisplayName("Should fail to get user with zero id")
    void testGetUserById_ZeroId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> userService.getUserById(0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User ID must be positive");

        verify(userDAO, never()).findById(any());
    }

    @Test
    @DisplayName("Should get user by email")
    void testGetUserByEmail_ValidEmail_ShouldReturnUser() {
        // Given
        when(userDAO.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<UserEntity> found = userService.getUserByEmail("test@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(testUser);
        verify(userDAO, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should fail to get user with empty email")
    void testGetUserByEmail_EmptyEmail_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> userService.getUserByEmail(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email cannot be empty");

        verify(userDAO, never()).findByEmail(any());
    }

    @Test
    @DisplayName("Should get all users")
    void testGetAllUsers_ShouldReturnList() {
        // Given
        List<UserEntity> users = Arrays.asList(testUser, new UserEntity("User2", "user2@example.com", 25));
        when(userDAO.findAll()).thenReturn(users);

        // When
        List<UserEntity> found = userService.getAllUsers();

        // Then
        assertThat(found).hasSize(2);
        verify(userDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testGetAllUsers_NoUsers_ShouldReturnEmptyList() {
        // Given
        when(userDAO.findAll()).thenReturn(Collections.emptyList());

        // When
        List<UserEntity> found = userService.getAllUsers();

        // Then
        assertThat(found).isEmpty();
        verify(userDAO, times(1)).findAll();
    }

    // ========== UPDATE USER TESTS ==========

    @Test
    @DisplayName("Should update user with valid data")
    void testUpdateUser_ValidData_ShouldSucceed() {
        // Given
        when(userDAO.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDAO.update(any(UserEntity.class))).thenReturn(testUser);

        // When
        UserEntity updated = userService.updateUser(1L, "New Name", "test@example.com", 35);

        // Then
        assertThat(updated).isNotNull();
        verify(userDAO, times(1)).findById(1L);
        verify(userDAO, times(1)).update(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should update user email when changed")
    void testUpdateUser_EmailChanged_ShouldCheckUniqueness() {
        // Given
        when(userDAO.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDAO.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userDAO.update(any(UserEntity.class))).thenReturn(testUser);

        // When
        userService.updateUser(1L, "Test", "newemail@example.com", 30);

        // Then
        verify(userDAO, times(1)).existsByEmail("newemail@example.com");
        verify(userDAO, times(1)).update(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should not check email uniqueness when email unchanged")
    void testUpdateUser_EmailUnchanged_ShouldNotCheckUniqueness() {
        // Given
        when(userDAO.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDAO.update(any(UserEntity.class))).thenReturn(testUser);

        // When
        userService.updateUser(1L, "New Name", "test@example.com", 30);

        // Then
        verify(userDAO, never()).existsByEmail(any());
        verify(userDAO, times(1)).update(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should fail to update with existing email")
    void testUpdateUser_ExistingEmail_ShouldThrowException() {
        // Given
        when(userDAO.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDAO.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(1L, "Test", "existing@example.com", 30))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("already exists");

        verify(userDAO, never()).update(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should fail to update non-existing user")
    void testUpdateUser_NonExistingUser_ShouldThrowException() {
        // Given
        when(userDAO.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(999L, "Test", "test@example.com", 30))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("not found");

        verify(userDAO, never()).update(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should fail to update with null id")
    void testUpdateUser_NullId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> userService.updateUser(null, "Test", "test@example.com", 30))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User ID must be positive");

        verify(userDAO, never()).update(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should fail to update with invalid name")
    void testUpdateUser_InvalidName_ShouldThrowException() {
        // Given
        when(userDAO.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(1L, "", "test@example.com", 30))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Name cannot be empty");

        verify(userDAO, never()).update(any(UserEntity.class));
    }

    // ========== DELETE USER TESTS ==========

    @Test
    @DisplayName("Should delete existing user")
    void testDeleteUser_ExistingUser_ShouldReturnTrue() {
        // Given
        when(userDAO.delete(1L)).thenReturn(true);

        // When
        boolean deleted = userService.deleteUser(1L);

        // Then
        assertThat(deleted).isTrue();
        verify(userDAO, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Should return false when deleting non-existing user")
    void testDeleteUser_NonExistingUser_ShouldReturnFalse() {
        // Given
        when(userDAO.delete(999L)).thenReturn(false);

        // When
        boolean deleted = userService.deleteUser(999L);

        // Then
        assertThat(deleted).isFalse();
        verify(userDAO, times(1)).delete(999L);
    }

    @Test
    @DisplayName("Should fail to delete with null id")
    void testDeleteUser_NullId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User ID must be positive");

        verify(userDAO, never()).delete(any());
    }

    @Test
    @DisplayName("Should fail to delete with negative id")
    void testDeleteUser_NegativeId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(-1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User ID must be positive");

        verify(userDAO, never()).delete(any());
    }

    // ========== VALIDATION TESTS ==========

    @Test
    @DisplayName("Should validate email with various valid formats")
    void testEmailValidation_ValidFormats_ShouldSucceed() {
        // Given
        when(userDAO.existsByEmail(anyString())).thenReturn(false);
        when(userDAO.create(any(UserEntity.class))).thenReturn(testUser);

        // When & Then
        assertThatCode(() -> userService.createUser("Test", "simple@example.com", 30))
            .doesNotThrowAnyException();
        
        assertThatCode(() -> userService.createUser("Test", "with+plus@example.com", 30))
            .doesNotThrowAnyException();
        
        assertThatCode(() -> userService.createUser("Test", "with.dot@example.com", 30))
            .doesNotThrowAnyException();
        
        assertThatCode(() -> userService.createUser("Test", "with_underscore@example.com", 30))
            .doesNotThrowAnyException();

        verify(userDAO, times(4)).create(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should reject invalid email formats")
    void testEmailValidation_InvalidFormats_ShouldFail() {
        // When & Then
        assertThatThrownBy(() -> userService.createUser("Test", "invalidemail.com", 30))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> userService.createUser("Test", "test@", 30))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> userService.createUser("Test", "@example.com", 30))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> userService.createUser("Test", "test @example.com", 30))
            .isInstanceOf(IllegalArgumentException.class);

        verify(userDAO, never()).create(any(UserEntity.class));
    }

    // ========== INTERACTION TESTS ==========

    @Test
    @DisplayName("Should verify correct interaction sequence on create")
    void testCreateUser_VerifyInteractionSequence() {
        // Given
        when(userDAO.existsByEmail("test@example.com")).thenReturn(false);
        when(userDAO.create(any(UserEntity.class))).thenReturn(testUser);

        // When
        userService.createUser("Test", "test@example.com", 30);

        // Then
        var inOrder = inOrder(userDAO);
        inOrder.verify(userDAO).existsByEmail("test@example.com");
        inOrder.verify(userDAO).create(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should not call create when validation fails")
    void testCreateUser_ValidationFails_ShouldNotCallDAO() {
        // When & Then
        assertThatThrownBy(() -> userService.createUser("", "test@example.com", 30))
            .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(userDAO);
    }
}
