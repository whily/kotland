/**
  * Class Render2DActivity and Render2DView, based on
  *   Beginning Android Games, 2nd edition
  *
  * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
  *
  * License: 
  *   GNU General Public License v2
  *   http://www.gnu.org/licenses/gpl-2.0.html
  * Copyright (C) 2014 Yujian Zhang
  */

package net.whily.scaland

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.view.{SurfaceHolder, SurfaceView}

/** Activity class associated with 2D drawing into class/subclass of
  * `Render2DView` which uses SurfaceView.
  */
class Render2DActivity extends Activity {
  protected var renderView: Render2DView = null

  override protected def onResume() {
    super.onResume()
    renderView.resume()
  }

  override protected def onPause() {
    super.onPause()
    renderView.pause()
  }
}

/** View class which uses android.view.SurfaceView for 2D drawing. One
  * needs to implement method drawOn() for actual drawing
  * operation. Computation intensive code can be put inside drawOn()
  * because it is not running inside UI thread.
  */
abstract class Render2DView(context: Context) extends SurfaceView(context) with Runnable { 
  var renderThread: Thread = null 
  var holder: SurfaceHolder = getHolder()
  @volatile var running: Boolean = false

  /** 2D Drawing operations to be put inside this method. 
    * 
    * @param canvas the canvas for 2D drawing.
    */
  def drawOn(canvas: Canvas)

  /** The method is called when the corresponding activity is
    * resumed. If overriden, please make sure to call super.resume()
    * first.
    */
  def resume() {
    running = true
    renderThread = new Thread(this)
    renderThread.start()
  }

  /** The method is run continuous in a thread separate from UI thread. */
  final def run() {
    while (running) {
      if (holder.getSurface().isValid()) {
        val canvas = holder.lockCanvas()
        drawOn(canvas)
        holder.unlockCanvasAndPost(canvas)
      }
    }
  }

  /** The method is called when the corresponding activity is paused. If
    * overriden, please make sure to call super.pause() first.
    */
  def pause() {
    running = false
    while(true) {
      try {
        renderThread.join()
        return
      } catch {
        // Retry
        case ex: InterruptedException => None
      }
    }
  }
}
