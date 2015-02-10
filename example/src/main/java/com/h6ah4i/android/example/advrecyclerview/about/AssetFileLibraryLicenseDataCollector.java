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
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssetFileLibraryLicenseDataCollector {
    private static final String TAG = "AssetLibLicenseCollect";

    private Context mContext;
    private String mBaseAssetDir;

    public static class LibraryInfo {
        public String mLibraryName;
        public String mLicenseText;
        public String mNoticeText;
        public Uri mLink;
    }

    public AssetFileLibraryLicenseDataCollector(Context context, String baseAssetDir) {
        mContext = context;
        mBaseAssetDir = baseAssetDir;
    }

    public List<LibraryInfo> collect() {
        ArrayList<LibraryInfo> list = new ArrayList<>();
        AssetManager assets = mContext.getAssets();
        try {
            String[] dirs = assets.list(mBaseAssetDir);

            // sort numerically
            final Pattern p = Pattern.compile("^([0-9]+).*$");
            Arrays.sort(dirs, new Comparator<String>() {
                public int compare(String s1, String s2) {
                    try {
                        Matcher m1 = p.matcher(s1);
                        Matcher m2 = p.matcher(s2);
                        if (m1.find() && m2.find()) {
                            int n1 = Integer.parseInt(m1.group(1));
                            int n2 = Integer.parseInt(m2.group(1));

                            if (n1 == n2) {
                                return s1.compareTo(s2);
                            }

                            return n1 - n2;
                        } else {
                            return s1.compareTo(s2);
                        }
                    } catch (RuntimeException e) {
                        return s1.compareTo(s2);
                    }
                }
            });

            for (String dirname : dirs) {
                LibraryInfo info = loadLicenseInfo(assets, mBaseAssetDir + "/" + dirname);

                if (info != null) {
                    list.add(info);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "collect()", e);
        }

        return list;
    }

    private LibraryInfo loadLicenseInfo(AssetManager assets, String dirname) {
        String name = loadTextFile(assets, dirname + "/NAME");
        String license = loadTextFile(assets, dirname + "/LICENSE");
        String notice = loadTextFile(assets, dirname + "/NOTICE");
        Uri link = safeParseLinkUri(loadTextFile(assets, dirname + "/LINK"));

        if (name != null && (license != null || notice != null)) {
            LibraryInfo info = new LibraryInfo();

            info.mLibraryName = name;
            info.mLicenseText = license;
            info.mNoticeText = notice;
            info.mLink = link;

            return info;
        } else {
            Log.w(TAG, "Failed to load " + dirname);
            return null;
        }
    }

    private static Uri safeParseLinkUri(String s) {
        if (s == null) {
            return null;
        }

        final String[] lines = s.split("\n");

        if (lines.length == 0) {
            return null;
        }


        return Uri.parse(lines[0]);
    }

    // http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
    private static String loadTextFile(AssetManager assets, String path) {
        InputStream stream = null;
        try {
            stream = assets.open(path, AssetManager.ACCESS_STREAMING);
            java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
            return s.hasNext() ? s.next().trim() : "";
        } catch (IOException e) {
            // just ignore
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }
}
