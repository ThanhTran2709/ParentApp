package com.cmpt276.parentapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.model.Child;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * Activity for adding, editing, and deleting saved children
 * */
public class ChildrenActivity extends AppCompatActivity {

    private Options options;
    private ImageHandler imageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);
        options = Options.getInstance(this);
        imageHandler = new ImageHandler();

        setUpAddBtn();
        populateList();
        setUpBackBtn();
        listItemClick();
    }

    public class ImageHandler{
        private String imageResult;
        private int photoActivityCode; // 1 for select from gallery, 2 for taking new photo

        private ActivityResultLauncher<Intent> openPhotoActivity;

        public ImageHandler() {
            openPhotoActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (photoActivityCode == 1){
                            Intent data = result.getData();
                            Uri selectedImageUri = data.getData();
                            if (selectedImageUri != null) {
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                                    imageResult = encodeBitmap(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (photoActivityCode == 2){
                            Intent data = result.getData();
                            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                            imageResult = encodeBitmap(bitmap);
                        }
                    }
                });
        }

        private void selectFromPhotos() {
            photoActivityCode = 1;
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            openPhotoActivity.launch(intent);
        }

        private void takePhoto() {
            photoActivityCode = 2;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            openPhotoActivity.launch(intent);
        }

        public String encodeBitmap(Bitmap image) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] bytes = outputStream.toByteArray();
            String imageEncoded = Base64.encodeToString(bytes, Base64.DEFAULT);
            return imageEncoded;
        }

        public Bitmap decodeBitmap(String encodedString) {
            byte[] decodedByte = Base64.decode(encodedString, 0);
            return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        }
    }

    public static Intent getIntent(Context context){
        return new Intent(context, ChildrenActivity.class);
    }

    private void setUpBackBtn() {
        Button backBtn = findViewById(R.id.backBtn_children);
        backBtn.setText(R.string.backTxt);
        backBtn.setOnClickListener((view) -> finish());
    }

    private void setUpAddBtn() {
        Button addBtn = findViewById(R.id.addBtn);
        addBtn.setText(R.string.addBtnText);

        addBtn.setOnClickListener((view) -> {
            AddChildDialog alert = new AddChildDialog();
            alert.showDialog(ChildrenActivity.this);
        });
    }

    //Populate list view with name and age of children
    private void populateList(){
        ArrayAdapter<Child> adapter = new ChildrenListViewAdapter();
        ListView childrenListView = findViewById(R.id.childrenListView);
        childrenListView.setAdapter(adapter);
        childrenListView.setDivider(null);
        childrenListView.setDividerHeight(16);
    }

    private class ChildrenListViewAdapter extends ArrayAdapter<Child>{

        public ChildrenListViewAdapter(){
            super(ChildrenActivity.this, R.layout.children_view, options.getChildList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View gamesView = convertView;
            if (gamesView == null){
                gamesView = getLayoutInflater().inflate(R.layout.children_view, parent, false);
            }

            Child currentChild = options.getChildList().get(position);

            //todo setup child image
            ImageView childImage = gamesView.findViewById(R.id.children_name_list_image);
            if (currentChild.getEncodedImage() != null) {
                childImage.setImageBitmap(imageHandler.decodeBitmap(currentChild.getEncodedImage()));
            }


            // set up game ListView item
            TextView childName = gamesView.findViewById(R.id.child_name);
            childName.setText(currentChild.getName());
            return gamesView;
        }

    }


    //Click handling for children list view
    private void listItemClick(){
        if (options.getChildList().size() == 0) {
            return;
        }
        ListView childrenListView = findViewById(R.id.childrenListView);
        childrenListView.setOnItemClickListener((adapterView, childClicked, index, position) -> {
            EditChildDialog alert = new EditChildDialog();
            alert.showDialog(ChildrenActivity.this, index);
        });
    }

    public class AddChildDialog {
        boolean hasNewImage = false;

        public void showDialog(Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.add_child_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            EditText nameInput = dialog.findViewById(R.id.childNameedittext);

            //basically i just made a listview that will appear when user click the image
            //i was trying to make the listview disappear when user click outside of the listview
            //but i can't figure out how to do that so i just created a cancel button
            String[] optionItem = getResources().getStringArray(R.array.add_image_option);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChildrenActivity.this, R.layout.pick_image_text_view, optionItem);

            ListView pickImage = dialog.findViewById(R.id.addImage);
            pickImage.setAdapter(adapter);
            pickImage.setVisibility(View.INVISIBLE);

            ImageView childImage = dialog.findViewById(R.id.child_image);
            childImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickImage.setVisibility(View.VISIBLE);
                }
            });

            pickImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            // TODO code to pick image
                            imageHandler.selectFromPhotos();
                            pickImage.setVisibility(View.INVISIBLE);
                            hasNewImage = true;
                            break;

                        case 1:
                            // TODO code to take photo
                            imageHandler.takePhoto();
                            pickImage.setVisibility(View.INVISIBLE);
                            hasNewImage = true;
                            break;

                        case 2:
                            pickImage.setVisibility(View.INVISIBLE);
                            break;
                    }
                }
            });


            FloatingActionButton cancelFab = dialog.findViewById(R.id.cancelfab);
            cancelFab.setOnClickListener(getCancelFabListener(dialog));

            FloatingActionButton addFab = dialog.findViewById(R.id.addfab);
            addFab.setOnClickListener(getAddFabListener(dialog, nameInput));

            dialog.show();
        }

        private View.OnClickListener getAddFabListener(Dialog dialog, EditText nameInput) {
            return (view) -> {
                if(nameInput.getText().toString().isEmpty()) {
                    Toast.makeText(ChildrenActivity.this, R.string.error_validate_name, Toast.LENGTH_SHORT).show();
                }
                else {
                    if(hasNewImage)
                        options.addChild(new Child(nameInput.getText().toString(), imageHandler.imageResult));
                    else
                        options.addChild(new Child(nameInput.getText().toString()));

                    Options.saveChildListInPrefs(ChildrenActivity.this, options.getChildList());
                    Options.saveStringListInPrefs(ChildrenActivity.this, options.getChildListToString());
                    populateList();
                    listItemClick();

                    dialog.cancel();
                }
            };
        }

        private View.OnClickListener getCancelFabListener(Dialog dialog) {
            return (view) -> dialog.dismiss();
        }
    }

    public class EditChildDialog {
        boolean hasNewImage = false;

        public void showDialog(Activity activity, int index) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.edit_child_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            EditText nameInput = dialog.findViewById(R.id.childNameEditText2);
            nameInput.setText(options.getChildList().get(index).getName());

            //similar to add child
            String[] optionItem = getResources().getStringArray(R.array.add_image_option);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChildrenActivity.this, R.layout.pick_image_text_view, optionItem);

            ListView pickImage = dialog.findViewById(R.id.edit_image);
            pickImage.setAdapter(adapter);
            pickImage.setVisibility(View.INVISIBLE);

            Child currentChild = options.getChildList().get(index);

            ImageView editChildImage = dialog.findViewById(R.id.child_image_edit);

            if (currentChild.getEncodedImage() != null) {
                editChildImage.setImageBitmap(imageHandler.decodeBitmap(currentChild.getEncodedImage()));
            }

            editChildImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickImage.setVisibility(View.VISIBLE);
                }
            });

            pickImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            // TODO code to pick image
                            imageHandler.selectFromPhotos();
                            pickImage.setVisibility(View.INVISIBLE);
                            hasNewImage = true;
                            //editChildImage.setImageBitmap(imageHandler.decodeBitmap(imageHandler.getImageResult()));
                            break;

                        case 1:
                            // TODO code to take photo
                            imageHandler.takePhoto();
                            pickImage.setVisibility(View.INVISIBLE);
                            hasNewImage = true;
                            break;

                        case 2:
                            pickImage.setVisibility(View.INVISIBLE);
                            break;
                    }
                }
            });


            FloatingActionButton cancelFab = dialog.findViewById(R.id.cancelfab2);
            cancelFab.setOnClickListener(getCancelFabListener(dialog));

            FloatingActionButton addFab = dialog.findViewById(R.id.addfab2);
            addFab.setOnClickListener(getAddFabListener(dialog, nameInput, index));

            FloatingActionButton deleteFab = dialog.findViewById(R.id.deletefab2);
            deleteFab.setOnClickListener(getDeleteFabListener(dialog, index));

            dialog.show();
        }

        private View.OnClickListener getCancelFabListener(Dialog dialog) {
            return (view) -> dialog.dismiss();
        }

        private View.OnClickListener getAddFabListener(Dialog dialog, EditText nameInput, int index) {
            return (view) -> {
                if (nameInput.getText().toString().isEmpty()) {
                    Toast.makeText(ChildrenActivity.this, R.string.error_validate_name, Toast.LENGTH_SHORT).show();
                } else {
                    if(hasNewImage)
                        options.editChildImage(index, imageHandler.imageResult);
                    options.editChild(index, nameInput.getText().toString());
                    Options.saveChildListInPrefs(ChildrenActivity.this, options.getChildList());
                    Options.saveStringListInPrefs(ChildrenActivity.this, options.getChildListToString());
                    populateList();
                    listItemClick();
                    dialog.cancel();
                }
            };
        }

        private View.OnClickListener getDeleteFabListener(Dialog dialog, int index) {
            return (view) -> {
                options.removeChild(index);
                Options.saveChildListInPrefs(ChildrenActivity.this, options.getChildList());
                Options.saveStringListInPrefs(ChildrenActivity.this, options.getChildListToString());
                populateList();
                listItemClick();
                dialog.cancel();
            };
        }
    }

}