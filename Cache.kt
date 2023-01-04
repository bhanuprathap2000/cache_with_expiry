import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import java.util.concurrent.ConcurrentMap
import kotlin.concurrent.timerTask

class Cache(
    private val map: ConcurrentMap<String, Int>,
) {
    private var expiryInMillis: Int = 3 * 10000

    init {
        Timer().scheduleAtFixedRate(timerTask {
            cleanMap()
        }, expiryInMillis.toLong(), expiryInMillis.toLong())
    }

    /**
     * This method is used to clean the keys that are no longer needed
     * Since we put value as current time at fixed rate this method is executed and check
     * if current time is greater than keys value if yes then remove them
     * */
    private fun cleanMap() {
        log.info("Inside the cleanup method")
        val currentTime: Int = LocalTime.now().millisOfDay
        for (key in map.keys) {
            if (currentTime > (map[key]?.plus(expiryInMillis)!!)) {
                val value = map[key]
                map.remove(key)
                println("Removing : " + LocalDateTime.now() + " : " + key + " : " + value)
            }
        }
    }

    /**
     *This method accepts any object calculates it's hashcode and tries to add the key if not present
     * This is done by using the putIfAbsent method that is if key already existed then it will return the value
     * else it will insert and return null, and now we check if that is null then return false(key don't exists)
     * If key existed then it will return value, so we will return true(that is key exists)
     * */
    fun checkForDuplicateRequest(value: Any): Boolean {
        log.info("Inside checkForDuplicateRequest")
        val key = value.hashCode().toString()
        return map.putIfAbsent(key, LocalTime.now().millisOfDay) != null
    }
}
