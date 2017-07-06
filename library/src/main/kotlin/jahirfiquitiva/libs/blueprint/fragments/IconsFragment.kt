/*
 * Copyright (c) 2017. Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/Blueprint#special-thanks
 */

package jahirfiquitiva.libs.blueprint.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.adapters.IconsAdapter
import jahirfiquitiva.libs.blueprint.extensions.getBoolean
import jahirfiquitiva.libs.blueprint.extensions.getInteger
import jahirfiquitiva.libs.blueprint.extensions.sortIconsList
import jahirfiquitiva.libs.blueprint.fragments.presenters.ItemsFragmentPresenter
import jahirfiquitiva.libs.blueprint.models.Icon
import jahirfiquitiva.libs.blueprint.models.IconsCategory
import jahirfiquitiva.libs.blueprint.tasks.BasicTaskLoader
import jahirfiquitiva.libs.blueprint.tasks.IconsLoader
import jahirfiquitiva.libs.blueprint.tasks.XMLIconsLoader
import jahirfiquitiva.libs.blueprint.ui.views.EmptyViewRecyclerView
import jahirfiquitiva.libs.blueprint.utils.DEFAULT_PREVIEWS_POSITION

class IconsFragment:Fragment(), ItemsFragmentPresenter<ArrayList<IconsCategory>> {

    private lateinit var recyclerView:EmptyViewRecyclerView
    private lateinit var fastScroller:RecyclerFastScroller

    override fun onCreateView(inflater:LayoutInflater?, container:ViewGroup?,
                              savedInstanceState:Bundle?):View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val content = inflater?.inflate(R.layout.section_icons_preview, container, false) as View
        initUI(content)
        return content
    }

    override fun onViewCreated(view:View?, savedInstanceState:Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        executeTask(context)
    }

    override fun initUI(content:View) {
        recyclerView = content.findViewById(R.id.icons_grid)
        fastScroller = content.findViewById(R.id.fast_scroller)
        recyclerView.emptyView = content.findViewById(R.id.empty_view)
        recyclerView.textView = content.findViewById(R.id.empty_text)
        recyclerView.adapter = IconsAdapter {
            onItemClicked(it)
        }
        val columns = context.getInteger(R.integer.icons_grid_width)
        recyclerView.layoutManager = GridLayoutManager(context, columns,
                                                       GridLayoutManager.VERTICAL, false)
        recyclerView.updateState(EmptyViewRecyclerView.STATE_LOADING)
        fastScroller.attachRecyclerView(recyclerView)
    }

    override fun onItemClicked(item:Any) {
        if (item is Icon) {
            TODO("Not implemented yet")
        }
    }

    override fun getLoaderId():Int = DEFAULT_PREVIEWS_POSITION

    override fun buildLoader():Loader<ArrayList<IconsCategory>> {
        if (context.getBoolean(R.bool.xml_drawable_enabled)) {
            return XMLIconsLoader(context,
                                  object:BasicTaskLoader.TaskListener {
                                      override fun onTaskStarted() {
                                          recyclerView.updateState(
                                                  EmptyViewRecyclerView.STATE_LOADING)
                                      }
                                  })
        } else {
            return IconsLoader(context,
                               object:BasicTaskLoader.TaskListener {
                                   override fun onTaskStarted() {
                                       recyclerView.updateState(
                                               EmptyViewRecyclerView.STATE_LOADING)
                                   }
                               })
        }
    }

    override fun onDataLoadFinished(data:ArrayList<IconsCategory>) {
        val adapter = recyclerView.adapter
        if (adapter is IconsAdapter) {
            val icons = ArrayList<Icon>()
            data.forEach {
                icons.addAll(it.icons)
            }
            adapter.clearAndAddAll(icons.sortIconsList())
            recyclerView.updateState(EmptyViewRecyclerView.STATE_NORMAL)
        }
    }

}