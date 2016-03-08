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

  def loadBitmap(resId: Int) = {
    val imageKey = resId.toString
    val bitmap = getBitmapFromMemCache(imageKey)
    if (bitmap == null) {
      val decodedBitmap = BitmapFactory.decodeResource(context.getResources(), resId)
      addBitmapToMemoryCache(resId.toString, decodedBitmap)
      decodedBitmap
    } else bitmap
  }
}
