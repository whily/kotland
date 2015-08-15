/**
 * Utility functions.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License:
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2014-2015 Yujian Zhang
 */

package net.whily.scaland

import java.io.{PrintWriter, StringWriter}
import android.app.Activity
import android.content.{Context, SharedPreferences}
import android.preference.PreferenceManager
import android.app.Activity
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.{EditText, Toast}

object Util {
  /** Request immersive mode. */
  def requestImmersiveMode(activity: Activity) = {
    activity.getWindow().getDecorView().
      setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE)
  }

  /** Return id (like R.drawable.name).
   *
   *  @param context: it is needed to access global variable Resources.
   */
  def getDrawableId(context: Context, name: String) =
    getId(context, name, "drawable")

  /**
   *  Return id (like R.raw.name).
   *
   *  @param context: it is needed to access global variable Resources.
   */
  def getRawId(context: Context, name: String) =
    getId(context, name, "raw")

  /**
   *  Return id (like R.xml.name).
   *
   *  @param context: it is needed to access global variable Resources.
   */
  def getXmlId(context: Context, name: String) =
    getId(context, name, "xml")

  /**
   *  Return string value corresponding to R.string.name.
   *
   *  @param context: it is needed to access global variable Resources.
   */
  def getString(context: Context, name: String) =
    context.getResources.getString(getStringId(context, name))

  /** Return id (like R.string.name).
   *
   *  @param context: it is needed to access global variable Resources.
   */
  def getStringId(context: Context, name: String) =
    getId(context, name, "string")

  /** Core function used by getRawId() or getStringId().
   *
   *  @param defType indicates e.g. "raw" or "string". For details, see
   *  android.content.res.Resources.getIdentifier().
   */
  def getId(context: Context, name: String, defType: String) = {
    context.getResources.getIdentifier(name, defType,
      context.getApplicationContext.getPackageName)
  }

  def toast (context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
  }

  /**
   *  Return string value of the corresponding key; return default if not found.
   *
   *  @param context is used to get the shared preference object.
   */
  def getSharedPref(context: Context, key: String, default: String) =
    PreferenceManager.getDefaultSharedPreferences(context).getString(key, default)

  /**
   *  Set (key, value) to the shared preference object.
   *
   *  @param context is used to get the shared preference object.
   *  @param value String value
   */
  def setSharedPref(context: Context, key: String, value: String) {
    val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
    editor.putString(key, value)
    editor.commit()
  }

  /** Return the theme preference in {0, 1}. */
  def getThemePref(context: Context) = getSharedPref(context, "theme_preference", "0").toInt

  /** Return the language preference. */
  def getLanguagePref(context: Context) =
    getSharedPref(context, "language_preference", "local")

  /** Return the simplified/traditional Chinese preference. */
  def getChinesePref(context: Context) =
    getSharedPref(context, "chinese_preference", "simplified")

  /**
   * Return plural string.
   *
   * @param activity: it is needed to access global variable Resources.
   */
  def getPluralString(activity: Activity, id: Int, quantity: Int) = {
    val quantityInteger: Integer = quantity
    activity.getResources.getQuantityString(id, quantity, quantityInteger)
  }

  /** Return text size for Edit component in unit of sp. */
  def getEditTextSize(activity: Activity) = {
    getSharedPref(activity, "text_size_preference", "1") match {
      case "0" => 12
      case "1" => 15
      case "2" => 18
    }
  }

  /** Convert from sp to px. */
  def sp2px(sp: Float, context: Context) = {
    val scaledDensity = context.getResources().getDisplayMetrics().scaledDensity
    sp * scaledDensity
  }

  /** Convert from dp to px. */
  def dp2px(dp: Float, context: Context) = {
    val density = context.getResources().getDisplayMetrics().density
    dp * density
  }

  /** Return the stack trace of the exception. */
  def exceptionStack(e: Throwable): String = {
    val sw = new StringWriter()
    val pw = new PrintWriter(sw)
    e.printStackTrace(pw)
    sw.toString
  }

  /** Hide soft input window from `view` of current `context`. */
  def hideSoftInput(context: Context, view: View) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE).asInstanceOf[InputMethodManager]
    if (imm.isActive(view))
      imm.hideSoftInputFromWindow(view.getWindowToken, 0)
  }

  /** Move the cursor to the end of the `widget`. */
  def moveCursorToEnd(widget: EditText) {
    widget.setSelection(widget.getText().toString().length)
  }
}
