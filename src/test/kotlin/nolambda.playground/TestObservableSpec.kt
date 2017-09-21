package nolambda.playground

import io.kotlintest.mock.mock
import io.kotlintest.specs.StringSpec
import io.reactivex.Single
import org.mockito.Mockito

class TestObservableSpec : StringSpec({

    val localSource = MovieLocalSource()
    val networkSource = mock<MovieNetwokrSource>()
    val tested = MovieFetcher(localSource, networkSource)

    "Local data should not empty" {
        localSource.getMovies().test().also {
            it.assertNoErrors()
            it.assertComplete()
            it.assertValueCount(1)
        }
    }

    "Verify movie not empty and error" {
        Mockito.`when`(networkSource.getMovies())
                .then { Single.just(listOf(mock<Movie>())) }

        tested.getMovies().test().also {
            it.assertNoErrors()
            it.assertComplete()
            it.assertValueCount(1)
        }
    }

})

