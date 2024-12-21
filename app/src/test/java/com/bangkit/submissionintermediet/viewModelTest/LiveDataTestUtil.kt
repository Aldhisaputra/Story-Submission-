package com.bangkit.submissionintermediet.viewModelTest

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    timeout: Long = 2,
    unit: TimeUnit = TimeUnit.SECONDS,
    beforeObserve: () -> Unit = {}
): T {
    var result: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            result = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)
    try {
        beforeObserve()
        if (!latch.await(timeout, unit)) {
            throw TimeoutException("LiveData value not set within timeout.")
        }
    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return result as T
}
