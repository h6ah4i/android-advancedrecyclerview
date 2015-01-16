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

package com.h6ah4i.android.example.advrecyclerview.demo.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.demo.MainActivity;

public class ItemPinnedMessageDialogFragment extends DialogFragment {
    private static final String KEY_ITEM_POSITION = "position";

    public static ItemPinnedMessageDialogFragment newInstance(int position) {
        final ItemPinnedMessageDialogFragment frag = new ItemPinnedMessageDialogFragment();
        final Bundle args = new Bundle();

        args.putInt(KEY_ITEM_POSITION, position);

        frag.setArguments(args);
        return frag;
    }

    public ItemPinnedMessageDialogFragment() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final int itemPosition = getArguments().getInt(KEY_ITEM_POSITION);

        builder.setMessage(getString(R.string.dialog_message_item_pinned, itemPosition));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notifyItemPinnedDialogDismissed(true);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(true);
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        notifyItemPinnedDialogDismissed(false);
    }

    private void notifyItemPinnedDialogDismissed(boolean ok) {
        final int position = getArguments().getInt(KEY_ITEM_POSITION);
        ((MainActivity) getActivity()).onNotifyItemPinnedDialogDismissed(position, ok);
    }

}
