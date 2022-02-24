package com.hlag.routine;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Callable;

import kotlin.Function;

public class DirDialog extends AppCompatDialogFragment {

    File baseDir = new File("/storage/emulated/0");
    File thisDir;
    File oldDir = baseDir;

    Button select, cancel;
    ArrayAdapter<File> dirAdapt;
    Callable onSelect;

    DirDialog(Callable onSelect){
        this.onSelect = onSelect;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //dialog stuff
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.dir_select_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);


        //adapter to put style in list elements
        ArrayList<File> files = new ArrayList<>();
        dirAdapt = new ArrayAdapter<File>(Objects.requireNonNull(getContext()), R.layout.row_layout_dir, files) {

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater inflator = LayoutInflater.from(getContext());
                View view = inflator.inflate(R.layout.row_layout_dir, parent, false);
                File file = getItem(position);
                if (file != null) {
                    ((TextView)view.findViewById(R.id.dir_row_text)).setText(file.getName());
                }
                return view;
            }
        };

        //setup listview
        ListView lsdView = view.findViewById(R.id.dir_listview);
        lsdView.setAdapter(dirAdapt);
        lsdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            final TextView textView = view.findViewById(R.id.dir_textview);
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File file = (File) adapterView.getItemAtPosition(i);
                textView.setText(file.getName());
                dirAdapt.clear();
                try {
                    dirAdapt.addAll(getSubDirs(file.getAbsolutePath()));
                } catch (NullPointerException e) {
                    Toast.makeText(getContext(), "Access denied", Toast.LENGTH_LONG).show();
                }
            }
        });

        Handler handler = new Handler();
        Runnable r = () -> dirAdapt.addAll(getSubDirs(baseDir.getAbsolutePath()));
        handler.postDelayed(r, 200);


        //go to parent dir btn
        Button back = view.findViewById(R.id.dir_back);
        back.setOnClickListener(view1 -> {
            //gets the dir that's viewed
            thisDir = getThisDir();

            if (thisDir.equals(baseDir)) {
                Toast.makeText(getContext(), "You cannot go further back", Toast.LENGTH_LONG).show();
                return;
            }
            dirAdapt.clear();
            dirAdapt.addAll(getSubDirs(thisDir.getParent()));
        });

        //ok
        select = view.findViewById(R.id.dir_select);
        select.setOnClickListener(view12 -> {
            String finalDir = getThisDir().getAbsolutePath();
            FileManager.Companion.setDir(getThisDir().getAbsolutePath());
            MyApp.Companion.getSp(getContext()).edit().putString("prjDir", finalDir).apply();
            try {
                onSelect.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            dismiss();
        });

        //cancel
        cancel = view.findViewById(R.id.dir_cancel);
        cancel.setOnClickListener(view13 -> {
            if(isCancelable()){
                dismiss();
            }else{
            }
        });

        //dialog stuff
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null)
        {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public File[] getSubDirs(String base) {
        oldDir = new File(base);
        return new File(base).listFiles(File::isDirectory);
    }

    public File getThisDir(){
        File dir;
        try { dir = new File(dirAdapt.getItem(0).getParent()); } catch (Exception e) {
            dir = oldDir;
        }
        return dir;
    }
}
