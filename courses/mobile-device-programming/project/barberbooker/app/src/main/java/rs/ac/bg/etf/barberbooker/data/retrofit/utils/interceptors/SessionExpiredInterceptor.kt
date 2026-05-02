package rs.ac.bg.etf.barberbooker.data.retrofit.utils.interceptors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import rs.ac.bg.etf.barberbooker.utils.events.SessionExpiredEventBus

class SessionExpiredInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == HTTP_UNAUTHORIZED) {
            CoroutineScope(Dispatchers.Default).launch {
                SessionExpiredEventBus.notifySessionExpired()
            }
        }
        return response
    }
}