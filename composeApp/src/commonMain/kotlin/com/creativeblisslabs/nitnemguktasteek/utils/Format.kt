fun formatTime(ms: Long): String {

    val totalSeconds = (ms / 1000).coerceAtLeast(0)

    val minutes = totalSeconds / 60

    val seconds = totalSeconds % 60

    return "$minutes:${seconds.toString().padStart(2, '0')}"
}