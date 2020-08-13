package info.androidhive.firebaseauthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.firebaseauthapp.adapter.ClassAdapter;
import info.androidhive.firebaseauthapp.adapter.ClassEntityAdapter;
import info.androidhive.firebaseauthapp.classModels.ClassdataEntity;
import info.androidhive.firebaseauthapp.classModels.FitClass;

public class FitnessClassActivity extends AppCompatActivity {
    private Context context;
    private RecyclerView recyclerview_classes;
    private DatabaseReference myRef;
    private List<ClassdataEntity> classdataEntities = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_class);

        context = getApplicationContext();
        recyclerview_classes = findViewById(R.id.recyclerview_classes);
        myRef = FirebaseDatabase.getInstance().getReference();
        Bundle extras = getIntent().getExtras();
        String className = extras.getString("className");
        Query query = FirebaseDatabase.getInstance().getReference("fitness")
                .orderByChild("className")
                .equalTo(className);

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot classSnapShot:dataSnapshot.getChildren()){
                    FitClass fitClass = classSnapShot.getValue(FitClass.class);
                    Log.e("moves",fitClass.getClassData().toString());
                    Log.e("moves", fitClass.getClassData().get(0).getMoveName());
                    Log.e("moveType",fitClass.getClassData().getClass().toString());

                    classdataEntities = fitClass.getClassData();
                    ClassEntityAdapter adapter = new ClassEntityAdapter(context,classdataEntities);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    recyclerview_classes.setLayoutManager(layoutManager);
                    recyclerview_classes.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }
}