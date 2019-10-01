package nolambda.playground.coruoutine

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> ReceiveChannel<T>.asFlow(): Flow<T> {
    return flow {
        consumeEach { channelItem ->
            emit(channelItem)
        }
    }
}