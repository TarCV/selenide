package support

actual interface AsyncPredicate<T> {
    actual suspend fun test(input: T): Boolean
}
