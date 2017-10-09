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

package com.h6ah4i.android.example.advrecyclerview.about;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;

import java.util.List;


public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    public static class PlaceholderFragment extends ListFragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_oss_license_info, container, false);
            Context context = getActivity();

            setListAdapter(new LibraryInfoAdapter(context));

            return rootView;
        }

    }

    private static class LibraryInfoAdapter extends BaseAdapter {
        List<AssetFileLibraryLicenseDataCollector.LibraryInfo> mInfo;

        public LibraryInfoAdapter(Context context) {
            mInfo = collectLicenseInfo(context);
        }

        private static List<AssetFileLibraryLicenseDataCollector.LibraryInfo> collectLicenseInfo(Context context) {
            AssetFileLibraryLicenseDataCollector collector = new AssetFileLibraryLicenseDataCollector(context, "oss");
            return collector.collect();
        }

        @Override
        public int getCount() {
            return mInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return mInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AssetFileLibraryLicenseDataCollector.LibraryInfo info = mInfo.get(position);
            ViewHolder vh;
            View v;

            if (convertView == null) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.license_info_item, parent, false);
                vh = new ViewHolder(v);
                v.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
                v = convertView;
            }

            if (info.mLink == null) {
                vh.mName.setText(info.mLibraryName);
            } else {
                vh.mName.setMovementMethod(LinkMovementMethod.getInstance());
                vh.mName.setText(Html.fromHtml("<a href=\"" + info.mLink.toString() + "\">" + info.mLibraryName + "</a>"));
            }

            if (info.mNoticeText != null) {
                vh.mLicenseNotice.setText(info.mNoticeText);
            } else if (info.mLicenseText != null) {
                vh.mLicenseNotice.setText(info.mLicenseText);
            } else {
                vh.mLicenseNotice.setText("");
            }

            return v;
        }

        private static class ViewHolder {
            public TextView mName;
            public TextView mLicenseNotice;

            public ViewHolder(View v) {
                mName = v.findViewById(R.id.textLibraryName);
                mLicenseNotice = v.findViewById(R.id.textLicenseNotice);
            }
        }
    }
}
