package support

actual interface Predicate<T> {
    actual fun test(input: T): Boolean
}
