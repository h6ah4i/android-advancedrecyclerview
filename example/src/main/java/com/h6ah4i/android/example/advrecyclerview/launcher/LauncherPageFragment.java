/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.h6ah4i.android.example.advrecyclerview.launcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.demo_composition_all.CompositionAllExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_d_basic.DraggableExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_d_check_can_drop.DraggableCheckCanDropExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_d_grid.DraggableGridExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_d_minimal.MinimalDraggableExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_d_on_longpress.DragOnLongPressExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_d_staggered_grid.DraggableStaggeredGridExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_d_with_section.DraggableWithSectionExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_ds.DraggableSwipeableExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_e_add_remove.AddRemoveExpandableExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_e_already_expanded.AlreadyExpandedGroupsExpandableExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_e_basic.ExpandableExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_e_minimal.MinimalExpandableExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_ed_with_section.ExpandableDraggableWithSectionExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_eds.ExpandableDraggableSwipeableExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_hf_add_remove.AddRemoveHeaderFooterExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_hf_e.ExpandableWithHeaderFooterExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_hf_minimal.MinimalHeaderFooterExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_s_basic.SwipeableExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_s_button.SwipeableWithButtonExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_s_longpress.SwipeOnLongPressExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_s_minimal.MinimalSwipeableExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_s_vertical.VerticalSwipeableExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_s_viewpager.ViewPagerSwipeableExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_wa_filtering.CustomFilteringWrapperAdapterExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_wa_insertion.CustomInsertionWrapperAdapterExampleActivity;

public class LauncherPageFragment extends Fragment {
    private static final String ARG_PAGE_NO = "page no";

    public static final int PAGE_DRAG = 0;
    public static final int PAGE_SWIPE = 1;
    public static final int PAGE_EXPAND = 2;
    public static final int PAGE_HEADER = 3;
    public static final int PAGE_ADAPTER = 4;
    public static final int PAGE_ADVANCED = 5;

    public static final int NUM_PAGES = 6;


    public static LauncherPageFragment newInstance(int pageNo) {
        LauncherPageFragment fragment = new LauncherPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NO, pageNo);
        fragment.setArguments(args);
        return fragment;
    }

    public LauncherPageFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_list_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        int pageNo = getArguments().getInt(ARG_PAGE_NO);

        LauncherButtonsAdapter adapter = createAdapter(pageNo);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    public static String getPageTitle(int pageNo) {
        switch (pageNo) {
            case PAGE_DRAG:
                return "Drag";
            case PAGE_SWIPE:
                return "Swipe";
            case PAGE_EXPAND:
                return "Expand";
            case PAGE_HEADER:
                return "Header";
            case PAGE_ADAPTER:
                return "Adapter";
            case PAGE_ADVANCED:
                return "Advanced";
            default:
                throw new IllegalArgumentException();
        }
    }

    private LauncherButtonsAdapter createAdapter(int pageNo) {
        LauncherButtonsAdapter adapter = new LauncherButtonsAdapter(this);

        switch (pageNo) {
            case PAGE_DRAG:
                // Drag
                adapter.put(MinimalDraggableExampleActivity.class, R.string.activity_title_demo_d_minimal);
                adapter.put(DraggableExampleActivity.class, R.string.activity_title_demo_d_basic);
                adapter.put(DragOnLongPressExampleActivity.class, R.string.activity_title_demo_d_on_longpress);
                adapter.put(DraggableWithSectionExampleActivity.class, R.string.activity_title_demo_d_with_section);
                adapter.put(DraggableCheckCanDropExampleActivity.class, R.string.activity_title_demo_d_check_can_drop);
                adapter.put(DraggableGridExampleActivity.class, R.string.activity_title_demo_d_grid);
                adapter.put(DraggableStaggeredGridExampleActivity.class, R.string.activity_title_demo_d_staggered_grid);
                break;
            case PAGE_SWIPE:
                // Swipe
                adapter.put(MinimalSwipeableExampleActivity.class, R.string.activity_title_demo_s_minimal);
                adapter.put(SwipeableExampleActivity.class, R.string.activity_title_demo_s_basic);
                adapter.put(SwipeOnLongPressExampleActivity.class, R.string.activity_title_demo_s_on_longpress);
                adapter.put(SwipeableWithButtonExampleActivity.class, R.string.activity_title_demo_us);
                adapter.put(VerticalSwipeableExampleActivity.class, R.string.activity_title_demo_s_vertical);
                adapter.put(ViewPagerSwipeableExampleActivity.class, R.string.activity_title_demo_s_viewpager);
                break;
            case PAGE_EXPAND:
                // Expand
                adapter.put(MinimalExpandableExampleActivity.class, R.string.activity_title_demo_e_minimal);
                adapter.put(ExpandableExampleActivity.class, R.string.activity_title_demo_e_basic);
                adapter.put(AddRemoveExpandableExampleActivity.class, R.string.activity_title_demo_e_add_remove);
                adapter.put(AlreadyExpandedGroupsExpandableExampleActivity.class, R.string.activity_title_demo_e_already_expanded);
                break;
            case PAGE_HEADER:
                // Headers/Footers
                adapter.put(MinimalHeaderFooterExampleActivity.class, R.string.activity_title_demo_hf_minimal);
                adapter.put(ExpandableWithHeaderFooterExampleActivity.class, R.string.activity_title_demo_hf_e);
                adapter.put(AddRemoveHeaderFooterExampleActivity.class, R.string.activity_title_demo_hf_add_remove);
                break;
            case PAGE_ADAPTER:
                // Adapter
                adapter.put(CompositionAllExampleActivity.class, R.string.activity_title_demo_composition_all);
                adapter.put(CustomInsertionWrapperAdapterExampleActivity.class, R.string.activity_title_demo_wa_insertion);
                adapter.put(CustomFilteringWrapperAdapterExampleActivity.class, R.string.activity_title_demo_wa_filtering);
                break;
            case PAGE_ADVANCED:
                // Advanced
                adapter.put(DraggableSwipeableExampleActivity.class, R.string.activity_title_demo_ds);
                adapter.put(ExpandableDraggableSwipeableExampleActivity.class, R.string.activity_title_demo_eds);
                adapter.put(ExpandableDraggableWithSectionExampleActivity.class, R.string.activity_title_demo_ed_with_section);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return adapter;
    }
}
