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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.demo_d.DraggableExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_ds.DraggableSwipeableExampleActivity;
import com.h6ah4i.android.example.advrecyclerview.demo_eds.ExpandableDraggableSwipeableExampleActivity;

public class ExampleLauncherContentsFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_launcher_contents, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_launch_demo_d).setOnClickListener(this);
        view.findViewById(R.id.button_launch_demo_ds).setOnClickListener(this);
        view.findViewById(R.id.button_launch_demo_eds).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_launch_demo_d:
                launchExampleActivity(DraggableExampleActivity.class);
                break;
            case R.id.button_launch_demo_ds:
                launchExampleActivity(DraggableSwipeableExampleActivity.class);
                break;
            case R.id.button_launch_demo_eds:
                launchExampleActivity(ExpandableDraggableSwipeableExampleActivity.class);
                break;
        }
    }

    private void launchExampleActivity(Class activityClass) {
        Intent intent = new Intent(getActivity(), activityClass);
        startActivity(intent);
    }
}
