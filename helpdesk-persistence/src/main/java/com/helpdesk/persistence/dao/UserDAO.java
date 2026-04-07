package com.helpdesk.persistence.dao;

import java.util.List;
import java.util.Optional;

import com.helpdesk.domain.entity.User;

/**
 * Kullanici veritabani islemleri icin soyutlama katmani (Repository Pattern).
 * Sadece ham SQL sorgu sozlesmelerini tanimlar; gercek SQL implementasyonu
 * UserDAOImpl sinifindadir.
 *
 * Kullanim zinciri: UserServiceImpl → UserDAO → UserDAOImpl (JdbcTemplate +
 * MySQL)
 *
 * Onemli metotlar: - assignRole : Bir kullaniciya rol atar (user_roles join
 * tablosu). - getRoleName : Login sirasinda kullanicinin rolunu okur. -
 * deleteById : FK kisitlamasi nedeniyle once user_roles, sonra users siler.
 */
public interface UserDAO {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    List<User> findByGroupId(Long groupId);

    /**
     * Belirli bir role sahip aktif kullanıcıları döner.
     * Supervisor'ın ticket atama dialogunda agent listesini doldurmak için kullanılır.
     *
     * @param roleName aranacak rol adı (örn. "AGENT", "SUPERVISOR")
     * @return o role sahip ve is_active=true olan kullanıcı listesi
     */
    List<User> findByRoleName(String roleName);

    User save(User user);

    void update(User user);

    void deleteById(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void assignRole(Long Id, String roleName);

    String getRoleName(Long userId);
}
