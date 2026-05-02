package rs.ac.bg.etf.barberbooker.utils.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SessionExpiredEventBus {
    private val _sessionExpired = MutableSharedFlow<Unit>()
    val sessionExpired = _sessionExpired.asSharedFlow()

    suspend fun notifySessionExpired() {
        _sessionExpired.emit(Unit)
    }
}