/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yks.chestnutyun.adaper

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yks.chestnutyun.views.files.DownloadListFragment
import com.yks.chestnutyun.views.files.UploadListFragment
import com.yks.chestnutyun.views.files.allfiles.TabAllFilesFragment
import com.yks.chestnutyun.views.files.music.TabMusicFragment
import com.yks.chestnutyun.views.files.video.TabVideoFragment

/**
 * ViewPager的适配器
 */
const val UPLOAD_LIST = 0
const val DOWNLOAD_LIST = 1


class TransferListAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) { //FragmentStatePagerAdapter - 适用于对未知数量的页面进行分页。
                                                                            // FragmentStatePagerAdapter 会在用户导航至其他位置时销毁 Fragment，从而优化内存使用情况。

    /**
     * 将ViewPager页面索引映射到各自的片段
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(  //将fragments放进map中
        UPLOAD_LIST to { UploadListFragment() },
        DOWNLOAD_LIST to { DownloadListFragment() },


        ) as Map<Int, () -> Fragment>

    override fun getItemCount() = tabFragmentsCreators.size  //获取map的长度

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}
