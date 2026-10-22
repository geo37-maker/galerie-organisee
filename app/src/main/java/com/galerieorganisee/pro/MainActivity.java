package com.galerieorganisee.pro;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.galerieorganisee.pro.adapters.FolderAdapter;
import com.galerieorganisee.pro.model.FolderItem;
import com.galerieorganisee.pro.storage.FolderRepository;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FolderRepository repository;
    private List<FolderItem> folderList;
    private FolderAdapter adapter;
    private Spinner spinnerLevels;
    private RecyclerView recyclerFolders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new FolderRepository(this);

        spinnerLevels = findViewById(R.id.spinnerLevels);
        recyclerFolders = findViewById(R.id.recyclerFolders);
        Button btnAddFolder = findViewById(R.id.btnAddFolder);

        folderList = repository.getFolders();
        setupLevelSpinner();

        adapter = new FolderAdapter(folderList, folder -> {
            Toast.makeText(this, "Dossier sélectionné : " + folder.getName(), Toast.LENGTH_SHORT).show();
        });

        recyclerFolders.setLayoutManager(new LinearLayoutManager(this));
        recyclerFolders.setAdapter(adapter);

        btnAddFolder.setOnClickListener(v -> showAddFolderDialog());
    }

    private void setupLevelSpinner() {
        Integer[] levels = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, levels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevels.setAdapter(spinnerAdapter);

        int currentMax = repository.getMaxLevels();
        spinnerLevels.setSelection(currentMax - 1);

        spinnerLevels.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                int selectedLevel = levels[position];
                int deepest = 1;
                for (FolderItem f : folderList) {
                    if (f.getLevel() > deepest) deepest = f.getLevel();
                }
                if (selectedLevel < deepest) {
                    Toast.makeText(MainActivity.this, R.string.error_min_level, Toast.LENGTH_SHORT).show();
                    spinnerLevels.setSelection(deepest - 1);
                } else {
                    repository.setMaxLevels(selectedLevel);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void showAddFolderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_folder);

        final EditText input = new EditText(this);
        input.setHint(R.string.folder_name_hint);
        builder.setView(input);

        builder.setPositiveButton(R.string.create, (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                FolderItem newFolder = new FolderItem(name, 1, null);
                folderList.add(newFolder);
                repository.saveFolders(folderList);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
