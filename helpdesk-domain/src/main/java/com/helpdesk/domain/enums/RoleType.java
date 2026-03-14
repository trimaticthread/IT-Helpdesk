package com.helpdesk.domain.enums;


/* BİLGİLENDİRME
    * Sistemdeki kullanıcı rollerini tanımlar.
    * ADMIN: Sistemin tam kontrolüne sahip, tüm işlemleri yapabilir.
    * SUPERVISOR: Agent'ları yönetebilir, raporları görüntüleyebilir, ancak sistem ayarlarına erişemez.
    * AGENT: Müşteri taleplerini yönetebilir, ticket'ları çözebilir, ancak diğer kullanıcıların hesaplarına erişemez.
    * CUSTOMER: Son kullanıcı, yani MÜŞTERİ , ticket oluşturur ve takip eder.
 */


public enum RoleType {
    ADMIN,
    SUPERVISOR,
    AGENT,
    CUSTOMER
}
