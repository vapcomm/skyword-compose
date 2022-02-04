package online.vapcom.swcomp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Главнй класс приложения, нужен для запуска Koin
 */
class SkywordApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            if(BuildConfig.DEBUG) {
                //NOTE: ERROR поставил из-за бага в koin, см. https://github.com/InsertKoinIO/koin/issues/1188
                androidLogger(Level.ERROR)
            }

            androidContext(this@SkywordApplication)
            modules(appModule)
        }
    }
}