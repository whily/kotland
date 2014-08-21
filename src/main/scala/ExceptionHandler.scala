/**
 * Handle uncaught exception for each activity.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2014 Yujian Zhang
 */

package net.whily.scaland

import android.content.Context
import android.os.{Looper, Process}

class ExceptionHandler(context: Context) extends Thread.UncaughtExceptionHandler {
  override def uncaughtException(thread: Thread, ex: Throwable) {
    new Thread() {
      override def run() {
        Looper.prepare()
        Util.toast(context, Util.exceptionStack(ex))
        // The following functions are disabled since toast cannot be
        // shown if enabled.  
        // Process.killProcess(Process.myPid())
        // System.exit(10)
        Looper.loop()
      }
    }.start()

    try {
      // Sleep several seconds to all the toast to be seen. Otherwise
      // the app is closed immediately.
      Thread.sleep(4000)
    } catch {
      case e: InterruptedException => ()         // Ignored.
    }
  }
}
