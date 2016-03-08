/**
 * Class BitmapCache.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License:
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2016 Yujian Zhang
 */

package net.whily.scaland

import java.lang.ref.WeakReference
import android.content.Context
import android.graphics.{Bitmap, BitmapFactory, Canvas, Paint, RectF}
import android.os.AsyncTask
import android.util.LruCache

/** Memory cache for bitmap. */
class BitmapCache(context: Context) {
  // VM memory in kB.
  private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).asInstanceOf[Int]
  // Use 1/8 for bitmap cache.
  private val cacheSize = maxMemory / 8
  private val cache = new LruCache[String, Bitmap](cacheSize) {
    override protected def sizeOf(key: String, bitmap: Bitmap) = {
      // The cache size will be measured in kilobytes rather than
      // number of items.
      bitmap.getByteCount() / 1024
    }
  }

  private def addBitmapToMemoryCache(key: String, bitmap: Bitmap) {
    if (getBitmapFromMemCache(key) == null) {
      cache.put(key, bitmap)
    }
  }

  private def getBitmapFromMemCache(key: String) = cache.get(key)

  def loadBitmap(resId: Int, bitmaps: Array[Bitmap], index: Int, canvas: Canvas,
                 tileRect: RectF, paint: Paint) {
    val imageKey = resId.toString
    val bitmap = getBitmapFromMemCache(imageKey)
    if (bitmap != null) {
      bitmaps(index) = bitmap
    } else {
      val task = new BitmapWorkerTask(bitmaps, index, canvas, tileRect, paint)
      task.execute(resId.asInstanceOf[AnyRef])
    }
  }

  // It seems that there is some issues when using AsyncTask with Scala.
  // For example AnyRef should be used instead of actual data type.
  class BitmapWorkerTask(private val bitmaps: Array[Bitmap], private val index: Int, private val canvas: Canvas,
                 private val tileRect: RectF, private val paint: Paint)
      extends AsyncTask[AnyRef, Void, Bitmap] {
    private val canvasReference = new WeakReference[Canvas](canvas)
    private val paintReference = new WeakReference[Paint](paint)

    // Decode image in background.
    override protected def doInBackground(params: AnyRef*): AnyRef = {
      val resId = params(0).asInstanceOf[Int]
      val decodedBitmap = BitmapFactory.decodeResource(context.getResources(), resId)
      addBitmapToMemoryCache(resId.toString, decodedBitmap)
      decodedBitmap
    }

    override protected def onPostExecute(bitmap: Bitmap) {
      if ((canvasReference != null) && (paintReference != null) && (bitmapRef != null)) {
        val canvasNow = canvasReference.get()
        val paintNow = paintReference.get()
        if ((canvasNow != null) && (paintNow != null)) {
          bitmaps(index) = bitmap
          canvas.drawBitmap(bitmaps(index), null, tileRect, paint)
        }
      }
    }
  }
}
