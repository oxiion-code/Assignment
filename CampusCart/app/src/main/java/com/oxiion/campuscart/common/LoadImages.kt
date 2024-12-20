import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

@Composable
fun LoadImageFromUrl(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    Image(
        painter = rememberAsyncImagePainter(model = imageUrl),
        contentDescription = "Image from URL",
        modifier = modifier,
        contentScale = contentScale
    )
}
