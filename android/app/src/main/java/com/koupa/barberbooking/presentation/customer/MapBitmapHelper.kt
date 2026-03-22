package com.koupa.barberbooking.presentation.customer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import coil.Coil
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import org.osmdroid.util.GeoPoint

/** Distance (metres) between two [GeoPoint] objects. */
fun GeoPoint.distanceTo(other: GeoPoint): Double =
    distanceToAsDouble(GeoPoint(other.latitude, other.longitude))

/** Converts a [LatLon] domain value to an osmdroid [GeoPoint]. */
fun LatLon.toGeoPoint() = GeoPoint(latitude, longitude)

/**
 * Creates a 120×120 circular bitmap suitable for an osmdroid Marker icon.
 *
 * - Gold border (#E1A553) when unselected, Teal border (#1A7A78) when selected
 * - Centre image: Coil-loaded photo (circle-cropped) or a slate-bg fallback circle
 *
 * Must be called from a coroutine (Coil's execute is a suspend function).
 */
suspend fun createCircularMarkerBitmap(
    context  : Context,
    photoUrl : String?,
    isSelected: Boolean = false
): BitmapDrawable {
    val dp      = context.resources.displayMetrics.density
    val sizePx  = (68 * dp).toInt()
    val borderW = (if (isSelected) 4 else 3) * dp

    val bmp    = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val paint  = Paint(Paint.ANTI_ALIAS_FLAG)
    val radius = sizePx / 2f

    // Background circle (Slate #323E4B)
    paint.style = Paint.Style.FILL
    paint.color = android.graphics.Color.parseColor("#323E4B")
    canvas.drawCircle(radius, radius, radius, paint)

    // Photo (or fallback)
    if (!photoUrl.isNullOrBlank()) {
        val inner = (sizePx - borderW * 2).toInt()
        val result = Coil.imageLoader(context).execute(
            ImageRequest.Builder(context)
                .data(photoUrl)
                .size(inner, inner)
                .transformations(CircleCropTransformation())
                .allowHardware(false)          // required for canvas drawing
                .build()
        )
        val photo = (result.drawable as? BitmapDrawable)?.bitmap
        if (photo != null) {
            canvas.drawBitmap(photo, borderW, borderW, paint)
        } else {
            drawFallbackIcon(canvas, radius, paint)
        }
    } else {
        drawFallbackIcon(canvas, radius, paint)
    }

    // Gold / Teal border
    paint.style       = Paint.Style.STROKE
    paint.strokeWidth = borderW
    paint.color       = if (isSelected)
        android.graphics.Color.parseColor("#1A7A78") else
        android.graphics.Color.parseColor("#E1A553")
    canvas.drawCircle(radius, radius, radius - borderW / 2f, paint)

    return BitmapDrawable(context.resources, bmp)
}

private fun drawFallbackIcon(canvas: Canvas, radius: Float, paint: Paint) {
    // Gold inner disc as fallback ✂-like indicator
    paint.style = Paint.Style.FILL
    paint.color = android.graphics.Color.parseColor("#E1A553")
    paint.alpha = 160
    canvas.drawCircle(radius, radius, radius * 0.42f, paint)
    paint.alpha = 255
}

/**
 * Creates a 36×36 teal pulsing-user-location dot bitmap for osmdroid.
 * (A solid teal circle with a white border — static version for the marker.)
 */
fun createUserDotBitmap(context: Context): BitmapDrawable {
    val dp     = context.resources.displayMetrics.density
    val size   = (36 * dp).toInt()
    val bmp    = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val paint  = Paint(Paint.ANTI_ALIAS_FLAG)
    val r      = size / 2f

    // Teal fill
    paint.style = Paint.Style.FILL
    paint.color = android.graphics.Color.parseColor("#1A7A78")
    canvas.drawCircle(r, r, r, paint)

    // White border
    paint.style       = Paint.Style.STROKE
    paint.strokeWidth = 3 * dp
    paint.color       = android.graphics.Color.WHITE
    canvas.drawCircle(r, r, r - 2 * dp, paint)

    return BitmapDrawable(context.resources, bmp)
}
