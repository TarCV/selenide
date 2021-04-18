package support

interface Predicate<T> {
    operator fun invoke(input: T): Boolean
}
