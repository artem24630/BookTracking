package ru.sda.booktracking;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment implements TextToSpeech.OnInitListener {
    private SqliteDB db;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private MyAdapter adapter;
    //переменная для проверки возможности
    //распознавания голоса в телефоне
    private static final int VR_REQUEST=999;

    private int MY_DATA_CHECK_CODE=0;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new SqliteDB(getActivity());
        recyclerView = view.findViewById(R.id.recyclerView);
        fab = view.findViewById(R.id.fab);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new MyAdapter(db.getAllBooks());

        recyclerView.setAdapter(adapter);


        PackageManager packManager= getActivity().getPackageManager();
        List<ResolveInfo> intActivities= packManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);
        if(intActivities.size()!=0){

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listerToSpeech();
                }
            });
        }
        else
        {

            fab.setEnabled(false);
            Toast.makeText(getActivity(),"Oops - Speech recognition not supported!", Toast.LENGTH_LONG).show();
        }




    }

    private void listerToSpeech() {


        Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //указываем пакет
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getClass().getPackage().getName());
        //В процессе распознования выводим сообщение
        listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say a word!");
        //устанавливаем модель речи
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //указываем число результатов, которые могут быть получены
        listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,15);

        //начинаем прослушивание
        startActivityForResult(listenIntent, VR_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == VR_REQUEST && resultCode == -1){
            String name = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            db.addBook(name);
            adapter.changeData(db.getAllBooks());
            recyclerView.setAdapter(adapter);
            Log.d("OLOL"," ok");


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onInit(int i) {

    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.Holder>{
        ArrayList<String> books;

        void changeData(ArrayList<String> books){
            this.books = books;
        }

        private static class Holder extends RecyclerView.ViewHolder{
            TextView textView;

            Holder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.title);

            }


        }

        MyAdapter(ArrayList<String> books) {
            this.books = books;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent,false);
           return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.textView.setText(books.get(position));
        }



        @Override
        public int getItemCount() {
            return books.size();
        }
    }



}
