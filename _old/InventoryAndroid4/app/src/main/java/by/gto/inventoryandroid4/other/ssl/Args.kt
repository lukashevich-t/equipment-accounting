package by.gto.inventoryandroid4.other.ssl


object Args {

    fun check(expression: Boolean, message: String) {
        if (!expression) {
            throw IllegalArgumentException(message)
        }
    }

    fun check(expression: Boolean, message: String, vararg args: Any) {
        if (!expression) {
            throw IllegalArgumentException(String.format(message, *args))
        }
    }

    fun check(expression: Boolean, message: String, arg: Any) {
        if (!expression) {
            throw IllegalArgumentException(String.format(message, arg))
        }
    }

    fun <T> notNull(argument: T?, name: String): T {
        if (argument == null) {
            throw IllegalArgumentException("$name may not be null")
        }
        return argument
    }

    fun <T : CharSequence> notEmpty(argument: T?, name: String): T {
        if (argument == null) {
            throw IllegalArgumentException("$name may not be null")
        }
        if (argument.isEmpty()) {
            throw IllegalArgumentException("$name may not be empty")
        }
        return argument
    }

    fun <T : CharSequence> notBlank(argument: T?, name: String): T {
        if (argument == null) {
            throw IllegalArgumentException("$name may not be null")
        }
        if (argument.isBlank()) {
            throw IllegalArgumentException("$name may not be blank")
        }
        return argument
    }

    fun <T : CharSequence> containsNoBlanks(argument: T?, name: String): T {
        if (argument == null) {
            throw IllegalArgumentException("$name may not be null")
        }

        if (argument.find { it.isWhitespace() } != null) {
            throw IllegalArgumentException("$name may not contain blanks")
        }
        return argument
    }

    fun <E, T : Collection<E>> notEmpty(argument: T?, name: String): T {
        if (argument == null) {
            throw IllegalArgumentException("$name may not be null")
        }
        if (argument.isEmpty()) {
            throw IllegalArgumentException("$name may not be empty")
        }
        return argument
    }

    fun positive(n: Int, name: String): Int {
        if (n <= 0) {
            throw IllegalArgumentException("$name may not be negative or zero")
        }
        return n
    }

    fun positive(n: Long, name: String): Long {
        if (n <= 0) {
            throw IllegalArgumentException("$name may not be negative or zero")
        }
        return n
    }

    fun notNegative(n: Int, name: String): Int {
        if (n < 0) {
            throw IllegalArgumentException("$name may not be negative")
        }
        return n
    }

    fun notNegative(n: Long, name: String): Long {
        if (n < 0) {
            throw IllegalArgumentException("$name may not be negative")
        }
        return n
    }

}
