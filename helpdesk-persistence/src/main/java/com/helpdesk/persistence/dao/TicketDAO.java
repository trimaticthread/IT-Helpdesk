package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.Ticket;
import com.helpdesk.domain.enums.TicketStatus;
import java.util.List;
import java.util.Optional;

/**
 * Ticket'lara yönelik veritabanı erişim arayüzü.
 *
 * Sorumluluklar:
 * - Ticket'ları tek tek veya çeşitli filtrelerle listeler.
 * - Yeni ticket kaydeder; günceller veya siler.
 * - Tüm sorgular oluşturulma tarihine göre azalan sırada döner (en yeni önce).
 *
 * Kullanım zinciri:
 *   TicketServiceImpl → TicketDAO → TicketDAOImpl (JdbcTemplate + MySQL)
 *
 * Rol bazlı kullanım:
 * - findAll()            → ADMIN / SUPERVISOR: tüm ticket'lar
 * - findByRequesterId()  → CUSTOMER: sadece kendi oluşturduğu ticket'lar
 * - findByAssigneeId()   → AGENT: kendisine atanan ticket'lar
 * - findByGroupId()      → Grup bazlı listeleme (gelecek özellik)
 */
public interface TicketDAO {

    /**
     * Verilen ID'ye sahip ticket'ı döner.
     *
     * @param id ticket birincil anahtarı
     * @return ticket bulunursa dolu, bulunamazsa boş Optional
     */
    Optional<Ticket> findById(Long id);

    /**
     * Verilen ticket numarasıyla eşleşen kaydı döner (örn. "TKT-20240001").
     *
     * @param ticketNumber benzersiz ticket numarası
     * @return eşleşen ticket varsa dolu, yoksa boş Optional
     */
    Optional<Ticket> findByTicketNumber(String ticketNumber);

    /**
     * Tüm ticket'ları oluşturulma tarihine göre azalan sırada döner.
     * ADMIN ve SUPERVISOR panelinde kullanılır.
     *
     * @return tüm ticket listesi; kayıt yoksa boş liste
     */
    List<Ticket> findAll();

    /**
     * Belirli bir kullanıcının oluşturduğu ticket'ları döner.
     * CUSTOMER panosunda aktif kullanıcının kendi ticket'larını görmek için kullanılır.
     *
     * @param requesterId ticket'ı oluşturan kullanıcının ID'si
     * @return kullanıcıya ait ticket listesi; yoksa boş liste
     */
    List<Ticket> findByRequesterId(Long requesterId);

    /**
     * Belirli bir kullanıcıya atanmış ticket'ları döner.
     * AGENT panosunda çalışan kuyruğunu görmek için kullanılır.
     *
     * @param assigneeId ticket'a atanan kullanıcının ID'si
     * @return atanmış ticket listesi; yoksa boş liste
     */
    List<Ticket> findByAssigneeId(Long assigneeId);

    /**
     * Belirli bir durumdaki tüm ticket'ları döner.
     *
     * @param status sorgulanacak ticket durumu (örn. TicketStatus.OPEN)
     * @return eşleşen ticket listesi; yoksa boş liste
     */
    List<Ticket> findByStatus(TicketStatus status);

    /**
     * Belirli bir gruba atanmış ticket'ları döner.
     *
     * @param groupId sorgulanacak grup ID'si
     * @return gruba ait ticket listesi; yoksa boş liste
     */
    List<Ticket> findByGroupId(Long groupId);

    /**
     * Belirli bir kategoriye ait ticket'ları döner.
     *
     * @param categoryId sorgulanacak kategori ID'si
     * @return kategoriye ait ticket listesi; yoksa boş liste
     */
    List<Ticket> findByCategoryId(Long categoryId);

    /**
     * Yeni ticket'ı veritabanına kaydeder.
     * Oluşturulan birincil anahtar entity üzerine set edilir.
     *
     * @param ticket kaydedilecek ticket (id null olmalı)
     * @return ID set edilmiş, kaydedilmiş ticket
     */
    Ticket save(Ticket ticket);

    /**
     * Mevcut ticket kaydını günceller (başlık, açıklama, durum, öncelik,
     * kategori, atanan kullanıcı, grup).
     *
     * @param ticket güncellenecek ticket (id dolu olmalı)
     */
    void update(Ticket ticket);

    /**
     * Verilen ID'ye sahip ticket'ı kalıcı olarak siler.
     * İlişkili yorum ve ek dosyaların önce silinmesi gerekir.
     *
     * @param id silinecek ticket'ın birincil anahtarı
     */
    void deleteById(Long id);

}
