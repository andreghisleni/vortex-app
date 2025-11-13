package br.com.andreg.mobile.vortex.model

import java.time.Instant
import java.util.UUID

// Usando 'var' para permitir a modificação dos campos (com setters)
data class Event(
    var id: UUID,
    var name: String = "",
    var description: String,
    var ticketType: String = "",
    var ownerId: UUID,
    var autoGenerateTicketsTotalPerMember: Int,
    var readOnly: Boolean,
    var ticketRanges: List<TicketRange>
) {
    data class TicketRange(
        var id: UUID,
        var start: Int,
        var end: Int,
        var type: String = "",
        var cost: Double
    )
}