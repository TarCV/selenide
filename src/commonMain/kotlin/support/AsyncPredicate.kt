package support

expect interface AsyncPredicate<T> {
    suspend fun test(input: T): Boolean
}
