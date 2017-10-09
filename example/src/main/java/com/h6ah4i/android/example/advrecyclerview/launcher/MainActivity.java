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
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.tablayouthelper.TabLayoutHelper;

public class MainActivity extends AppCompatActivity {
    private static final String FRAGMENT_TAG_OPTIONS_MENU = "options menu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager pager = findViewById(R.id.viewpager);

        pager.setAdapter(new LauncherPagerAdapter(getSupportFragmentManager()));

        TabLayoutHelper tabLayoutHelper = new TabLayoutHelper(tabLayout, pager);
        tabLayoutHelper.setAutoAdjustTabModeEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(new OptionsMenuFragment(), FRAGMENT_TAG_OPTIONS_MENU)
                    .commit();
        }
    }
}
