/**
 * Class BitmapCache.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License:
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2016-2017 Yujian Zhang
 */

package net.whily.scaland

import java.lang.ref.{SoftReference, WeakReference}
import scala.collection.mutable
import android.content.Context
import android.graphics.{Bitmap, BitmapFactory, Canvas, Paint, RectF}
import android.os.AsyncTask
import android.util.LruCache

/** Memory cache for bitmap. */
class BitmapCache(context: Context) {
  // Bitmap option to reduce memory usage by half.
  private val bitmapLoadingOptions = new BitmapFactory.Options()
  bitmapLoadingOptions.inScaled = false // Only decode into original size.
  bitmapLoadingOptions.inPreferredConfig = Bitmap.Config.RGB_565
  // inBitmap only works with mutable bitmaps, so force the decoder to
  // return mutable bitmaps.
  bitmapLoadingOptions.inMutable = true
  bitmapLoadingOptions.inSampleSize = 1

  // Save a soft reference to the bitmap in a HashSet, for possible
  // reuse later with inBitmap.
  // Synchronizedset is deprecated in Scala. Maybe later (TODO) this will
  // be replaced with something like
  //     new java.util.concurrent.ConcurrentHashMap[SoftReference[Bitmap], Unit]()
  private val reusableBitmaps =
    new mutable.HashSet[SoftReference[Bitmap]]() with mutable.SynchronizedSet[SoftReference[Bitmap]]

  // VM memory in kB.
  private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).asInstanceOf[Int]
  // Use 1/4 for bitmap cache.
  private val cacheSize = maxMemory / 4
  private val cache = new LruCache[String, Bitmap](cacheSize) {
    override protected def sizeOf(key: String, bitmap: Bitmap) = {
      // The cache size will be measured in kilobytes rather than
      // number of items.
      bitmap.getByteCount() / 1024
    }

    override protected def entryRemoved(evicted: Boolean, key: String,
      oldValue: Bitmap, newValue: Bitmap) {
      reusableBitmaps.add(new SoftReference[Bitmap](oldValue))
    }
  }

  private def addBitmapToMemoryCache(key: String, bitmap: Bitmap) {
    if ((key == null) || (bitmap == null)) {
      return;
    }

    if (getBitmapFromMemCache(key) == null) {
      cache.put(key, bitmap)
    }
  }

  private def getBitmapFromMemCache(key: String) = cache.get(key)

  /**
    * Return the loaded bitmap. Could be null if loading fails.
    */
  def loadBitmap(resId: Int) = {
    val imageKey = resId.toString
    val bitmap = getBitmapFromMemCache(imageKey)
    if (bitmap == null) {
      bitmapLoadingOptions.inBitmap = getBitmapFromReusableSet(bitmapLoadingOptions)
      val decodedBitmap = BitmapFactory.decodeResource(context.getResources(), resId, bitmapLoadingOptions)
      if (decodedBitmap != null) {
        addBitmapToMemoryCache(imageKey, decodedBitmap)
      }
      decodedBitmap
    } else bitmap
  }

  // This method iterates through the reusable bitmaps, looking for one
  // to use for inBitmap:
  protected def getBitmapFromReusableSet(options: BitmapFactory.Options) = {
    var bitmap: Bitmap = null

    if ((reusableBitmaps != null) && !reusableBitmaps.isEmpty) {
      reusableBitmaps.synchronized {
        reusableBitmaps.retain(x => (x.get() != null) && x.get().isMutable())
        reusableBitmaps.find((x: SoftReference[Bitmap])
          => BitmapCache.canUseForInBitmap(x.get(), options)) match {
          case Some(item) => bitmap = item.get(); reusableBitmaps.remove(item)
        }
      }
    }

    bitmap
  }
}

object BitmapCache {
  // Returns true if candidate can be used for inBitmap re-use with targetOptions.
  def canUseForInBitmap(candidate: Bitmap, targetOptions: BitmapFactory.Options) = {
    val width = targetOptions.outWidth / targetOptions.inSampleSize
    val height = targetOptions.outHeight / targetOptions.inSampleSize
    val byteCount = width * height * getBytesPerPixel(targetOptions.inPreferredConfig)

    byteCount <= candidate.getAllocationByteCount()
  }

  // Return the byte usage per pixel of a bitmap based on its configuration.
  private def getBytesPerPixel(config: Bitmap.Config) = config match {
    case Bitmap.Config.ARGB_8888 => 4
    case Bitmap.Config.RGB_565   => 2
    case Bitmap.Config.ARGB_4444 => 2
    case Bitmap.Config.ALPHA_8   => 1
    case _                       => 1
  }
}
