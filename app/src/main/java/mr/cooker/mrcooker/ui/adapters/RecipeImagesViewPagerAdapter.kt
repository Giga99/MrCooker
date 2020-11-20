/*
 * Created by Igor Stevanovic on 11/20/20 12:38 AM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/20/20 12:38 AM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mr.cooker.mrcooker.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import mr.cooker.mrcooker.R

class RecipeImagesViewPagerAdapter(
    private val context: Context,
    private val itemsList: List<String>
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.recipe_image_view_pager_item, container, false)

        Glide.with(context).load(itemsList[position]).into(view.findViewById(R.id.imageViewPager))
        view.findViewById<ImageView>(R.id.imageViewPager).setColorFilter(Color.parseColor("#4D000000"))
        container.addView(view, position)

        return view
    }

    override fun getCount(): Int = itemsList.size

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) = container.removeView(obj as View)
}