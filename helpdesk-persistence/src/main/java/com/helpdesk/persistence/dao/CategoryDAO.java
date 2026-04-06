package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.Category;
import java.util.List;
import java.util.Optional;

/**
 * Ticket kategorilerine yönelik veritabanı erişim arayüzü.
 *
 * Sorumluluklar:
 * - Tüm kategorileri veya yalnızca aktif olanları listeler.
 * - ID veya isim ile tekil kategori sorgular.
 * - Yeni kategori kaydeder, mevcudu günceller ya da siler.
 *
 * Kullanım zinciri:
 *   CategoryServiceImpl → CategoryDAO → CategoryDAOImpl (JdbcTemplate + MySQL)
 *
 * Not: Kategori silme işlemi, ilişkili ticket kayıtları varsa
 * FK kısıtlaması nedeniyle veritabanı hatası üretir.
 * Önce kategoriyi pasif (is_active = false) yapmak daha güvenlidir.
 */
public interface CategoryDAO {

    /**
     * Verilen ID'ye sahip kategoriyi döner.
     *
     * @param id kategori birincil anahtarı
     * @return kategori bulunursa dolu, bulunamazsa boş Optional
     */
    Optional<Category> findById(Long id);

    /**
     * Verilen isimle eşleşen kategoriyi döner (büyük/küçük harf duyarlı).
     *
     * @param name aranacak kategori adı
     * @return eşleşen kategori varsa dolu, yoksa boş Optional
     */
    Optional<Category> findByName(String name);

    /**
     * Aktif/pasif ayrımı yapmaksızın tüm kategorileri döner.
     *
     * @return tüm kategori listesi; kayıt yoksa boş liste
     */
    List<Category> findAll();

    /**
     * Yalnızca is_active = true olan kategorileri döner.
     * Ticket oluşturma formundaki kategori dropdown'u bu metodu kullanır.
     *
     * @return aktif kategori listesi; yoksa boş liste
     */
    List<Category> findAllActive();

    /**
     * Yeni kategoriyi veritabanına kaydeder.
     * Oluşturulan birincil anahtar entity üzerine set edilir.
     *
     * @param category kaydedilecek kategori (id null olmalı)
     * @return ID set edilmiş, kaydedilmiş kategori
     */
    Category save(Category category);

    /**
     * Mevcut kategori kaydını günceller (isim, açıklama, aktiflik).
     *
     * @param category güncellenecek kategori (id dolu olmalı)
     */
    void update(Category category);

    /**
     * Verilen ID'ye sahip kategoriyi kalıcı olarak siler.
     *
     * @param id silinecek kategorinin birincil anahtarı
     */
    void deleteById(Long id);

}
