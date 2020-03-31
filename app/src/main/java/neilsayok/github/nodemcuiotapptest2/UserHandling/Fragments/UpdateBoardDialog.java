package neilsayok.github.nodemcuiotapptest2.UserHandling.Fragments;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.amitshekhar.DebugDB;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sdsmdg.harjot.crollerTest.utilities.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;
import neilsayok.github.nodemcuiotapptest2.Room.Entities.Board;
import neilsayok.github.nodemcuiotapptest2.Extras.VolleySingleton;

import static android.app.Activity.RESULT_OK;

public class UpdateBoardDialog extends DialogFragment {

    private static final int CAMERA_REQUEST_CODE = 120;
    private static final int GALLERY_REQUEST_CODE = 121;
    private static boolean permissions = false;

    private View view;
    private Bundle bundle;
    private TextInputEditText boardNameEt;
    private Button cancelBT,addBT;
    private CircleImageView boardImg;
    private UpdateBoardDialog thisDialog;
    private BottomSheetDialog d;
    private MultiplePermissionsListener composite;
    private Uri uri;
    private File storageDir ;
    private File photoFile = null;
    private Board board;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_update_board, container, false);
        thisDialog = this;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity.getToolbar().setVisibility(View.VISIBLE);
        boardNameEt = view.findViewById(R.id.boardET);
        cancelBT = view.findViewById(R.id.cancelDialog);
        addBT = view.findViewById(R.id.addBoard);
        boardImg = view.findViewById(R.id.board_img);
        board = MainActivity.getAppDB().boardsDao().getBoardByID(getArguments().getInt("id"));

        Log.d("board url", String.valueOf(board.getImgUrl()==null)+"test");
        if(!board.getImgUrl().isEmpty()) {
           Picasso.get()
                    .load(board.getImgUrl())
                    .resize((int) Utils.convertDpToPixel(64, getContext()), (int) Utils.convertDpToPixel(64, getContext()))
                    .placeholder(R.drawable.ic_file_download_dark_24dp)
                    .error(R.drawable.ic_error_outline_red_24dp)
                    .into(boardImg);
        }
        else {
            boardImg.setImageResource(R.drawable.ic_switch);
        }

        DebugDB.getAddressLog();


        boardNameEt.setHint(board.getBoardName());
        boardImg.setOnClickListener(onClickListener);
        addBT.setOnClickListener(onClickListener);
        cancelBT.setOnClickListener(onClickListener);



    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog_Custom);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.cancelDialog:
                    thisDialog.dismiss();
                    break;
                case R.id.addBoard:
                    updateBoardToBD();
                    break;
                case R.id.board_img:
                    showDialog();
                    break;

            }

        }
    };

    private String imageToString(Uri imgUri) throws IOException {
        Bitmap bitmap;
        if (uri != null) {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imgUri);
        }else {
            Log.d("Return", "empty");
            return "";
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP,50,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        String s = Base64.encodeToString(imgBytes,Base64.DEFAULT);
        return s;
    }

    private boolean checkPermissions(){

        MultiplePermissionsListener dialogMultiplePermissionsListener =
                DialogOnAnyDeniedMultiplePermissionsListener.Builder
                        .withContext(getContext())
                        .withTitle("Camera & Storage permission")
                        .withMessage("Both camera and storage permission are needed.")
                        .withButtonText(android.R.string.ok)
                        .withIcon(R.drawable.ic_error_outline_red_24dp)
                        .build();

        MultiplePermissionsListener multiplePermissionsListener = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                permissions = true;
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        };

        composite = new CompositeMultiplePermissionsListener(multiplePermissionsListener,dialogMultiplePermissionsListener);
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(composite).check();
        return permissions;
    }

    private void showDialog(){
        Log.d("Img","Clicked");
        d = new BottomSheetDialog(getContext());
        d.setContentView(R.layout.bottomsheet_dialog_select_image);
        d.setTitle("");

        View.OnClickListener onClickListenerBottomsheet = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getId() == R.id.takepicbtn || v.getId()== R.id.takepicFab){
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePicture.resolveActivity(getContext().getPackageManager()) != null){

                        d.dismiss();
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                            ex.printStackTrace();
                            return;
                        }

                        if (photoFile != null){
                            uri = FileProvider.getUriForFile(getContext(),getContext().getPackageName()+".provider",photoFile);
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                            startActivityForResult(takePicture, CAMERA_REQUEST_CODE);

                        }


                    } else {
                        Toast.makeText(getContext(),"Please Install a Camera App",Toast.LENGTH_LONG).show();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=Camera&c=apps")));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=Camera&c=apps")));
                        }

                    }

                }else if(v.getId() == R.id.selectimgbtn || v.getId() == R.id.selectImageFab){
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    if (pickPhoto.resolveActivity(getContext().getPackageManager()) != null) {
                        startActivityForResult(pickPhoto, GALLERY_REQUEST_CODE);//one can be replaced with any action code
                        d.dismiss();
                    }
                    //Opens Play Store and search for a Camera app.
                    else {
                        Toast.makeText(getContext(),"Please Install a Gallery App",Toast.LENGTH_LONG).show();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=Gallery&c=apps")));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=Gallery&c=apps")));
                        }

                    }

                }

            }
        };




        if (checkPermissions()) {
            d.show();
            d.findViewById(R.id.takepicbtn).setOnClickListener(onClickListenerBottomsheet);
            d.findViewById(R.id.takepicFab).setOnClickListener(onClickListenerBottomsheet);
            d.findViewById(R.id.selectImageFab).setOnClickListener(onClickListenerBottomsheet);
            d.findViewById(R.id.selectimgbtn ).setOnClickListener(onClickListenerBottomsheet);
        }
        else {
            Log.d("checkPermissions", "False");
            checkPermissions();

        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName,".png",storageDir);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                boardImg.setImageURI(uri);
                boardImg.setClickable(true);
                boardImg.setFocusable(true);
                d.dismiss();
            }
            else {
                Toast.makeText(getContext(),"You Cancelled The Opperation",Toast.LENGTH_LONG).show();
                boardImg.setImageResource(R.drawable.ic_switch);
            }
        }
        else if (requestCode == GALLERY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                uri = data.getData();
                boardImg.setImageURI(uri);
                boardImg.setClickable(true);
                boardImg.setFocusable(true);
                d.dismiss();

            }else {
                Toast.makeText(getContext(),"You Cancelled The Opperation",Toast.LENGTH_LONG).show();
                boardImg.setImageResource(R.drawable.ic_switch);
            }
        }
    }

    private void updateBoardToBD(){
        view.findViewById(R.id.loadingLayout).setVisibility(View.VISIBLE);
        String url = getString(R.string.site_url)+"update_board_user.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                thisDialog.dismiss();
                view.findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                Log.d("Response",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("status_name")){
                        if (jsonObject.getString("status_name").equals("nameUpdated")){
                            if (boardNameEt.getText().toString().isEmpty()){
                                board.setBoardName(board.getBoardName());
                            }else{
                                board.setBoardName(boardNameEt.getText().toString());
                            }

                            MainActivity.getAppDB().boardsDao().updateBoard(board);
                            BoardsFragment.getBoardsAdapter().notifyDataSetChanged();
                        }else {
                            throw new IOException(response);
                        }
                    }else {
                        throw new IOException(response);
                    }
                    if (jsonObject.has("status_img")){
                        if (jsonObject.getString("status_img").equals("imageUpdated")){
                            board.setImgUrl(getString(R.string.site_url)+"img/user_img/board_image/" +
                                    jsonObject.getString("file_name"));
                            MainActivity.getAppDB().boardsDao().updateBoard(board);
                            BoardsFragment.getBoardsAdapter().notifyDataSetChanged();
                            BoardsFragment.getBoardsAdapter().notifyItemChanged(getArguments().getInt("position"));
                            BoardsFragment.getBoardsAdapter().updateImage(board);
                        }else {
                            throw new IOException(response);
                        }
                    }else {
                        throw new IOException(response);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                    view.findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                    addBT.setText("Retry");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                view.findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                addBT.setText("Retry");
                //thisDialog.dismiss();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if (MainActivity.getSharedPreferences().getString(getString(R.string.sharedPrefCtrl_table),"") == null)
                    Log.d("ctrltable","null");
                if (board.getBoardsTable()== null)
                    Log.d("board_table","null");
                params.put("ctrl_table",MainActivity.getSharedPreferences().getString(getString(R.string.sharedPrefCtrl_table),""));
                params.put("board_table",board.getBoardsTable());
                if (boardNameEt.getText().toString().isEmpty()){
                    params.put("board_name", board.getBoardName());
                    Log.d("board_name if", board.getBoardName());
                }else{
                    params.put("board_name", boardNameEt.getText().toString());
                    Log.d("board_name else",boardNameEt.getText().toString());
                }
                try {
                    if(uri != null)
                        params.put("board_img",imageToString(uri));
                    else
                        params.put("board_img","");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getmInstance(getContext()).addToRequestQue(stringRequest);


    }



}
