import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer


@Composable
fun ShimmerEffectDetails(innerPaddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize().background(Color.White)
            .padding(innerPaddingValues)
    ) {
        // Backdrop Image Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmer()
                .background(Color(0xFFBDBDBD)) // Soft gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f) // Simulating title width
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
                .background(Color.LightGray) // Light gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Metadata (Release Date, Rating, Type)
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer()
                        .background(Color(0xFFBDBDBD)) // Soft gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Plot Overview Placeholder (Simulating multiple lines of text)
        repeat(4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer()
                    .background(Color.LightGray) // Light gray
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
