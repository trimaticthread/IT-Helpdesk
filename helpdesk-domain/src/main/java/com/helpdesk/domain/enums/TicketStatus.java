package com.helpdesk.domain.enums;


/* BİLGİLENDİRME
*   Bu sayfa ticketların ne durumlarda çalışacağını anlatır.
*
*   NEW: yeni oluşturuldu , kimse bakmadı
*   OPEN: ticket acildi bir agent' a atanmayı bekliyor
*   IN_PROGRESS: bir agent ticket üzerinde çalışıyor
*   PENDING: ticket beklemede, müşteri veya üçüncü taraf bir bilgi sağlayana kadar bekliyor
*   RESOLVED: ticket çözüldü, ancak henüz kapatılmadı
*   CLOSED: ticket kapatıldı, artık üzerinde işlem yapılmaz
* */

public enum TicketStatus {

    NEW,
    OPEN,
    IN_PROGRESS,
    PENDING,
    RESOLVED,
    CLOSED
}
