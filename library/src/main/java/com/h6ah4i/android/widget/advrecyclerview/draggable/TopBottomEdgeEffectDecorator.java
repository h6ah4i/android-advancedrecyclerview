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

package com.h6ah4i.android.widget.advrecyclerview.draggable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class TopBottomEdgeEffectDecorator extends BaseEdgeEffectDecorator {
    public TopBottomEdgeEffectDecorator(@NonNull RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    protected int getEdgeDirection(int no) {
        switch (no) {
            case 0:
                return EDGE_TOP;
            case 1:
                return EDGE_BOTTOM;
            default:
                throw new IllegalArgumentException();
        }
    }
}
