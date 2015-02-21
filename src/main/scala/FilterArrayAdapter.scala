/**
 * Class AccentFoldingArrayAdapter.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License:
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2013 Yujian Zhang
 */

package net.whily.scaland

import java.text.Normalizer
import android.content.Context
import android.widget.{ArrayAdapter, Filterable, Filter}

/**
 * Filter the string.
 */
abstract class FilterArrayAdapter(context: Context, textViewResourceId: Int, objects: Array[String])
  extends ArrayAdapter[String](context, textViewResourceId, objects) with Filterable {

  /** Normalize string. */
  def normalize(s: String): String

  /* Keep the original data and corresponding accent-folded version. */
  private val foldedPair =
    for (s <- objects) yield (s, normalize(s))

  /* The actual data displayed. */
  private var data: Array[String] = objects

  private var filter: Filter = null

  override def getCount(): Int = data.length
  override def getItem(index: Int): String = data(index)

  override def getFilter(): Filter = {
    if (filter == null) filter = new CustomFilter()
    filter
  }

  private class CustomFilter extends Filter {
    override protected def performFiltering(constraint: CharSequence): Filter.FilterResults = {
      var filterResults = new Filter.FilterResults
      if (constraint == null || constraint.length == 0) {
        filterResults.values = objects
        filterResults.count = objects.length
      } else {
        val query = normalize(constraint.toString)
        val filteredData = for (item <- foldedPair if item._2.contains(query))
          yield item._1
        filterResults.values = filteredData
        filterResults.count = filteredData.length
      }
      filterResults
    }

    override protected def publishResults(constraint: CharSequence, results: Filter.FilterResults) {
      data = results.values.asInstanceOf[Array[String]]
      if (results != null && results.count > 0) {
        notifyDataSetChanged()
      } else {
        notifyDataSetInvalidated()
      }
    }
  }
}
