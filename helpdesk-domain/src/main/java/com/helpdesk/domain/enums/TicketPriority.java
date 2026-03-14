package com.helpdesk.domain.enums;

/*
    * BİLGİLENDİRME
    *  Ticketların aciliyet seviyesini belirler.

    * Low: Düşük öncelik,
    * Medium: Normal öncelik. Default
    * High: Yüksek öncelik
    * Critical: Hayati önem seviyesinde acil.
 */


public enum TicketPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
