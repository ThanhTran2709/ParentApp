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

	//Handles children's images by encoding Bitmaps into Base64 Strings
	public class ImageHandler{

		private static final int SELECT_FROM_GALLERY = 1;
		private static final int TAKE_NEW_PHOTO = 2;

		private String encodedResult;
		private int photoActivityCode; // 1 for select from gallery, 2 for taking new photo
		private ImageView output;     //pass in preview image from dialogs
		private final ActivityResultLauncher<Intent> openPhotoActivity;

		public ImageHandler() {
			//ActivityResultLaunchers can only be initialized in OnCreate, hence
			//the weird way to pass things into the ActivityResultLauncher
			openPhotoActivity = getPhoneActivity();
		}

		private void selectFromPhotos(ImageView showImage) {
			photoActivityCode = SELECT_FROM_GALLERY;
			output = showImage;
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			openPhotoActivity.launch(intent);
		}

		private void takePhoto(ImageView showImage) {
			photoActivityCode = TAKE_NEW_PHOTO;
			output = showImage;
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			openPhotoActivity.launch(intent);
		}

		//Encodes bitmap into a String
		private String encodeBitmap(Bitmap image) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
			byte[] bytes = outputStream.toByteArray();
			return Base64.encodeToString(bytes, Base64.DEFAULT);
		}

		//Decodes String into bitmap
		private Bitmap decodeBitmap(String encodedString) {
			byte[] decodedByte = Base64.decode(encodedString, 0);
			return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
		}

		private ActivityResultLauncher<Intent> getPhoneActivity(){
			return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
				result -> {
					if (result.getResultCode() == Activity.RESULT_OK) {
						Intent data = result.getData();
						switch(photoActivityCode) {
							case SELECT_FROM_GALLERY:
								Uri selectedImageUri = data.getData();
								if (selectedImageUri != null) {
									try {
										Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
										encodedResult = encodeBitmap(bitmap);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								break;
							case TAKE_NEW_PHOTO:
								Bitmap bitmap = (Bitmap) data.getExtras().get("data");
								encodedResult = encodeBitmap(bitmap);
								break;
							default:
								throw new IllegalStateException("Invalid photo activity code.");
						}

						output.setImageBitmap(decodeBitmap(encodedResult));
					}
				});
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

	//Custom ArrayListAdapter to display children's names and photos
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

			ImageView childImage = gamesView.findViewById(R.id.children_name_list_image);
			if (currentChild.getEncodedImage() != null) {
				childImage.setImageBitmap(imageHandler.decodeBitmap(currentChild.getEncodedImage()));
			}

			// Set up game ListView item
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
		private boolean hasNewImage = false;

		public void showDialog(Activity activity) {
			final Dialog dialog = new Dialog(activity);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setCancelable(false);
			dialog.setContentView(R.layout.add_child_dialog);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

			EditText nameInput = dialog.findViewById(R.id.childNameEditText);

			//Image menu to select or take new image
			String[] optionItem = getResources().getStringArray(R.array.add_image_option);
			ArrayAdapter<String> adapter = new ArrayAdapter<>(ChildrenActivity.this, R.layout.pick_image_text_view, optionItem);

			ListView pickImage = dialog.findViewById(R.id.addImage);
			pickImage.setAdapter(adapter);
			pickImage.setVisibility(View.INVISIBLE);

			ImageView addChildImage = dialog.findViewById(R.id.child_image);
			addChildImage.setOnClickListener(v -> pickImage.setVisibility(View.VISIBLE));

			pickImage.setOnItemClickListener((parent, view, position, id) -> {
				switch (position){
					case 0:
						// Select from gallery
						imageHandler.selectFromPhotos(addChildImage);
						pickImage.setVisibility(View.INVISIBLE);
						hasNewImage = true;
						break;

					case 1:
						// Take new photo
						imageHandler.takePhoto(addChildImage);
						pickImage.setVisibility(View.INVISIBLE);
						hasNewImage = true;
						break;

					case 2:
						pickImage.setVisibility(View.INVISIBLE);
						break;
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
				if (nameInput.getText().toString().isEmpty()) {
					Toast.makeText(ChildrenActivity.this, R.string.error_validate_name, Toast.LENGTH_SHORT).show();
				}
				else {
					if (hasNewImage) {
						options.addChild(new Child(nameInput.getText().toString(), imageHandler.encodedResult));
					}
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

			//Similar to add child dialog
			String[] optionItem = getResources().getStringArray(R.array.add_image_option);
			ArrayAdapter<String> adapter = new ArrayAdapter<>(ChildrenActivity.this, R.layout.pick_image_text_view, optionItem);

			ListView pickImage = dialog.findViewById(R.id.edit_image);
			pickImage.setAdapter(adapter);
			pickImage.setVisibility(View.INVISIBLE);

			Child currentChild = options.getChildList().get(index);

			ImageView editChildImage = dialog.findViewById(R.id.child_image_edit);

			if (currentChild.getEncodedImage() != null) {
				editChildImage.setImageBitmap(imageHandler.decodeBitmap(currentChild.getEncodedImage()));
			}

			editChildImage.setOnClickListener(v -> {
				pickImage.setVisibility(View.VISIBLE);
			});

			pickImage.setOnItemClickListener((parent, view, position, id) -> {
				switch (position){
					case 0:
						// Select from gallery
						imageHandler.selectFromPhotos(editChildImage);
						pickImage.setVisibility(View.INVISIBLE);
						hasNewImage = true;
						break;

					case 1:
						// Take new photo
						imageHandler.takePhoto(editChildImage);
						pickImage.setVisibility(View.INVISIBLE);
						hasNewImage = true;
						break;

					case 2:
						pickImage.setVisibility(View.INVISIBLE);
						break;
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
					if(hasNewImage) {
						options.editChildImage(index, imageHandler.encodedResult);
					}
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