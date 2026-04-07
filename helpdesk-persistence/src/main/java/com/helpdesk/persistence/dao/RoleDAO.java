package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.Role;
import java.util.List;
import java.util.Optional;

/**
 * Sistem rollerine yönelik veritabanı erişim arayüzü.
 *
 * Sorumluluklar:
 * - Mevcut rolleri listeler (ADMIN, SUPERVISOR, AGENT, CUSTOMER).
 * - ID veya isim ile tekil rol sorgular.
 * - Belirli bir kullanıcıya atanmış rolleri getirir (user_roles JOIN).
 * - Rol oluşturur, günceller veya siler.
 *
 * Kullanım zinciri:
 *   UserServiceImpl → RoleDAO → RoleDAOImpl (JdbcTemplate + MySQL)
 *
 * Not: Roller genellikle uygulama başlangıcında sabit olarak eklenir;
 * çalışma zamanında sık değiştirilmesi beklenmez.
 */
public interface RoleDAO {

    /**
     * Verilen ID'ye sahip rolü döner.
     *
     * @param id rol birincil anahtarı
     * @return rol bulunursa dolu, bulunamazsa boş Optional
     */
    Optional<Role> findById(Long id);

    /**
     * Verilen isimle eşleşen rolü döner (örn. "ADMIN", "AGENT").
     *
     * @param name aranacak rol adı
     * @return eşleşen rol varsa dolu, yoksa boş Optional
     */
    Optional<Role> findByName(String name);

    /**
     * Veritabanındaki tüm rolleri döner.
     *
     * @return tüm rol listesi; kayıt yoksa boş liste
     */
    List<Role> findAll();

    /**
     * Belirli bir kullanıcıya atanmış rolleri döner.
     * {@code user_roles} join tablosu üzerinden sorgu yapılır.
     *
     * @param userId rolleri sorgulanacak kullanıcı ID'si
     * @return kullanıcının rol listesi; atama yoksa boş liste
     */
    List<Role> findByUserId(Long userId);

    /**
     * Yeni rolü veritabanına kaydeder.
     * Oluşturulan birincil anahtar entity üzerine set edilir.
     *
     * @param role kaydedilecek rol (id null olmalı)
     * @return ID set edilmiş, kaydedilmiş rol
     */
    Role save(Role role);

    /**
     * Mevcut rol kaydını günceller (isim, açıklama, aktiflik).
     *
     * @param role güncellenecek rol (id dolu olmalı)
     */
    void update(Role role);

    /**
     * Verilen ID'ye sahip rolü kalıcı olarak siler.
     *
     * @param id silinecek rolün birincil anahtarı
     */
    void deleteById(Long id);

}
