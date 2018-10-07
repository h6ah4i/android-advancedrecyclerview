/*
 *    Copyright (C) 2016 Haruki Hasegawa
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

package com.h6ah4i.android.widget.advrecyclerview.utils;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.adapter.AdapterPath;
import com.h6ah4i.android.widget.advrecyclerview.adapter.AdapterPathSegment;
import com.h6ah4i.android.widget.advrecyclerview.adapter.SimpleWrapperAdapter;
import com.h6ah4i.android.widget.advrecyclerview.adapter.UnwrapPositionResult;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Config(manifest=Config.NONE, sdk = Build.VERSION_CODES.P)
@RunWith(AndroidJUnit4.class)
public class DebugWrapperAdapterTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void unwrapPosition() throws Exception {
        RecyclerView.Adapter adapter = new GoodWrapperAdapter(new TestAdapter());
        DebugWrapperAdapter debugAdapter = new DebugWrapperAdapter(adapter);

        for (int i = 0; i < adapter.getItemCount(); i++) {
            UnwrapPositionResult result = new UnwrapPositionResult();
            debugAdapter.unwrapPosition(result, i);

            assertThat(result.isValid(), is(true));
            assertThat(result.adapter, is(adapter));
            assertThat(result.position, is(i));
        }
    }

    @Test
    public void wrapPosition() throws Exception {
        RecyclerView.Adapter adapter = new GoodWrapperAdapter(new TestAdapter());
        DebugWrapperAdapter debugAdapter = new DebugWrapperAdapter(adapter);

        for (int i = 0; i < adapter.getItemCount(); i++) {
            UnwrapPositionResult result = new UnwrapPositionResult();
            debugAdapter.unwrapPosition(result, i);

            AdapterPath path = new AdapterPath().append(result);

            int wrappedPosition = debugAdapter.wrapPosition(path.lastSegment(), result.position);

            assertThat(wrappedPosition, is(i));
        }
    }

    @Test
    public void unwrapPosition_withBadAdapter() throws Exception {
        RecyclerView.Adapter adapter = new BadUnwrapPositionAdapter(new TestAdapter());
        DebugWrapperAdapter debugAdapter = new DebugWrapperAdapter(adapter);

        UnwrapPositionResult result = new UnwrapPositionResult();

        thrown.expect(IllegalStateException.class);

        debugAdapter.unwrapPosition(result, 0);
    }

    @Test
    public void wrapPosition_withBadAdapter() throws Exception {
        RecyclerView.Adapter adapter = new BadWrapPositionAdapter(new TestAdapter());
        DebugWrapperAdapter debugAdapter = new DebugWrapperAdapter(adapter);

        debugAdapter.setSettingFlags(0);

        UnwrapPositionResult result = new UnwrapPositionResult();
        debugAdapter.unwrapPosition(result, 0);

        AdapterPath path = new AdapterPath().append(result);

        debugAdapter.setSettingFlags(DebugWrapperAdapter.FLAG_VERIFY_WRAP_POSITION);

        thrown.expect(IllegalStateException.class);

        debugAdapter.wrapPosition(path.lastSegment(), result.position);
    }

    @Test
    public void setSettingFlags() throws Exception {
        RecyclerView.Adapter adapter = new TestAdapter();
        DebugWrapperAdapter debugAdapter = new DebugWrapperAdapter(adapter);

        debugAdapter.setSettingFlags(0);
        debugAdapter.setSettingFlags(DebugWrapperAdapter.FLAG_VERIFY_WRAP_POSITION);
        debugAdapter.setSettingFlags(DebugWrapperAdapter.FLAG_VERIFY_UNWRAP_POSITION);
        debugAdapter.setSettingFlags(DebugWrapperAdapter.FLAGS_ALL_DEBUG_FEATURES);
    }

    @Test
    public void getSettingFlags() throws Exception {
        RecyclerView.Adapter adapter = new TestAdapter();
        DebugWrapperAdapter debugAdapter = new DebugWrapperAdapter(adapter);

        assertThat(debugAdapter.getSettingFlags(), is(DebugWrapperAdapter.FLAGS_ALL_DEBUG_FEATURES));

        debugAdapter.setSettingFlags(0);
        assertThat(debugAdapter.getSettingFlags(), is(0));

        debugAdapter.setSettingFlags(DebugWrapperAdapter.FLAG_VERIFY_WRAP_POSITION);
        assertThat(debugAdapter.getSettingFlags(), is(DebugWrapperAdapter.FLAG_VERIFY_WRAP_POSITION));
    }

    private static class TestViewHolder extends RecyclerView.ViewHolder {
        public TestViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class TestAdapter extends RecyclerView.Adapter<TestViewHolder> {
        public TestAdapter() {
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        @NonNull
        @Override
        public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            throw new IllegalStateException("not implemented");
        }

        @Override
        public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
            throw new IllegalStateException("not implemented");
        }
    }

    private static class GoodWrapperAdapter extends SimpleWrapperAdapter<TestViewHolder> {
        public GoodWrapperAdapter(@NonNull RecyclerView.Adapter<TestViewHolder> adapter) {
            super(adapter);
        }
    }

    private static class BadWrapPositionAdapter extends SimpleWrapperAdapter<TestViewHolder> {
        public BadWrapPositionAdapter(@NonNull RecyclerView.Adapter<TestViewHolder> adapter) {
            super(adapter);
        }

        @Override
        public int wrapPosition(@NonNull AdapterPathSegment pathSegment, int position) {
            return 123;
        }
    }

    private static class BadUnwrapPositionAdapter extends SimpleWrapperAdapter<TestViewHolder> {
        public BadUnwrapPositionAdapter(@NonNull RecyclerView.Adapter<TestViewHolder> adapter) {
            super(adapter);
        }

        @Override
        public void unwrapPosition(@NonNull UnwrapPositionResult dest, int position) {
            dest.adapter = getWrappedAdapter();
            dest.position = 123;
        }
    }
}