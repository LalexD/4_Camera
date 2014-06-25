package com.lad.camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraMainActivity extends Activity {
	
	
	private Uri imageUri;
	private static final int ACTIVITY_RESULT_CAMERA_CODE=100;
	private Intent intent;
	public ImageView imageView;
	private File photo;
	
		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_layout);
		imageView=(ImageView) findViewById(R.id.ViewPhoto);		
		intent=new Intent("android.media.action.IMAGE_CAPTURE");		
		try
		{
			photo=CreateImageFile();			
			
			imageUri=Uri.fromFile(photo);		
			Bitmap bitmap = BitmapFactory.decodeFile(photo.getAbsolutePath());
	        imageView.setImageBitmap(bitmap);
		}
		catch(IOException e)
		{
			Toast.makeText(this,"SD card not found!",5000).show();
			return;
		}
		
		
		
		
	}
	private File CreateImageFile() throws IOException{
		//save file in dir aplication
		File image=new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"CameraPictureCatch.jpg");		
		return image;		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		int id = item.getItemId();
		if (id == R.id.save_photo) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(R.string.title_save_dialog);
			alert.setMessage(R.string.message_save_dialog);		
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton(R.string.save_dialog, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				File dst=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
						input.getText().toString()+".jpg");				
					try {
						copyFile(photo,dst);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}			  
			  
			  }
			});

			alert.setNegativeButton(R.string.cancel_dialog, new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			   
			  }
			});
			alert.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	//start default camera
	public void onClickPhoto(View view){				
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent,ACTIVITY_RESULT_CAMERA_CODE);	    
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		
	   
	    try
	    {
	    	Bitmap bitmap = BitmapFactory.decodeFile(photo.getAbsolutePath());	        
	        Matrix m = new Matrix();
            m.postRotate( neededRotation());
            bitmap = Bitmap.createBitmap(bitmap,
                    0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    m, true);
            imageView.setImageBitmap(bitmap);
	    }
	    catch (Exception e)
	    {
	        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
	    }	    
	    
	}
	
	public int neededRotation()
    {
    try
        {

        ExifInterface exif = new ExifInterface(photo.getAbsolutePath());
        int orientation = exif.getAttributeInt(
           ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
            { return 270; }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
            { return 180; }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
            { return 90; }
        return 0;

        } catch (FileNotFoundException e)
        {
        e.printStackTrace();
        } catch (IOException e)
        {
        e.printStackTrace();
        }
    return 0;
    }
	
	public void copyFile(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);
	    
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
			

}
