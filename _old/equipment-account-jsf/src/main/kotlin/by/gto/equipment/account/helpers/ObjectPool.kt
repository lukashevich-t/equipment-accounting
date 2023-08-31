//package by.gto.equipment.account.helpers
//
//import java.util.concurrent.ConcurrentLinkedQueue
//import java.util.concurrent.Executors
//import java.util.concurrent.ScheduledExecutorService
//import java.util.concurrent.TimeUnit
//
//
//// по мотивам https://www.javacodegeeks.com/2013/08/simple-and-lightweight-pool-implementation.html
//abstract class ObjectPool<T> {
//    private var pool: ConcurrentLinkedQueue<T>? = null
//
//    private var executorService: ScheduledExecutorService? = null
//
//    /**
//     * Creates the pool.
//     *
//     * @param minIdle minimum number of objects residing in the pool
//     */
//    constructor(minIdle: Int) {
//        // initialize pool
//        initialize(minIdle)
//    }
//
//    /**
//     * Creates the pool.
//     *
//     * @param minIdle            minimum number of objects residing in the pool
//     * @param maxIdle            maximum number of objects residing in the pool
//     * @param validationInterval time in seconds for periodical checking of minIdle / maxIdle conditions in a separate thread.
//     * When the number of objects is less than minIdle, missing instances will be created.
//     * When the number of objects is greater than maxIdle, too many instances will be removed.
//     */
//    constructor(minIdle: Int, maxIdle: Int, validationInterval: Long) {
//        // initialize pool
//        initialize(minIdle)
//
//        // check pool conditions in a separate thread
//        executorService = Executors.newSingleThreadScheduledExecutor()
//        executorService!!.scheduleWithFixedDelay({
//            val size = pool!!.size
//            if (size < minIdle) {
//                val sizeToBeAdded = minIdle - size
//                for (i in 0 until sizeToBeAdded) {
//                    pool!!.add(createObject())
//                }
//            } else if (size > maxIdle) {
//                val sizeToBeRemoved = size - maxIdle
//                for (i in 0 until sizeToBeRemoved) {
//                    pool!!.poll()
//                }
//            }
//        }, validationInterval, validationInterval, TimeUnit.SECONDS)
//    }
//
//    /**
//     * Gets the next free object from the pool. If the pool doesn't contain any objects,
//     * a new object will be created and given to the caller of this method back.
//     *
//     * @return T borrowed object
//     */
//    fun borrowObject(): T {
//        var obj: T = pool!!.poll()
//        if (obj == null) {
//            obj = createObject()
//        }
//
//        return obj
//    }
//
//    /**
//     * Returns object back to the pool.
//     *
//     * @param object object to be returned
//     */
//    fun returnObject(obj: T?) {
//        if (obj == null) {
//            return
//        }
//
//        this.pool!!.offer(obj)
//    }
//
//    /**
//     * Shutdown this pool.
//     */
//    fun shutdown() {
//        executorService?.shutdown()
//    }
//
//    /**
//     * Creates a new object.
//     *
//     * @return T new object
//     */
//    protected abstract fun createObject(): T
//
//    private fun initialize(minIdle: Int) {
//        pool = ConcurrentLinkedQueue()
//
//        for (i in 0 until minIdle) {
//            pool!!.add(createObject())
//        }
//    }
//}