package support

expect interface Predicate<T> {
    fun test(input: T): Boolean
}
