package com.ejsfbu.app_main.DialogFragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.ejsfbu.app_main.BitmapScaler;
import com.ejsfbu.app_main.Models.Goal;
import com.ejsfbu.app_main.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.ejsfbu.app_main.Activities.AddGoalActivity.getPhotoFileUri;
import static com.ejsfbu.app_main.Activities.AddGoalActivity.getRealPathFromURI;
import static com.ejsfbu.app_main.Activities.AddGoalActivity.rotateBitmapOrientation;


public class EditGoalImageDialogFragment extends DialogFragment {

    private Context context;
    private Button bCancel;
    private Button bConfirm;
    private ImageView ivPrevGoalImage;
    private ImageButton ibGalleryPhoto;
    private ImageButton ibCameraPhoto;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    private static Goal currentGoal;

    // Request codes
    private final static int PICK_PHOTO_CODE = 1046;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;


    public EditGoalImageDialogFragment() {

    }

    public static EditGoalImageDialogFragment newInstance(String title, Goal goal) {
        EditGoalImageDialogFragment frag = new EditGoalImageDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        currentGoal = goal;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        return inflater.inflate(R.layout.fragment_edit_goal_image, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bCancel = view.findViewById(R.id.bEditGoalImageCancel);
        bConfirm = view.findViewById(R.id.bEditGoalImageConfirm);
        ivPrevGoalImage = view.findViewById(R.id.ivPrevGoalImage);
        ibGalleryPhoto = view.findViewById(R.id.ibEditGoalImagePhotos);
        ibCameraPhoto = view.findViewById(R.id.ibEditGoalImageCamera);

        setGoalImage();
        setListeners();
    }

    public void setGoalImage() {
        ParseFile image = currentGoal.getParseFile("image");
        if (image != null) {
            String imageUrl = image.getUrl();
            imageUrl = imageUrl.substring(4);
            imageUrl = "https" + imageUrl;
            RequestOptions options = new RequestOptions();
            Glide.with(context)
                    .load(imageUrl)
                    .apply(options.placeholder(R.drawable.icon_user)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .error(R.drawable.icon_user)
                            .transform(new CenterCrop())
                            .transform(new CircleCrop()))
                    .into(ivPrevGoalImage);
        }
    }

    public void setListeners() {
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseFile parseFile;
                if (photoFile == null || ivPrevGoalImage.getDrawable() == null) {
                    Toast.makeText(context, "Please upload or take a photo",
                            Toast.LENGTH_LONG).show();
                    return;
                } else {
                    parseFile = new ParseFile(photoFile);
                }

                currentGoal.setImage(parseFile);
                currentGoal.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            sendBackResult();
                        } else {
                            Toast.makeText(context, "Failed to Update Photo",
                                    Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

        ibGalleryPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPerms();
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    // Bring up gallery to select a photo
                    startActivityForResult(intent, PICK_PHOTO_CODE);
                }
            }
        });

        ibCameraPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoFile = getPhotoFileUri(photoFileName, context);

                Uri fileProvider = FileProvider.getUriForFile(context,
                        "com.codepath.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    // Start the image capture intent to take photo
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        });
    }

    public void sendBackResult() {
        ArrayList<Fragment> fragments = (ArrayList<Fragment>) getFragmentManager().getFragments();
        String fragmentTag = fragments.get(1).getTag();
        int fragmentId = fragments.get(1).getId();
        EditGoalImageDialogListener listener;
        listener = (EditGoalImageDialogListener) getFragmentManager().findFragmentById(fragmentId);
        listener.onFinishEditThisDialog();
        dismiss();
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.85), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri photoUri = data.getData();
                photoFile = new File(getRealPathFromURI(photoUri, context));
                Bitmap selectedImage = null;
                try {
                    selectedImage = MediaStore.Images.Media
                            .getBitmap(context.getContentResolver(), photoUri);
                    Bitmap resizedBitmap = BitmapScaler
                            .scaleToFill(selectedImage, 200, 200);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG,
                            40, bytes);
                    File resizedFile = getPhotoFileUri(
                            photoFileName + "_resized", context);
                    resizedFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(resizedFile);
                    fos.write(bytes.toByteArray());
                    fos.close();
                    photoFile = resizedFile;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ivPrevGoalImage.setImageBitmap(selectedImage);
            }
        }
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri takenPhotoUri = Uri.fromFile(getPhotoFileUri(photoFileName, context));
                Bitmap rotatedBitmap = rotateBitmapOrientation(takenPhotoUri.getPath());
                int width = rotatedBitmap.getWidth();
                int height = rotatedBitmap.getHeight();
                Bitmap resizedBitmap;
                if (width <= height) {
                    resizedBitmap = BitmapScaler.scaleToFitWidth(rotatedBitmap, 200);
                } else {
                    resizedBitmap = BitmapScaler.scaleToFitHeight(rotatedBitmap, 200);
                }
                Bitmap cropImg = Bitmap.createBitmap(resizedBitmap,
                        0, 0, 200, 200);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                cropImg.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                File resizedFile = getPhotoFileUri(photoFileName + "_resized", context);
                try {
                    resizedFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(resizedFile);
                    fos.write(bytes.toByteArray());
                    fos.close();
                    photoFile = resizedFile;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //ivGoalImage.setVisibility(View.VISIBLE);
                ivPrevGoalImage.setImageBitmap(cropImg);
                Log.d("ProfileImage", photoFile.getAbsolutePath());
            } else { // Result was a failure
                Toast.makeText(context, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Defines the listener interface
    public interface EditGoalImageDialogListener {
        void onFinishEditThisDialog();
    }

    public void requestPerms() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 0) {
            //testPost();
        }
    }

}
