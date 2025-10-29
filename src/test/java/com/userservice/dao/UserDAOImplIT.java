package com.userservice.dao;

import com.userservice.base.BaseIntegrationTest;
import com.userservice.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Интеграционные тесты для UserDAOImpl
 */
@DisplayName("UserDAO Integration Tests")
class UserDAOImplIT extends BaseIntegrationTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUpDAO() {
        // Создаем DAO с тестовой SessionFactory
        userDAO = new UserDAOImpl(getSessionFactory());
    }

    // ========== CREATE TESTS ==========

    @Test
    @DisplayName("Should create user with all fields")
    void testCreate_WithAllFields_ShouldSucceed() {
        // Given
        UserEntity user = new UserEntity("Ivan Ivanov", "ivan@example.com", 30);

        // When
        UserEntity created = userDAO.create(user);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull().isPositive();
        assertThat(created.getName()).isEqualTo("Ivan Ivanov");
        assertThat(created.getEmail()).isEqualTo("ivan@example.com");
        assertThat(created.getAge()).isEqualTo(30);
        assertThat(created.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should create user without age")
    void testCreate_WithoutAge_ShouldSucceed() {
        // Given
        UserEntity user = new UserEntity("Petr Petrov", "petr@example.com", null);

        // When
        UserEntity created = userDAO.create(user);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getAge()).isNull();
    }

    @Test
    @DisplayName("Should fail to create user with duplicate email")
    void testCreate_WithDuplicateEmail_ShouldFail() {
        // Given
        UserEntity user1 = new UserEntity("User1", "duplicate@example.com", 25);
        userDAO.create(user1);

        UserEntity user2 = new UserEntity("User2", "duplicate@example.com", 30);

        // When & Then
        assertThatThrownBy(() -> userDAO.create(user2))
            .isInstanceOf(RuntimeException.class);
    }

    // ========== READ TESTS ==========

    @Test
    @DisplayName("Should find user by id")
    void testFindById_ExistingUser_ShouldReturnUser() {
        // Given
        UserEntity user = new UserEntity("Test UserEntity", "test@example.com", 25);
        UserEntity created = userDAO.create(user);

        // When
        Optional<UserEntity> found = userDAO.findById(created.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(created.getId());
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should return empty when user not found by id")
    void testFindById_NonExistingUser_ShouldReturnEmpty() {
        // When
        Optional<UserEntity> found = userDAO.findById(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindByEmail_ExistingUser_ShouldReturnUser() {
        // Given
        UserEntity user = new UserEntity("Email Test", "findme@example.com", 28);
        userDAO.create(user);

        // When
        Optional<UserEntity> found = userDAO.findByEmail("findme@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("findme@example.com");
        assertThat(found.get().getName()).isEqualTo("Email Test");
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void testFindByEmail_NonExistingUser_ShouldReturnEmpty() {
        // When
        Optional<UserEntity> found = userDAO.findByEmail("notfound@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find all users")
    void testFindAll_MultipleUsers_ShouldReturnAll() {
        // Given
        userDAO.create(new UserEntity("User1", "user1@example.com", 20));
        userDAO.create(new UserEntity("User2", "user2@example.com", 25));
        userDAO.create(new UserEntity("User3", "user3@example.com", 30));

        // When
        List<UserEntity> users = userDAO.findAll();

        // Then
        assertThat(users).hasSize(3);
        assertThat(users).extracting(UserEntity::getEmail)
            .containsExactlyInAnyOrder("user1@example.com", "user2@example.com", "user3@example.com");
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testFindAll_NoUsers_ShouldReturnEmptyList() {
        // When
        List<UserEntity> users = userDAO.findAll();

        // Then
        assertThat(users).isEmpty();
    }

    // ========== UPDATE TESTS ==========

    @Test
    @DisplayName("Should update user successfully")
    void testUpdate_ExistingUser_ShouldSucceed() {
        // Given
        UserEntity user = new UserEntity("Original Name", "original@example.com", 25);
        UserEntity created = userDAO.create(user);

        // When
        created.setName("Updated Name");
        created.setEmail("updated@example.com");
        created.setAge(30);
        UserEntity updated = userDAO.update(created);

        // Then
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
        assertThat(updated.getAge()).isEqualTo(30);

        // Verify in database
        Optional<UserEntity> found = userDAO.findById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("Should update only changed fields")
    void testUpdate_PartialUpdate_ShouldSucceed() {
        // Given
        UserEntity user = new UserEntity("Test", "test@example.com", 25);
        UserEntity created = userDAO.create(user);

        // When
        created.setName("New Name");
        userDAO.update(created);

        // Then
        Optional<UserEntity> found = userDAO.findById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("New Name");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getAge()).isEqualTo(25);
    }

    // ========== DELETE TESTS ==========

    @Test
    @DisplayName("Should delete existing user")
    void testDelete_ExistingUser_ShouldReturnTrue() {
        // Given
        UserEntity user = new UserEntity("To Delete", "delete@example.com", 25);
        UserEntity created = userDAO.create(user);

        // When
        boolean deleted = userDAO.delete(created.getId());

        // Then
        assertThat(deleted).isTrue();
        Optional<UserEntity> found = userDAO.findById(created.getId());
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return false when deleting non-existing user")
    void testDelete_NonExistingUser_ShouldReturnFalse() {
        // When
        boolean deleted = userDAO.delete(999L);

        // Then
        assertThat(deleted).isFalse();
    }

    @Test
    @DisplayName("Should verify deletion removes user from database")
    void testDelete_VerifyRemoval_ShouldNotExist() {
        // Given
        UserEntity user1 = new UserEntity("User1", "user1@example.com", 20);
        UserEntity user2 = new UserEntity("User2", "user2@example.com", 25);
        userDAO.create(user1);
        UserEntity created2 = userDAO.create(user2);

        // When
        userDAO.delete(created2.getId());

        // Then
        List<UserEntity> users = userDAO.findAll();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("user1@example.com");
    }

    // ========== EXISTS TESTS ==========

    @Test
    @DisplayName("Should return true when email exists")
    void testExistsByEmail_ExistingEmail_ShouldReturnTrue() {
        // Given
        UserEntity user = new UserEntity("Test", "exists@example.com", 25);
        userDAO.create(user);

        // When
        boolean exists = userDAO.existsByEmail("exists@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void testExistsByEmail_NonExistingEmail_ShouldReturnFalse() {
        // When
        boolean exists = userDAO.existsByEmail("notexists@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    // ========== EDGE CASES ==========

    @Test
    @DisplayName("Should handle user with minimum valid age")
    void testCreate_WithMinAge_ShouldSucceed() {
        // Given
        UserEntity user = new UserEntity("Young UserEntity", "young@example.com", 0);

        // When
        UserEntity created = userDAO.create(user);

        // Then
        assertThat(created.getAge()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle user with maximum valid age")
    void testCreate_WithMaxAge_ShouldSucceed() {
        // Given
        UserEntity user = new UserEntity("Old UserEntity", "old@example.com", 150);

        // When
        UserEntity created = userDAO.create(user);

        // Then
        assertThat(created.getAge()).isEqualTo(150);
    }

    @Test
    @DisplayName("Should maintain data integrity across multiple operations")
    void testMultipleOperations_ShouldMaintainIntegrity() {
        // Given
        UserEntity user1 = userDAO.create(new UserEntity("User1", "user1@example.com", 20));
        UserEntity user2 = userDAO.create(new UserEntity("User2", "user2@example.com", 25));
        UserEntity user3 = userDAO.create(new UserEntity("User3", "user3@example.com", 30));

        // When
        user1.setName("Updated User1");
        user2.setName("Updated User2");
        userDAO.update(user1);
        userDAO.update(user2);
        userDAO.delete(user3.getId());

        // Then
        List<UserEntity> users = userDAO.findAll();
        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserEntity::getName)
            .containsExactlyInAnyOrder("Updated User1", "Updated User2");
    }
}
