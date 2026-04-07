package com.helpdesk.persistence.dao;

import com.helpdesk.domain.entity.Comment;
import java.util.List;
import java.util.Optional;

/**
 * Ticket yorumlarına yönelik veritabanı erişim arayüzü.
 *
 * Sorumluluklar:
 * - Ticket'a ait tüm yorumları veya sadece dahili (is_internal=true) yorumları listeler.
 * - Yeni yorum kaydeder; oluşturulan ID entity üzerine set edilir.
 * - ID ile tekil yorum sorgular ya da siler.
 *
 * Erişim kuralları (uygulama katmanında zorlanır):
 * - findByTicketId   : tüm roller ticket'a ait public yorumları görebilir.
 * - findInternalByTicketId : yalnızca AGENT ve SUPERVISOR rollerine açıktır;
 *                             CUSTOMER bu metodu çağırmamalıdır.
 */
public interface CommentDAO {

    /**
     * Verilen ID'ye sahip yorumu döner.
     *
     * @param id yorum birincil anahtarı
     * @return yorum bulunursa dolu, bulunamazsa boş Optional
     */
    Optional<Comment> findById(Long id);

    /**
     * Belirli bir ticket'a ait tüm yorumları oluşturulma tarihine göre
     * artan sırada döner (is_internal değerinden bağımsız).
     *
     * @param ticketId yorumların ait olduğu ticket ID'si
     * @return sıralı yorum listesi; yorum yoksa boş liste
     */
    List<Comment> findByTicketId(Long ticketId);

    /**
     * Belirli bir ticket'a ait yalnızca dahili (is_internal = true) yorumları döner.
     * Bu yorumlar müşteri arayüzünde gizlenir; sadece AGENT / SUPERVISOR görebilir.
     *
     * @param ticketId sorgulanacak ticket ID'si
     * @return dahili yorum listesi; yok ise boş liste
     */
    List<Comment> findInternalByTicketId(Long ticketId);

    /**
     * Yeni yorumu veritabanına kaydeder.
     * Başarılı INSERT sonrasında üretilen birincil anahtar
     * {@code comment.setId(...)} ile entity üzerine yazılır.
     *
     * @param comment kaydedilecek yorum entity'si (id null olmalı)
     * @return ID set edilmiş, kaydedilmiş yorum entity'si
     */
    Comment save(Comment comment);

    /**
     * Verilen ID'ye sahip yorumu kalıcı olarak siler.
     * Kayıt bulunamazsa sessizce tamamlanır (etkilenen satır = 0).
     *
     * @param id silinecek yorumun birincil anahtarı
     */
    void deleteById(Long id);
}
