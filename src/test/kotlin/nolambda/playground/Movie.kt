package nolambda.playground

import io.reactivex.Single
import io.reactivex.functions.BiFunction

data class Movie(val name: String)

interface MovieSource {
    fun getMovies(): Single<List<Movie>>
}

class MovieLocalSource : MovieSource {
    override fun getMovies(): Single<List<Movie>> = Single.just(listOf(Movie("ET"), Movie("ET 2")))
}

class MovieNetwokrSource : MovieSource {
    override fun getMovies(): Single<List<Movie>> = TODO("Network not ready")
}

class MovieFetcher(val local: MovieLocalSource, val network: MovieNetwokrSource) {
    fun getMovies(): Single<List<Movie>> = Single.zip(local.getMovies(), network.getMovies(),
            BiFunction { t1, t2 -> t1 + t2 })
}