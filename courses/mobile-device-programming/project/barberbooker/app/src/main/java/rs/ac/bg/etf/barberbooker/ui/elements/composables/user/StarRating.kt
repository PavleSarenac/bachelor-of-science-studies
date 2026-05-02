package rs.ac.bg.etf.barberbooker.ui.elements.composables.user

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StarRating(
    rating: Float,
    size: Dp = 0.dp
) {
    val fullStars = rating.toInt()
    val hasHalfStar = (rating - fullStars) >= 0.5
    val outlineStars = 5 - fullStars - if (hasHalfStar) 1 else 0

    val modifier = if (size == 0.dp) {
        Modifier
    } else {
        Modifier.size(size)
    }

    repeat(fullStars) {
        Icon(
            imageVector = Icons.Filled.StarRate,
            contentDescription = "Full Star",
            tint = Color.Yellow,
            modifier = modifier
        )
    }
    if (hasHalfStar) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.StarHalf,
            contentDescription = "Half Star",
            tint = Color.Yellow,
            modifier = modifier
        )
    }
    repeat(outlineStars) {
        Icon(
            imageVector = Icons.Filled.StarOutline,
            contentDescription = "Outline Star",
            tint = Color.Yellow,
            modifier = modifier
        )
    }
}